package namit.retail_app.grocery.presentation.merchant

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import namit.retail_app.address.presentation.set_location.SetLocationDialog
import namit.retail_app.address.presentation.set_location.SetLocationViewModel
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.CouponModel
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.extension.intentToSettingAppPermission
import namit.retail_app.core.extension.intentToSettingLocation
import namit.retail_app.core.extension.loadImage
import namit.retail_app.core.extension.visible
import namit.retail_app.core.navigation.*
import namit.retail_app.core.presentation.adapter.ProductAdapter
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.presentation.base.BasePermissionFragment
import namit.retail_app.core.presentation.cart.CartViewModel
import namit.retail_app.core.presentation.dialog.alert.AlertMessageDialog
import namit.retail_app.core.presentation.dialog.delivery.DeliveryTimeDialog
import namit.retail_app.core.presentation.product_category.CategoryMerchantView
import namit.retail_app.core.presentation.product_detail.ProductDetailDialog
import namit.retail_app.core.presentation.viewmodel.CartFloatButtonViewModel
import namit.retail_app.core.presentation.widget.EndlessProductVerticalListListener
import namit.retail_app.core.utils.AppBarStateChangeListener
import namit.retail_app.core.utils.getWidthScreenSize
import namit.retail_app.coupon.presentation.coupon.CouponViewModel
import namit.retail_app.coupon.presentation.detail.CouponDetailDialogFragment
import namit.retail_app.coupon.presentation.dialog.CouponMerchantListDialogFragment
import namit.retail_app.grocery.R
import namit.retail_app.grocery.presentation.category_all.GroceryAllCategoryFragment
import namit.retail_app.grocery.presentation.category_all.GroceryAllCategoryViewModel
import namit.retail_app.grocery.presentation.category_sub.GrocerySubCategoryFragment
import namit.retail_app.grocery.presentation.category_sub_detail.GrocerySubCategoryDetailFragment
import namit.retail_app.grocery.presentation.main.GroceryMainFragment
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_grocery_merchant.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ObsoleteCoroutinesApi
class GroceryMerchantDetailFragment : BasePermissionFragment() {
    companion object {
        const val TAG = "GroceryMerchantDetailFragment"
        const val ARG_MERCHANT_DATA = "ARG_MERCHANT_DATA"
        fun getNewInstance(merchant: MerchantInfoItem): GroceryMerchantDetailFragment {
            val fragment = GroceryMerchantDetailFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_MERCHANT_DATA, merchant)
            }
            return fragment
        }
    }

    private val addressNavigator: AddressNavigator by inject()
    private val coreNavigator: CoreNavigator by inject()
    private val groceryNavigator: GroceryNavigator by inject()
    private val cartNavigator: CartNavigator by inject()
    private val couponNavigator: CouponNavigator by inject()

    private val viewModel: GroceryMerchantDetailViewModel by viewModel(parameters = {
        parametersOf(arguments?.getParcelable(ARG_MERCHANT_DATA))
    })
    private val categoryViewModel: GroceryAllCategoryViewModel by viewModel(parameters = {
        parametersOf(arguments?.getParcelable(ARG_MERCHANT_DATA))
    })
    private val locationViewModel: SetLocationViewModel by viewModel()
    private val floatCartViewModel: CartFloatButtonViewModel by viewModel()
    private val cartViewModel: CartViewModel by viewModel()
    private val couponViewModel: CouponViewModel by viewModel()

    private var appBarStateChangeListener: AppBarStateChangeListener? = null
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_grocery_merchant, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()
        initView()
        initListProductView()

        bindViewModel()
        bindCartViewModel()
        bindFloatCartViewModel()
        bindLocationViewModel()
        bindCategoryViewModel()
        bindCouponViewModel()

        requestUserLocation()
        viewModel.loadProductList()
        viewModel.loadDefaultDeliveryTimeSlot()
        viewModel.checkDeliveryAddress()
        categoryViewModel.loadAllCategoryList()
        floatCartViewModel.loadCartInfoFirstTime()
    }

    override fun onResume() {
        super.onResume()
        appBarLayout.addOnOffsetChangedListener(appBarStateChangeListener)
        backImageView.setColorFilter(Color.WHITE)
    }

    override fun onPause() {
        appBarLayout.removeOnOffsetChangedListener(appBarStateChangeListener)
        super.onPause()
    }

    private fun initToolbar() {
        (activity as? BaseActivity)?.apply {
            setSupportActionBar(toolbar)
            title = ""
            if (appBarStateChangeListener == null) {
                appBarStateChangeListener = object : AppBarStateChangeListener() {
                    override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                        when (state) {
                            State.COLLAPSED -> {
                                setShowProductName(isShow = true)
                            }

                            State.EXPANDED -> {
                                setShowProductName(isShow = false)
                            }
                            else -> return
                        }
                    }
                }
            } else {
                appBarLayout.removeOnOffsetChangedListener(appBarStateChangeListener)
            }

            backImageView.setOnClickListener {
                (activity as BaseActivity).onBackPressed()
            }
        }
    }

    private fun setShowProductName(isShow: Boolean) {
        if (isShow) {
            titleToolbar.text = viewModel.merchantInfo.value?.title
            backImageView.setColorFilter(
                ContextCompat.getColor(
                    activity!!,
                    R.color.trout
                )
            )
        } else {
            titleToolbar.text = ""
            backImageView.setColorFilter(Color.WHITE)
        }
    }

    private fun initView() {
        categoryView.setActionListener(object : CategoryMerchantView.OnActionListener {
            override fun onItemSelected(category: CategoryItem) {
                categoryViewModel.checkSubCategory(category)
            }

            override fun onSeeAllPressed() {
                categoryViewModel.presentAllCategory()
            }
        })

        couponBannerView.setOnClickListener {
            viewModel.presentCouponMerchantDialog()
        }

        searchView.apply {
            setHintContent(resources.getString(R.string.search_hint_general))
            enableSearch(enable = false)
            setOnClickListener {
                context?.let {
                    startActivity(
                        coreNavigator.openSearchActivity(
                            context = it,
                            merchantInfoItem = arguments!!.getParcelable(ARG_MERCHANT_DATA)!!
                        )
                    )
                }
            }
        }

        val scrollViewRect = Rect()
        groceryMerchantScrollView.getHitRect(scrollViewRect)
        groceryMerchantScrollView.setOnScrollChangeListener { _: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->
            if (searchView != null
                && searchView.getLocalVisibleRect(scrollViewRect)
            ) {
                searchToolbarImageView.visibility = View.INVISIBLE
            } else {
                searchToolbarImageView.visibility = View.VISIBLE
            }
        }

        headerLocationTimeView.onLocationClick = {
            val setLocationDialog = addressNavigator.getSetLocationDialog() as SetLocationDialog
            setLocationDialog.onSetLocationListener = {
                groceryMerchantScrollView.scrollTo(0, 0)
                updateDeliveryAddress(it)
                setLocationDialog.dismiss()
            }
            activity?.supportFragmentManager?.let { fragmentManager ->
                setLocationDialog.show(fragmentManager, SetLocationDialog.TAG)
            }
        }

        headerLocationTimeView.onTimeSlotClick = {
            viewModel.presentTimeSlotDialog()
        }

        //Cart Button
        floatCartButton.setOnClickListener {
            openCartPage()
        }

        //BackButton
        backImageView.setOnClickListener {
            (activity as? BaseActivity)?.onBackPressed()
        }
    }

    private fun initListProductView() {
        productAdapter = ProductAdapter().apply {
            val itemWidthScaleTypedValue = TypedValue()
            resources.getValue(
                R.dimen.itemProductVerticalWidthScale,
                itemWidthScaleTypedValue,
                true
            )
            val itemHeightScaleTypedValue = TypedValue()
            resources.getValue(
                R.dimen.itemProductVerticalHeightScale,
                itemHeightScaleTypedValue,
                true
            )
            val itemWidthSize = (getWidthScreenSize(context = context!!) * itemWidthScaleTypedValue.float).toInt()
            itemWidth = itemWidthSize
            itemHeight = (itemWidthSize * itemHeightScaleTypedValue.float).toInt()

            onSelectProduct = {
                viewModel.openProductDetailDialog(it)
            }

            addOneMore = { product, position ->
                cartViewModel.addOneProduct(product, position)
            }

            reduceOne = { product, position ->
                cartViewModel.reduceOneProduct(product, position)
            }
        }

        val productLayoutManager = GridLayoutManager(context, 2)
        productRecyclerView.apply {
            adapter = productAdapter
            layoutManager = productLayoutManager
            isNestedScrollingEnabled = false
        }

        groceryMerchantScrollView.setOnScrollChangeListener(object :
            EndlessProductVerticalListListener(productLayoutManager) {
            override fun onGoToBottomList() {
                viewModel.loadProductList()
            }
        })
    }

    private fun bindViewModel() {
        viewModel.merchantInfo.observe(viewLifecycleOwner, Observer {
            titleToolbar.text = it.title
            it.cover?.let { coverImageUrl ->
                bannerMerchantImageView.loadImage(coverImageUrl)
            }
        })

        viewModel.renderMerchantCoupon.observe(viewLifecycleOwner, Observer {
            couponViewModel.loadCouponListByMerchantId(merchantIds = it)
        })

        viewModel.productList.observe(viewLifecycleOwner, Observer {
            productAdapter.items = it!!.toMutableList()
        })

        viewModel.showHaveNoMoreProduct.observe(viewLifecycleOwner, Observer {
            if (it) {
                bottomMessageTextView.visibility = View.VISIBLE
                bottomMessageTextView.text = getString(R.string.no_more_data)
            } else {
                bottomMessageTextView.visibility = View.INVISIBLE
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

        viewModel.deliveryDateTime.observe(this, Observer {
            //check delivery now item or normal
            if (it.first) {
                headerLocationTimeView.setTimeSlot(resources.getString(R.string.delivery_now))
            } else {
                headerLocationTimeView.setTimeSlot(it.second)
            }
        })

        viewModel.openTimeSlotDialog.observe(this, Observer {
            activity?.supportFragmentManager?.let { fragmentManager ->
                (coreNavigator.getDeliveryTimeDialog(it.id) as DeliveryTimeDialog).apply {
                    onConfirm = {
                        viewModel.setSelectedTimeSlot(it)
                    }
                }.show(fragmentManager, DeliveryTimeDialog.TAG)
            }
        })

        viewModel.openCouponMerchantDialog.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let { supportFragmentManager ->
                (couponNavigator.openCouponList(
                    merchantInfoItemList = it
                ) as CouponMerchantListDialogFragment).apply {
                    onCouponSelected = { coupon ->
                        openCouponDetails(coupon)
                    }
                    onUseNowCouponClicked = { coupon ->
                        couponViewModel.saveSelectedCoupon(coupon)
                        openCartPage()
                    }
                }.show(supportFragmentManager, CouponMerchantListDialogFragment.TAG)
            }
        })
    }

    private fun bindCategoryViewModel() {
        categoryViewModel.categoryList.observe(viewLifecycleOwner, Observer {
            categoryView.visible()
            categoryView.categoryAdapter?.items = it
        })

        categoryViewModel.openAllCategory.observe(viewLifecycleOwner, Observer {
            (activity as BaseActivity).apply {
                val groceryCategoryAllFragment =
                    groceryNavigator.getGroceryCategoryAllFragment(
                        it.first
                    ) as GroceryAllCategoryFragment
                addFragment(
                    fragment = groceryCategoryAllFragment,
                    tag = GroceryAllCategoryFragment.TAG,
                    addToBackStack = true
                )
            }
        })

        categoryViewModel.openSubCategory.observe(viewLifecycleOwner, Observer {
            (activity as BaseActivity).apply {
                val groceryCategorySubFragment =
                    groceryNavigator.getGrocerySubCategoryFragment(
                        merchantInfoItem = arguments!!.getParcelable(ARG_MERCHANT_DATA)!!,
                        parentCategory = it.first,
                        childCategoryList = it.second
                    ) as GrocerySubCategoryFragment
                addFragment(
                    fragment = groceryCategorySubFragment,
                    tag = GroceryAllCategoryFragment.TAG,
                    addToBackStack = true
                )
            }
        })

        categoryViewModel.openSubCategoryDetail.observe(viewLifecycleOwner, Observer {
            (activity as BaseActivity).apply {
                val subCategoryDetailFragment =
                    groceryNavigator.getGrocerySubCategoryDetailFragment(
                        merchantInfoItem = arguments!!.getParcelable(ARG_MERCHANT_DATA)!!,
                        selectedCategory = it,
                        parentCategory = it
                    ) as GrocerySubCategoryDetailFragment
                addFragment(
                    fragment = subCategoryDetailFragment,
                    tag = GroceryAllCategoryFragment.TAG,
                    addToBackStack = true
                )
            }
        })
    }

    private fun bindFloatCartViewModel() {
        //Cart Handling
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
    }

    private fun bindCartViewModel() {
        cartViewModel.addProductSuccess.observe(viewLifecycleOwner, Observer {
            if (it.first) {
                viewModel.addProduct(it.second)
                floatCartViewModel.refreshCartInfo()
            }
        })

        cartViewModel.reduceOneProductSuccess.observe(viewLifecycleOwner, Observer {
            if (it.first) {
                viewModel.reduceProductInCart(it.second)
                floatCartViewModel.refreshCartInfo()
            }
        })

        cartViewModel.deleteProductInCartSuccess.observe(viewLifecycleOwner, Observer {
            if (it.first) {
                viewModel.removeProductInCart(it.second)
                floatCartViewModel.refreshCartInfo()
            }
        })
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
                        activity?.intentToSettingLocation(requestCode = GroceryMainFragment.REQUEST_CODE_SETTING_LOCATION)
                    }
                }.show(it, AlertMessageDialog.TAG)
            }
        })

        locationViewModel.syncMap.observe(viewLifecycleOwner, Observer {
            locationViewModel.getSelectedDeliveryAddress()
        })
    }

    private fun bindCouponViewModel() {
        couponViewModel.totalOfCoupon.observe(viewLifecycleOwner, Observer {
            couponBannerView.setTotalCoupons(it)
        })

        couponViewModel.showEmpty.observe(viewLifecycleOwner, Observer { isShow ->
            couponBannerView.visibility = if (isShow) View.GONE else View.VISIBLE
        })
    }

    private fun updateDeliveryAddress(addressModel: AddressModel) {
        headerLocationTimeView.setLocationTitle(
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
        )
    }

    private fun requestUserLocation() {
        requestAllowPermission(ACCESS_FINE_LOCATION, onResult = {
            locationViewModel.checkLocationPermission(permissionResult = it)
        })
    }

    private fun openCouponDetails(couponModel: CouponModel) {
        activity?.supportFragmentManager?.let { supportFragmentManager ->
            val couponDialog =
                couponNavigator.openCouponDetail(couponModel = couponModel) as CouponDetailDialogFragment
            couponDialog.onUseNowClick = { useNowCoupon ->
                couponViewModel.saveSelectedCoupon(useNowCoupon)
                openCartPage()
            }
            couponDialog.show(supportFragmentManager, CouponDetailDialogFragment.TAG)
        }
    }

    private fun openCartPage() {
        activity?.let {
            startActivity(cartNavigator.getCartActivity(it))
        }
    }
}