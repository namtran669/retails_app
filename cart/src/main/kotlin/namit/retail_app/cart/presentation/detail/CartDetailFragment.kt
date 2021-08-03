 package namit.retail_app.cart.presentation.detail

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import namit.retail_app.address.presentation.edit.EditAddressBottomDialog
import namit.retail_app.address.presentation.manage.MyAddressDialogFragment
import namit.retail_app.cart.R
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.data.entity.CartMerchantModel
import namit.retail_app.core.data.entity.CouponModel
import namit.retail_app.core.data.entity.OrderModel
import namit.retail_app.core.extension.*
import namit.retail_app.core.navigation.*
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.core.presentation.dialog.alert.AlertMessageDialog
import namit.retail_app.core.presentation.dialog.alert.QuestionDialog
import namit.retail_app.core.presentation.dialog.delivery.DeliveryTimeDialog
import namit.retail_app.core.presentation.viewmodel.CartFloatButtonViewModel
import namit.retail_app.core.presentation.widget.LoadingDialog
import namit.retail_app.core.presentation.widget.decoration.VerticalSpaceItemDecoration
import namit.retail_app.coupon.presentation.coupon.CouponViewModel
import namit.retail_app.coupon.presentation.detail.CouponDetailDialogFragment
import namit.retail_app.coupon.presentation.dialog.CouponMerchantListDialogFragment
import namit.retail_app.coupon.presentation.promo_code.PromoCodeDialog
import namit.retail_app.order.presentation.tracking.TrackingOrderDialog
import namit.retail_app.payment.data.PaymentMethodModel
import namit.retail_app.payment.enums.CardType
import namit.retail_app.core.enums.PaymentType
import namit.retail_app.payment.presentation.payment.PaymentMethodListFragment
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import kotlinx.android.synthetic.main.fragment_cart_detail.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

@ObsoleteCoroutinesApi
class CartDetailFragment : BaseFragment() {
    companion object {
        val TAG: String = CartDetailFragment::class.java.simpleName

        //can not reference to TAG of merchant detail
        const val MERCHANT_DETAIL_TAG: String = "FromCartGroceryMerchantDetailFragment"
        private const val REQUEST_OPEN_LOGIN = 1994
        fun newInstance(): CartDetailFragment = CartDetailFragment()
    }

    private val viewModel: CartDetailViewModel by viewModel()
    private val floatCartViewModel: CartFloatButtonViewModel by viewModel()
    private val couponViewModel: CouponViewModel by viewModel()

    private val addressNavigator: AddressNavigator by inject()
    private val coreNavigator: CoreNavigator by inject()
    private val paymentNavigator: PaymentNavigator by inject()
    private val authNavigator: AuthNavigator by inject()
    private val orderNavigator: OrderNavigator by inject()
    private val groceryNavigator: GroceryNavigator by inject()
    private val couponNavigator: CouponNavigator by inject()

    private lateinit var cartMerchantAdapter: CartMerchantAdapter
    private var cartsSkeletonScreen: SkeletonScreen? = null
    private var loadingDialog: LoadingDialog? = null
    private var couponMerchantListDialog: CouponMerchantListDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_cart_detail, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindViewModel()
        bindCouponViewModel()

