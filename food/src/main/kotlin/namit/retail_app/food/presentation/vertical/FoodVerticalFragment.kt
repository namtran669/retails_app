package namit.retail_app.food.presentation.vertical

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import namit.retail_app.address.presentation.set_location.SetLocationDialog
import namit.retail_app.address.presentation.set_location.SetLocationViewModel
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.extension.gone
import namit.retail_app.core.extension.intentToSettingAppPermission
import namit.retail_app.core.extension.intentToSettingLocation
import namit.retail_app.core.extension.visible
import namit.retail_app.core.navigation.AddressNavigator
import namit.retail_app.core.navigation.CoreNavigator
import namit.retail_app.core.navigation.CouponNavigator
import namit.retail_app.core.navigation.FoodNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.presentation.base.BasePermissionFragment
import namit.retail_app.core.presentation.dialog.alert.AlertMessageDialog
import namit.retail_app.core.presentation.widget.DeliveryToolbar
import namit.retail_app.core.presentation.widget.decoration.VerticalSpaceItemDecoration
import namit.retail_app.core.utils.getWidthScreenSize
import namit.retail_app.coupon.presentation.adapter.CouponHorizontalListAdapter
import namit.retail_app.coupon.presentation.coupon.CouponViewModel
import namit.retail_app.coupon.presentation.detail.CouponDetailDialogFragment
import namit.retail_app.food.R
import namit.retail_app.food.presentation.restaurant.RestaurantDetailFragment
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import kotlinx.android.synthetic.main.fragment_food_vertical.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FoodVerticalFragment : BasePermissionFragment() {

    companion object {
        val TAG = FoodVerticalFragment::class.java.simpleName
        const val REQUEST_CODE_SETTING_LOCATION = 4001
        fun getNewInstance(): FoodVerticalFragment =
            FoodVerticalFragment()
    }

    private val viewModel: FoodVerticalViewModel by viewModel()
    private val couponViewModel: CouponViewModel by viewModel()
    private val locationViewModel: SetLocationViewModel by viewModel(parameters = {
        parametersOf(arguments?.getParcelable(SetLocationDialog.UPDATE_ADDRESS_MODEL))
    })

    private val addressNavigator: AddressNavigator by inject()
    private val foodNavigator: FoodNavigator by inject()
    private val coreNavigator: CoreNavigator by inject()
    private val couponNavigator: CouponNavigator by inject()

    private lateinit var couponHorizontalListAdapter: CouponHorizontalListAdapter
    private lateinit var restaurantAdapter: RestaurantAdapter

    private var couponsSkeletonScreen: SkeletonScreen? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_food_vertical, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewModel()
        requestUserLocation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SETTING_LOCATION) {
            requestUserLocation()
        }
    }

    private fun initView() {
        //Search bar
        searchView.apply {
            setHintContent(resources.getString(namit.retail_app.grocery.R.string.search_hint_general))
            enableSearch(enable = false)
            setOnClickListener {
                context?.let {
//                    startActivity(coreNavigator.openSearchActivity(it))
                }
            }
        }

        //Title toolbar
        //todo hard code address
        deliveryToolbarView.apply {
            setScreenTitle(resources.getString(R.string.food_main_title_toolbar))
            setActionListener(object : DeliveryToolbar.ActionListener {
                override fun onBackPress() {
                    (activity as BaseActivity).onBackPressed()
                }

                override fun onLocationIconPress() {
                    openSetLocationDialog()
                }

                override fun onSetLocationDataPress() {
                    //do nothing
                }
            })
        }

        //Address bar
        val scrollViewRect = Rect()
        foodVerticalNestedScrollView.getHitRect(scrollViewRect)
        foodVerticalNestedScrollView.setOnScrollChangeListener { _: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->
            if (addressBarClickView != null
                && addressBarClickView.getLocalVisibleRect(scrollViewRect)
            ) {
                deliveryToolbarView.toggleLocationIcon(true)
            } else {
                deliveryToolbarView.toggleLocationIcon(false)
            }
        }
        addressBarClickView.setOnClickListener {
            openSetLocationDialog()
        }

        couponHorizontalListAdapter = CouponHorizontalListAdapter().apply {
            context?.let { context ->
                val itemWidthSize = (getWidthScreenSize(context = context) * 0.55).toInt()
                itemWidth = itemWidthSize
                itemHeight = (itemWidthSize * 0.58).toInt()
            }
            onCouponClick = {
                activity?.supportFragmentManager?.let { supportFragmentManager ->
                    val couponDialog =
                        couponNavigator.openCouponDetail(couponModel = it) as CouponDetailDialogFragment
                    couponDialog.show(supportFragmentManager, CouponDetailDialogFragment.TAG)
                }
            }
        }

        couponRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = couponHorizontalListAdapter
            isNestedScrollingEnabled = false
        }

        //Restaurant list
        initMerchantListView()

    }

    private fun openSetLocationDialog() {
        val setLocationDialog = addressNavigator.getSetLocationDialog() as SetLocationDialog
        setLocationDialog.onSetLocationListener = {
            foodVerticalNestedScrollView.scrollTo(0, 0)
            updateDeliveryAddress(it)
            setLocationDialog.dismiss()
        }
        activity?.supportFragmentManager?.let { fragmentManager ->
            setLocationDialog.show(fragmentManager, SetLocationDialog.TAG)
        }
    }

    private fun initMerchantListView() {
        val restaurantListDecoration =
            VerticalSpaceItemDecoration(
                startEndSpace = resources.getDimension(R.dimen.contentCardMarginStartEnd).toInt(),
                betweenSpace = resources.getDimension(R.dimen.contentCardMarginBetween).toInt(),
                bottomSpace = resources.getDimension(R.dimen.contentCardMarginTopBottom).toInt()
            )

        restaurantAdapter = RestaurantAdapter().apply {
            onSelectItem = {
                val fragment =
                    foodNavigator.getRestaurantDetailFragment(it) as RestaurantDetailFragment
                (activity as BaseActivity).addFragment(
                    fragment = fragment,
                    addToBackStack = true,
                    tag = RestaurantDetailFragment.TAG
                )
            }
        }

        restaurantRecyclerView.apply {
            adapter = restaurantAdapter
            isNestedScrollingEnabled = false
            addItemDecoration(restaurantListDecoration)
        }
    }

    private fun bindViewModel() {
        bindRestaurantViewModel()
        bindCouponViewModel()
        bindLocationViewModel()
    }

    private fun bindLocationViewModel() {
        locationViewModel.currentDeliveryAddress.observe(viewLifecycleOwner, Observer {
            updateDeliveryAddress(it)
        })

        locationViewModel.alertOpenPermissionSetting.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let {
                coreNavigator.alertQuestionDialog(
                    title = getString(namit.retail_app.grocery.R.string.hello),
                    message = getString(namit.retail_app.grocery.R.string.alert_message_open_permission_location),
                    positiveButtonText = getString(namit.retail_app.grocery.R.string.setting),
                    negativeButtonText = getString(namit.retail_app.grocery.R.string.dismiss)
                ).apply {
                    onPositionClick = {
                        context?.intentToSettingAppPermission()
                    }
                }.show(it, AlertMessageDialog.TAG)
            }
        })

        locationViewModel.alertOpenUserLocationSetting.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let {
                coreNavigator.alertQuestionDialog(
                    title = getString(namit.retail_app.grocery.R.string.hello),
                    message = getString(namit.retail_app.grocery.R.string.alert_message_current_location),
                    positiveButtonText = getString(namit.retail_app.grocery.R.string.setting),
                    negativeButtonText = getString(namit.retail_app.grocery.R.string.dismiss)
                ).apply {
                    onPositionClick = {
                        activity?.intentToSettingLocation(requestCode = REQUEST_CODE_SETTING_LOCATION)
                    }
                }.show(it, AlertMessageDialog.TAG)
            }
        })
    }

    private fun bindRestaurantViewModel() {
        viewModel.restaurantList.observe(viewLifecycleOwner, Observer {
            restaurantAdapter.items = it
        })
    }

    private fun bindCouponViewModel() {
        couponViewModel.couponList.observe(viewLifecycleOwner, Observer {
            couponHorizontalListAdapter.items = it
        })

        couponViewModel.isCouponListLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                couponHeaderTextView.gone()
                if (couponsSkeletonScreen != null) {
                    couponsSkeletonScreen?.show()
                } else {
                    couponsSkeletonScreen = Skeleton.bind(couponRecyclerView)
                        .adapter(couponHorizontalListAdapter)
                        .shimmer(false)
                        .frozen(false)
                        .count(1)
                        .load(R.layout.item_shelf_coupon_skeleton)
                        .show()
                }
            } else {
                couponHeaderTextView.visible()
                couponsSkeletonScreen?.hide()
            }
        })
    }

    private fun requestUserLocation() {
        requestAllowPermission(ACCESS_FINE_LOCATION, onResult = {
            locationViewModel.checkLocationPermission(permissionResult = it)
        })
    }

    private fun updateDeliveryAddress(addressModel: AddressModel) {
        viewModel.loadRestaurantList(addressModel.lat, addressModel.lng)
        addressFoodTextView.text =
            when {
                addressModel.name.isNullOrBlank().not() -> {
                    addressModel.name!!
                }
                addressModel.landMark.isNullOrBlank().not() -> {
                    addressModel.landMark!!
                }
                addressModel.address.isNullOrBlank().not() -> {
                    addressModel.address!!
                }
                else -> {
                    getString(namit.retail_app.cart.R.string.selected_location)
                }
            }
    }
}