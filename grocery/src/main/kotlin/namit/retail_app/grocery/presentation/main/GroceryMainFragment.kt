package namit.retail_app.grocery.presentation.main

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import namit.retail_app.address.presentation.set_location.SetLocationDialog
import namit.retail_app.address.presentation.set_location.SetLocationViewModel
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.extension.intentToSettingAppPermission
import namit.retail_app.core.extension.intentToSettingLocation
import namit.retail_app.core.navigation.*
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.presentation.base.BasePermissionFragment
import namit.retail_app.core.presentation.cart.CartViewModel
import namit.retail_app.core.presentation.dialog.alert.AlertMessageDialog
import namit.retail_app.core.presentation.product_detail.ProductDetailDialog
import namit.retail_app.core.presentation.viewmodel.CartFloatButtonViewModel
import namit.retail_app.core.presentation.widget.DeliveryToolbar
import namit.retail_app.core.presentation.widget.decoration.HorizontalSpaceItemDecoration
import namit.retail_app.core.utils.getWidthScreenSize
import namit.retail_app.coupon.data.repository.CouponRepositoryImpl.Companion.VERTICAL_SUPERMARKET
import namit.retail_app.coupon.presentation.adapter.CouponHorizontalListAdapter
import namit.retail_app.coupon.presentation.coupon.CouponViewModel
import namit.retail_app.coupon.presentation.detail.CouponDetailDialogFragment
import namit.retail_app.grocery.R
import namit.retail_app.grocery.presentation.merchant.GroceryMerchantDetailFragment
import namit.retail_app.story.presentation.adapter.StoryAdapter
import namit.retail_app.story.presentation.details.StoryDetailDialogFragment
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import kotlinx.android.synthetic.main.fragment_grocery_main.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

@ObsoleteCoroutinesApi
class GroceryMainFragment : BasePermissionFragment() {

    companion object {
        const val TAG = "GroceryMainFragment"
        const val REQUEST_CODE_SETTING_LOCATION = 1001
        fun getNewInstance(): GroceryMainFragment = GroceryMainFragment()
    }

    private val viewModel: GroceryMainViewModel by viewModel()
    private val couponViewModel: CouponViewModel by viewModel()
    private val locationViewModel: SetLocationViewModel by viewModel()
    private val floatCartViewModel: CartFloatButtonViewModel by viewModel()
    private val cartViewModel: CartViewModel by viewModel()

    private val storyNavigator: StoryNavigator by inject()
    private val couponNavigator: CouponNavigator by inject()
    private val groceryNavigator: GroceryNavigator by inject()
    private val cartNavigator: CartNavigator by inject()
    private val addressNavigator: AddressNavigator by inject()
    private val coreNavigator: CoreNavigator by inject()

    private lateinit var merchantListAdapter: MerchantListAdapter
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var couponHorizontalListAdapter: CouponHorizontalListAdapter
    private lateinit var groceryProductAdapter: GroceryProductAdapter