        viewModel.loadCartContent()
        viewModel.loadDeliveryAddress()
        viewModel.checkCurrentPaymentMethod()
        viewModel.loadDeliveryTimeSlot()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PaymentMethodListFragment.RESULT_CODE_SELECTED_PAYMENT_METHOD -> {
                    val paymentMethod =
                        data?.getParcelableExtra(PaymentMethodListFragment.EXTRA_SELECTED_PAYMENT_METHOD) as PaymentMethodModel
                    viewModel.updateSelectedPaymentMethod(paymentMethod)
                }
                REQUEST_OPEN_LOGIN -> {
                    viewModel.loadCartContent()
                }
            }
        }
    }

    private fun initView() {
        cartDetailSimpleToolbar.onBackPressed = {
            (activity as BaseActivity).finish()
        }

        cartMerchantAdapter = CartMerchantAdapter().apply {
            onCheckItemChange = {
                viewModel.setSelectedCartMerchant(it)
            }
            onClickAddItem = {
                viewModel.presentMerchantDetailScreen(it)
            }
            onAddOneItemChild = { parentIndex, childIndex ->
                viewModel.addOneItemProductOptimistic(parentIndex, childIndex)
            }
            onReduceOneItemChild = { parentIndex, childIndex ->
                viewModel.reduceOneItemProductOptimistic(parentIndex, childIndex)
            }
            onDeleteItemChild = { parentIndex, childIndex ->
                showConfirmDeleteProductDialog(parentIndex, childIndex)
            }
            onSwipeItemChild = { parentIndex, childIndex, isExpand ->
                viewModel.updateSwipeItem(parentIndex, childIndex, isExpand)
            }
        }
        cartMerchantRecyclerView.apply {
            isNestedScrollingEnabled = false
            adapter = cartMerchantAdapter
            addItemDecoration(
                VerticalSpaceItemDecoration(
                    startEndSpace = 0,
                    betweenSpace = resources.getDimension(R.dimen.productCartItemBottomSpace)
                        .toInt()
                )
            )
        }

        deliveryAddressLayout.setOnClickListener {
            viewModel.presentEditDeliveryAddressDialog()
        }

        changeAddressTextView.setOnClickListener {
            viewModel.presentMyAddressDialog()
        }

        timeSlotLayout.setOnClickListener {
            viewModel.presentTimeSlotDialog()
        }

        //Payment
        showAllProductButton.setOnClickListener {
            viewModel.presentPaymentMethodScreen()
        }
        changePaymentTextView.setOnClickListener {
            viewModel.presentPaymentMethodScreen()
        }

        //Place Order
        placeOrderBottomLayout.setOnClickListener {
            viewModel.createOrder()
        }

        //Location Time view
        locationTimeTopView.apply {
            onTimeSlotClick = {
                viewModel.presentTimeSlotDialog()
            }
            onLocationClick = {
                viewModel.presentEditDeliveryAddressDialog()
            }
        }
        val scrollViewRect = Rect()
        cartDetailLayout.getHitRect(scrollViewRect)
        cartDetailLayout.setOnScrollChangeListener { _: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->
            if (timeSlotTitleTextView != null
                && timeSlotTitleTextView.getLocalVisibleRect(scrollViewRect)
            ) {
                locationTimeTopView.gone()
            } else {
                locationTimeTopView.visible()
            }
        }

        //Empty View
        cartEmptyLayoutView.apply {
            setEmptyTitle(getString(R.string.empty_cart_title))
            setEmptyDetails(getString(R.string.empty_cart_description))
            setEmptyImage(R.drawable.img_empty_cart)
            onClickAction = {
                activity?.finish()
            }
        }

        //Coupon
        couponViewButton.setOnClickListener {
            viewModel.openCouponMerchantDialog()
        }

        clearCouponImageView.setOnClickListener {
            viewModel.clearCurrentPromoCode()
        }

        //Promo code
        enterPromoteCodeTextView.setOnClickListener {
            viewModel.presentPromoCodeDialog()
        }
    }


    private fun bindViewModel() {
        viewModel.renderCurrentPromoCode.observe(viewLifecycleOwner, Observer {
            currentPromoCodeTextView.text = it
        })

        viewModel.discount.observe(viewLifecycleOwner, Observer {
            discountTextView.text = "-${it.formatCurrency().toThaiCurrency()}"
        })

        viewModel.renderCampaignName.observe(viewLifecycleOwner, Observer {
            if (it.isNullOrBlank()) {
                campaignTitleTextView.gone()
            } else {
                campaignTitleTextView.visible()
                campaignTitleTextView.text = it
            }
        })

        viewModel.showCurrentPromoCode.observe(viewLifecycleOwner, Observer { isShow ->
            if (isShow) {
                currentPromoCodeTitleTextView.visibility = View.VISIBLE
                couponVerifiedImageView.visibility = View.VISIBLE
                currentPromoCodeTextView.visibility = View.VISIBLE
                clearCouponImageView.visibility = View.VISIBLE
                discountTitleTextView.visibility = View.VISIBLE
                discountTextView.visibility = View.VISIBLE
                campaignTitleTextView.visibility = View.VISIBLE
            } else {
                currentPromoCodeTitleTextView.visibility = View.GONE
                couponVerifiedImageView.visibility = View.GONE
                currentPromoCodeTextView.visibility = View.GONE
                clearCouponImageView.visibility = View.GONE
                discountTitleTextView.visibility = View.GONE
                discountTextView.visibility = View.GONE
                campaignTitleTextView.visibility = View.GONE
            }
        })

        viewModel.cartMerchantList.observe(viewLifecycleOwner, Observer {
            cartMerchantAdapter.items = it
        })

        viewModel.cartModelData.observe(viewLifecycleOwner, Observer {
            floatCartViewModel.notifyCartInfoChange(it)
        })

        viewModel.totalQuantity.observe(viewLifecycleOwner, Observer {
            productAmountTextView.text = it.toString()
        })

        viewModel.finalTotalPrice.observe(viewLifecycleOwner, Observer {
            it.formatCurrency().toThaiCurrency().apply {
                finalTotalButtonTextView.text = this
                finalTotalTextView.text = this
            }
        })

        viewModel.totalProductPrice.observe(viewLifecycleOwner, Observer {
            subTotalContentTextView.text =
                it.formatCurrency().toThaiCurrency()
        })

        viewModel.totalDeliveryFee.observe(viewLifecycleOwner, Observer {
            deliveryFeeContentTextView.text =
                it.formatCurrency().toThaiCurrency()
        })

        viewModel.isCartListLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                if (cartsSkeletonScreen != null) {
                    cartsSkeletonScreen?.show()
                } else {
                    cartsSkeletonScreen = Skeleton.bind(cartMerchantRecyclerView)
                        .adapter(cartMerchantAdapter)
                        .shimmer(false)
                        .frozen(false)
                        .count(1)
                        .load(R.layout.item_shelf_cart_merchant_skeleton)
                        .show()
                }
            } else {
                cartsSkeletonScreen?.hide()
            }
        })

        viewModel.deliveryAddress.observe(viewLifecycleOwner, Observer {
            handleAddressView(it)
        })

        viewModel.openEditAddressDialog.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let { fragmentManager ->
                (addressNavigator.getEditAddressDialog(
                    editAddressContent = it,
                    saveToDeliveryAddress = true
                ) as EditAddressBottomDialog).apply {
                    onDismissDialog = {
                        viewModel.loadDeliveryAddress()
                    }
                }.show(fragmentManager, MyAddressDialogFragment.TAG)
            }
        })

        viewModel.openMyAddressDialog.observe(viewLifecycleOwner, Observer {
            openChangeAddressDialog()
        })

        viewModel.deliveryDateTime.observe(viewLifecycleOwner, Observer {
            //check delivery now item or normal
            val deliveryTimeText = if (it.first) {
                resources.getString(R.string.delivery_now)
            } else {
                it.second
            }
            pickSlotTextView.text = deliveryTimeText
            locationTimeTopView.setTimeSlot(deliveryTimeText)
        })

        viewModel.openTimeSlotDialog.observe(viewLifecycleOwner, Observer {
            openTimeSlotDialog(it)
        })

        viewModel.openPromoCodeDialog.observe(viewLifecycleOwner, Observer { cartId ->
            activity?.supportFragmentManager?.let { fragmentManager ->
                (couponNavigator.openPromoCodeDialog(cartId) as PromoCodeDialog)
                    .apply {
                        onRedeemSuccess = {
                            viewModel.redeemCoupon(it)
                        }
                    }.show(fragmentManager, PromoCodeDialog.TAG)
            }
        })

        viewModel.selectedPaymentMethod.observe(viewLifecycleOwner, Observer {
            handlePaymentMethod(it)
        })

        viewModel.createOrderSuccess.observe(viewLifecycleOwner, Observer {
            floatCartViewModel.refreshCartInfo()
            openTrackingOrderDialog(order = it)
        })

        viewModel.openLoginScreen.observe(viewLifecycleOwner, Observer {
            activity?.let {
                startActivityForResult(authNavigator.openLogin(it), REQUEST_OPEN_LOGIN)
            }
        })

        viewModel.showLoadingDialog.observe(viewLifecycleOwner, Observer {
            showLoadingDialog(isShow = it)
        })

        viewModel.showOtherErrorMessage.observe(viewLifecycleOwner, Observer { message ->
            activity?.supportFragmentManager?.let {
                coreNavigator.alertMessageDialog(
                    getString(R.string.create_order_error_title),
                    message,
                    getString(R.string.ok)
                ).show(it, AlertMessageDialog.TAG)
            }
        })

        viewModel.cartListEmptyError.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let {
                coreNavigator.alertMessageDialog(
                    getString(R.string.create_order_error_title),
                    getString(R.string.create_order_error_cart_empty),
                    getString(R.string.ok)
                ).show(it, AlertMessageDialog.TAG)
            }
        })

        viewModel.missChooseCartError.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let {
                coreNavigator.alertMessageDialog(
                    getString(R.string.create_order_error_title),
                    getString(R.string.create_order_error_miss_cart),
                    getString(R.string.ok)
                ).show(it, AlertMessageDialog.TAG)
            }
        })

        viewModel.missDeliveryAddressError.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let {
                coreNavigator.alertMessageDialog(
                    getString(R.string.create_order_error_title),
                    getString(R.string.create_order_error_miss_address),
                    getString(R.string.select_new_address)
                ).apply {
                    onDismiss = {
                        openChangeAddressDialog()
                    }
                }.show(it, AlertMessageDialog.TAG)
            }
        })

        viewModel.missDeliveryTimeSlotError.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let { fragmentManager ->
                coreNavigator.alertMessageDialog(
                    getString(R.string.create_order_error_title),
                    getString(R.string.create_order_error_miss_time_slot),
                    getString(R.string.select_new_time)
                ).apply {
                    onDismiss = {
                        openTimeSlotDialog(it)
                    }
                }.show(fragmentManager, AlertMessageDialog.TAG)
            }
        })

        viewModel.missDeliveryPaymentError.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let {
                coreNavigator.alertMessageDialog(
                    getString(R.string.create_order_error_title),
                    getString(R.string.create_order_error_miss_payment),
                    getString(R.string.select_new_payment)
                ).apply {
                    onDismiss = {
                        showPaymentMethodSelection()
                    }
                }.show(it, AlertMessageDialog.TAG)
            }
        })

        viewModel.addDeliveryAddressDetailError.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let { fragmentManager ->
                (addressNavigator.getEditAddressDialog(
                    editAddressContent = it,
                    saveToDeliveryAddress = true
                ) as EditAddressBottomDialog).apply {
                    onDismissDialog = {
                        viewModel.loadDeliveryAddress()
                        viewModel.checkCreateOrderFromEditAddressDialog()
                    }
                }.show(fragmentManager, MyAddressDialogFragment.TAG)
            }
        })

        viewModel.openMerchantDetailScreen.observe(viewLifecycleOwner, Observer {
            (activity as BaseActivity).apply {
                supportFragmentManager.popBackStack(TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                addFragment(
                    fragment = groceryNavigator.getGroceryMerchantFragment(it),
                    tag = MERCHANT_DETAIL_TAG,
                    addToBackStack = true
                )
            }
        })

        viewModel.openPaymentMethodScreen.observe(viewLifecycleOwner, Observer {
            showPaymentMethodSelection()
        })

        viewModel.emptyCartMerchantItem.observe(viewLifecycleOwner, Observer {
            emptyConstraintLayout.visibleWhen(it)
            cartDetailSimpleToolbar.apply {
                if (it) {
                    setBackgroundColor(Color.WHITE)
                } else {
                    setBackgroundColor(Color.TRANSPARENT)
                }
            }
        })

        viewModel.renderCouponWithCart.observe(viewLifecycleOwner, Observer {
            couponViewModel.loadCouponListByCart(it)
        })

        viewModel.openCouponCartDialog.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let { supportFragmentManager ->
                couponMerchantListDialog?.dismiss()
                couponMerchantListDialog = couponNavigator.openCouponList(
                    cartId = it
                ) as CouponMerchantListDialogFragment
                couponMerchantListDialog?.onCouponSelected = {
                    openCouponDetails(couponModel = it)
                }
                couponMerchantListDialog?.onUseNowCouponClicked = {
                    couponMerchantListDialog?.dismiss()
                    viewModel.redeemCoupon(couponCode = it)
                }
                couponMerchantListDialog?.show(
                    supportFragmentManager,
                    CouponMerchantListDialogFragment.TAG
                )
            }
        })
    }

    private fun bindCouponViewModel() {
        couponViewModel.totalOfCoupon.observe(viewLifecycleOwner, Observer {
            couponAvailableTextView.text =
                resources.getString(
                    namit.retail_app.coupon.R.string.you_have_x_coupon_available, it.toString()
                )
        })

        couponViewModel.showEmpty.observe(viewLifecycleOwner, Observer { isShow ->
            if (isShow) {
                couponImageView.visibility = View.GONE
                couponAvailableTextView.visibility = View.GONE
                couponViewButton.visibility = View.GONE
            } else {
                couponImageView.visibility = View.VISIBLE
                couponAvailableTextView.visibility = View.VISIBLE
                couponViewButton.visibility = View.VISIBLE
            }
        })
    }

    private fun openTimeSlotDialog(cart: CartMerchantModel) {
        activity?.supportFragmentManager?.let { fragmentManager ->
            (coreNavigator.getDeliveryTimeDialog(cart.merchantId) as DeliveryTimeDialog).apply {
                onConfirm = {
                    viewModel.setSelectedTimeSlot(it)
                }
            }.show(fragmentManager, DeliveryTimeDialog.TAG)
        }
    }

    private fun openChangeAddressDialog() {
        activity?.supportFragmentManager?.let {
            (addressNavigator.getMyAddressDialog() as MyAddressDialogFragment).apply {
                onDismissDialog = {
                    viewModel.loadDeliveryAddress()
                }
            }.show(it, MyAddressDialogFragment.TAG)
        }
    }

    private fun handlePaymentMethod(method: PaymentMethodModel) {
        method.let {
            paymentSelectMethodLayout.gone()
            paymentCurrentMethodLayout.visible()

            titleTextView.text = it.title
            descTextView.apply {
                if (it.description.isNullOrBlank().not()) {
                    descTextView.visible()
                    descTextView.text = it.description
                } else {
                    descTextView.gone()
                }
            }
            when (it.type) {
                PaymentType.TRUE_MONEY -> {
                    namit.retail_app.payment.R.drawable.ic_payment_true_money
                }
                PaymentType.CASH -> {
                    namit.retail_app.payment.R.drawable.ic_payment_cash
                }
                else -> {
                    when (it.cardType) {
                        CardType.VISA -> {
                            namit.retail_app.payment.R.drawable.ic_visa_credit_card
                        }
                        CardType.MASTER_CARD -> {
                            namit.retail_app.payment.R.drawable.ic_master_credit_card
                        }
                        CardType.JCB -> {
                            namit.retail_app.payment.R.drawable.ic_jcb_credit_card
                        }
                        else -> {
                            null
                        }
                    }
                }
            }?.let {
                paymentImageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        paymentImageView.context,
                        it
                    )
                )
            }

        }
    }

    private fun handleAddressView(addressData: AddressModel) {
        val addressTitle = if (addressData.name.isNullOrBlank()) {
            resources.getString(namit.retail_app.address.R.string.home)
        } else {
            addressData.name!!
        }
        addressLabelTextView.text = addressTitle
        locationTimeTopView.setLocationTitle(addressTitle)

        addressTitleTextView.setContentGoneWhenBlank(addressData.address)
        addressDetailTextView.setContentGoneWhenBlank(addressData.addressDetail)
        addressNoteTextView.setContentGoneWhenBlank(addressData.note)
        addressPhoneTextView.setContentGoneWhenBlank(addressData.phone)
    }

    private fun showConfirmDeleteProductDialog(parentIndex: Int, childIndex: Int) {
        activity?.supportFragmentManager?.let {
            coreNavigator.alertQuestionDialog(
                title = getString(R.string.are_you_sure_you_want_to_remove_this_product),
                message = getString(R.string.this_address_will_be_permanently_deleted_from_your_saved_address_list),
                positiveButtonText = getString(R.string.yes),
                negativeButtonText = getString(R.string.cancel)
            ).apply {
                onPositionClick = {
                    viewModel.deleteItemProduct(parentIndex, childIndex)
                }
            }.show(it, QuestionDialog.TAG)
        }
    }

    private fun showPaymentMethodSelection() {
        context?.let {
            startActivityForResult(
                paymentNavigator.openPaymentWithPaymentList(context = it),
                PaymentMethodListFragment.RESULT_CODE_SELECTED_PAYMENT_METHOD
            )
        }
    }

    private fun openTrackingOrderDialog(order: OrderModel) {
        activity?.supportFragmentManager?.let {
            (orderNavigator.getTrackingOrderDialog(orderData = order) as TrackingOrderDialog)
                .apply {
                    onDismissDialog = {
                        activity?.finish()
                    }
                }.show(it, TrackingOrderDialog.TAG)
        }
    }

    private fun openCouponDetails(couponModel: CouponModel) {
        activity?.supportFragmentManager?.let { supportFragmentManager ->
            val couponDialog =
                couponNavigator.openCouponDetail(couponModel = couponModel) as CouponDetailDialogFragment
            couponDialog.onUseNowClick = { coupon ->
                couponMerchantListDialog?.dismiss()
                viewModel.redeemCoupon(couponCode = coupon)
            }
            couponDialog.show(supportFragmentManager, CouponDetailDialogFragment.TAG)
        }
    }

    private fun showLoadingDialog(isShow: Boolean) {
        fragmentManager?.let { fragmentManager ->
            if (isShow) {
                loadingDialog?.dismiss()
                loadingDialog?.show(fragmentManager, LoadingDialog.TAG) ?: kotlin.run {
                    loadingDialog = coreNavigator.getLoadingDialog(haveBlurBackground = true)
                    loadingDialog!!.show(fragmentManager, LoadingDialog.TAG)
                }
            } else {
                loadingDialog?.dismiss()
            }
        }
    }
}