    private var productsSkeletonScreen: SkeletonScreen? = null
    private var merchantsSkeletonScreen: SkeletonScreen? = null
    private var couponsSkeletonScreen: SkeletonScreen? = null
    private var storiesSkeletonScreen: SkeletonScreen? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_grocery_main, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewModel()
        viewModel.loadStoryList()
        couponViewModel.loadCouponListByVertical(vertical = VERTICAL_SUPERMARKET)
        floatCartViewModel.loadCartInfoFirstTime()
        locationViewModel.checkLocationPermission()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SETTING_LOCATION) {
            requestUserLocation()
        }
    }

    private fun initView() {
        //Title toolbar
        deliveryToolbarView.setScreenTitle(
            activity?.resources?.getString(R.string.grocery_main_title_toolbar) ?: ""
        )
        deliveryToolbarView.setActionListener(object : DeliveryToolbar.ActionListener {
            override fun onBackPress() {
                (activity as BaseActivity).onBackPressed()
            }

            override fun onLocationIconPress() {
                viewModel.showSetLocationDialog()
            }

            override fun onSetLocationDataPress() {
                //do nothing
            }
        })

        //Address bar
        val scrollViewRect = Rect()
        merchantVerticalNestedScrollView.getHitRect(scrollViewRect)
        merchantVerticalNestedScrollView.setOnScrollChangeListener { _: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->
            if (addressBarClickView != null
                && addressBarClickView.getLocalVisibleRect(scrollViewRect)) {
                deliveryToolbarView.setScreenTitleTextSize(30F)
                deliveryToolbarView.toggleLocationIcon(true)
            } else {
                deliveryToolbarView.setScreenTitleTextSize(26F)
                deliveryToolbarView.toggleLocationIcon(false)
            }
        }

        addressBarClickView.setOnClickListener {
            viewModel.showSetLocationDialog()
        }

        storyAdapter = StoryAdapter()
        storyAdapter.onItemClick = { story ->
            activity?.supportFragmentManager?.let { fragmentManager ->
                val storyDetailDialog = storyNavigator.getStoryDetail(baseStoryContent = story)
                storyDetailDialog.show(fragmentManager, StoryDetailDialogFragment.TAG)
            }
        }
        storyRecyclerView.apply {
            adapter = storyAdapter
            isNestedScrollingEnabled = false
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
                    couponDialog.onUseNowClick = { useNowCoupon ->
                        couponViewModel.saveSelectedCoupon(couponModel = useNowCoupon)
                        openCart()
                    }
                    couponDialog.show(supportFragmentManager, CouponDetailDialogFragment.TAG)
                }
            }
        }

        couponRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = couponHorizontalListAdapter
            isNestedScrollingEnabled = false
        }

        groceryProductAdapter = GroceryProductAdapter().apply {
            val itemWidthScaleTypedValue = TypedValue()
            resources.getValue(
                R.dimen.itemProductHorizontalWidthScale,
                itemWidthScaleTypedValue,
                true
            )
            val itemHeightScaleTypedValue = TypedValue()
            resources.getValue(
                R.dimen.itemProductHorizontalHeightScale,
                itemHeightScaleTypedValue,
                true
            )
            val itemWidthSize = (getWidthScreenSize(context = context!!) *
                    itemWidthScaleTypedValue.float).toInt()
            productItemWidth = itemWidthSize
            productItemHeight = (itemWidthSize * itemHeightScaleTypedValue.float).toInt()

            onClickSeeAll = {
                viewModel.showAllMerchantProduct(it)
            }

            onSelectChildProduct = {
                viewModel.showProductDetailDialog(it)
            }

            addProductToCart = { categoryPosition, product, position ->
                cartViewModel.addOneProduct(categoryPosition, product, position)
            }

            addOneMoreProduct = { categoryPosition, product, position ->
                cartViewModel.addOneProduct(categoryPosition, product, position)
            }

            reduceOneProduct = { categoryPosition, product, position ->
                cartViewModel.reduceOneProduct(categoryPosition, product, position)
            }
        }

        groceryRecyclerView.apply {
            adapter = groceryProductAdapter
            isNestedScrollingEnabled = false
        }

        //Merchant Name List
        initMerchantListView()

        //Cart Button
        floatCartButton.setOnClickListener {
            openCart()
        }
    }

    private fun initMerchantListView() {
        val merchantListDecoration =
            HorizontalSpaceItemDecoration(
                startEndSpace = resources.getDimension(R.dimen.contentCardMarginStartEnd).toInt(),
                betweenSpace = resources.getDimension(R.dimen.contentCardMarginBetween).toInt(),
                topBottomSpace = resources.getDimension(R.dimen.contentCardMarginTopBottom).toInt()
            )

        merchantListAdapter = MerchantListAdapter()
        merchantListAdapter.setActionListener(object : MerchantListAdapter.OnActionListener {
            override fun onItemSelect(merchant: MerchantInfoItem) {
                (activity as? BaseActivity)?.apply {
                    val merchantFragment =
                        groceryNavigator.getGroceryMerchantFragment(merchant) as GroceryMerchantDetailFragment
                    addFragment(
                        fragment = merchantFragment,
                        tag = GroceryMerchantDetailFragment.TAG,
                        addToBackStack = true
                    )
                }
            }
        })

        with(merchantNameRecyclerView) {
            adapter = merchantListAdapter
            isNestedScrollingEnabled = false
            addItemDecoration(merchantListDecoration)
        }
    }

    private fun bindViewModel() {
        bindMerchantViewModel()
        bindStoryViewModel()
        bindCouponViewModel()
        bindProductViewModel()
        bindCartViewModel()
        bindLocationViewModel()
    }

    private fun bindLocationViewModel() {
        locationViewModel.currentDeliveryAddress.observe(viewLifecycleOwner, Observer {
            updateDeliveryAddress(it)
        })

        locationViewModel.alertOpenPermissionSetting.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let {
                coreNavigator.alertQuestionDialog(
                    title = getString(R.string.hello),
                    message = getString(R.string.alert_message_open_permission_location),
                    positiveButtonText = getString(R.string.setting),
                    negativeButtonText = getString(R.string.dismiss)
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
                    title = getString(R.string.hello),
                    message = getString(R.string.alert_message_current_location),
                    positiveButtonText = getString(R.string.setting),
                    negativeButtonText = getString(R.string.dismiss)
                ).apply {
                    onPositionClick = {
                        activity?.intentToSettingLocation(requestCode = REQUEST_CODE_SETTING_LOCATION)
                    }
                }.show(it, AlertMessageDialog.TAG)
            }
        })

        locationViewModel.alertRequestLocationPermission.observe(viewLifecycleOwner, Observer {
            requestUserLocation()
        })

        locationViewModel.syncMap.observe(viewLifecycleOwner, Observer {
            locationViewModel.getSelectedDeliveryAddress()
        })
    }

    private fun bindMerchantViewModel() {
        viewModel.merchantNameList.observe(viewLifecycleOwner, Observer {
            merchantListAdapter.items = it
        })

        viewModel.isMerchantListLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                if (merchantsSkeletonScreen != null) {
                    merchantsSkeletonScreen?.show()
                } else {
                    merchantsSkeletonScreen = Skeleton.bind(merchantNameRecyclerView)
                        .adapter(merchantListAdapter)
                        .shimmer(false)
                        .frozen(false)
                        .count(1)
                        .load(R.layout.view_merchant_skeleton)
                        .show()
                }
            } else {
                merchantsSkeletonScreen?.hide()
            }
        })

        viewModel.shouldShowSetLocationDialog.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let { fragmentManager ->
                val setLocationDialog = addressNavigator.getSetLocationDialog() as SetLocationDialog
                setLocationDialog.onSetLocationListener = {
                    merchantVerticalNestedScrollView.scrollTo(0, 0)
                    updateDeliveryAddress(it)
                    setLocationDialog.dismiss()
                }
                setLocationDialog.show(fragmentManager, SetLocationDialog.TAG)
            }
        })
    }

    private fun bindStoryViewModel() {
        viewModel.storyShelfList.observe(viewLifecycleOwner, Observer {
            storyAdapter.items = it
        })

        viewModel.isStoryListLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                if (storiesSkeletonScreen != null) {
                    storiesSkeletonScreen?.show()
                } else {
                    storiesSkeletonScreen = Skeleton.bind(storyRecyclerView)
                        .adapter(storyAdapter)
                        .shimmer(false)
                        .frozen(false)
                        .count(1)
                        .load(R.layout.item_shelf_story_skeleton)
                        .show()
                }
            } else {
                storiesSkeletonScreen?.hide()
            }
        })
    }

    private fun bindCouponViewModel() {
        couponViewModel.couponList.observe(viewLifecycleOwner, Observer {
            couponHorizontalListAdapter.items = it
        })

        couponViewModel.isCouponListLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
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
                couponsSkeletonScreen?.hide()
            }
        })

        couponViewModel.showEmpty.observe(viewLifecycleOwner, Observer { isShow ->
            if (isShow) {
                couponHeaderTextView.visibility = View.GONE
                couponRecyclerView.visibility = View.GONE
            } else {
                couponHeaderTextView.visibility = View.VISIBLE
                couponRecyclerView.visibility = View.VISIBLE
            }
        })
    }

    private fun bindProductViewModel() {
        viewModel.featuredProductCategoryList.observe(viewLifecycleOwner, Observer {
            groceryProductAdapter.items = it
        })

        viewModel.isFeaturedProductListLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                if (productsSkeletonScreen != null) {
                    productsSkeletonScreen?.show()
                } else {
                    productsSkeletonScreen = Skeleton.bind(groceryRecyclerView)
                        .adapter(groceryProductAdapter)
                        .shimmer(false)
                        .frozen(false)
                        .count(1)
                        .load(R.layout.item_shelf_product_skeleton)
                        .show()
                }
            } else {
                productsSkeletonScreen?.hide()
            }
        })

        viewModel.openMerchantDetail.observe(viewLifecycleOwner, Observer {
            (activity as? BaseActivity)?.apply {
                val merchantFragment =
                    groceryNavigator.getGroceryMerchantFragment(it) as GroceryMerchantDetailFragment
                addFragment(
                    fragment = merchantFragment,
                    tag = GroceryMerchantDetailFragment.TAG,
                    addToBackStack = true
                )
            }
        })

        viewModel.openProductDetail.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let { fragmentManager ->
                coreNavigator.getProductDetailDialog(product = it.first, merchantData = it.second)
                    .apply {
                        onAddProductToCartSuccess = {
                            floatCartViewModel.refreshCartInfo()
                        }
                    }.show(fragmentManager, ProductDetailDialog.TAG)
            }
        })
    }

    private fun bindCartViewModel() {
        floatCartViewModel.cartInfo.observe(viewLifecycleOwner, Observer {
            floatCartButton.setCartValue(it.totalPrice, it.amount)
        })

        floatCartViewModel.isCartLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                floatCartButton.showSkeleton()
            } else {
                floatCartButton.hideSkeleton()
            }
        })

        cartViewModel.addProductInCategorySuccess.observe(viewLifecycleOwner, Observer {
            if (it.first) {
                viewModel.addProduct(it.second, it.third)
                floatCartViewModel.refreshCartInfo()
            }
        })

        cartViewModel.reduceOneProductInCategorySuccess.observe(viewLifecycleOwner, Observer {
            if (it.first) {
                viewModel.reduceProductInCart(it.second, it.third)
                floatCartViewModel.refreshCartInfo()
            }
        })

        cartViewModel.deleteProductCategoryInCartSuccess.observe(viewLifecycleOwner, Observer {
            if (it.first) {
                viewModel.deleteProductInCart(it.second, it.third)
                floatCartViewModel.refreshCartInfo()
            }
        })
    }

    private fun requestUserLocation() {
        requestAllowPermission(ACCESS_FINE_LOCATION, onResult = {
            locationViewModel.checkLocationPermission(permissionResult = it)
        })
    }

    private fun updateDeliveryAddress(addressModel: AddressModel) {
        viewModel.loadMerchantList(addressModel.lat, addressModel.lng)
        addressGroceryTextView.text =
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

    private fun openCart() {
        activity?.let {
            val intent = cartNavigator.getCartActivity(it)
            startActivity(intent)
        }
    }
}