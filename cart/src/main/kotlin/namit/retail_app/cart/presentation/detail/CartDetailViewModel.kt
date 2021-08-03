package namit.retail_app.cart.presentation.detail

import androidx.lifecycle.MutableLiveData
import namit.retail_app.address.domain.GetDeliveryAddressUseCase
import namit.retail_app.address.domain.GetUserAddressListUseCase
import namit.retail_app.address.domain.SaveDeliveryAddressUseCase
import namit.retail_app.core.data.entity.*
import namit.retail_app.core.domain.*
import namit.retail_app.core.domain.RedeemCartUseCaseImpl.Companion.ERROR_CAMPAIGN_QUOTA_EXCEED
import namit.retail_app.core.enums.CouponType
import namit.retail_app.core.enums.PaymentType
import namit.retail_app.core.extension.*
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.tracking.TrackingValue
import namit.retail_app.core.utils.CartUtils
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.coupon.domain.GetSelectedCouponUseCase
import namit.retail_app.coupon.domain.RemoveSelectedCouponUseCase
import namit.retail_app.coupon.domain.SaveSelectedCouponUseCase
import namit.retail_app.order.domain.CreateOrderUseCase
import namit.retail_app.order.domain.CreateOrderUseCaseImpl.Companion.ERROR_CANNOT_CREATE_ORDER_WITH_TIME_SLOT
import namit.retail_app.payment.data.PaymentMethodModel
import namit.retail_app.payment.domain.GetDeliveryPaymentUseCase
import kotlinx.coroutines.*

class CartDetailViewModel(
    private val getDeliveryAddressUseCase: GetDeliveryAddressUseCase,
    private val getCurrentDeliveryTimeSlotDataUseCase: GetCurrentDeliveryTimeSlotDataUseCase,
    private val getSelectedDeliveryTimeUseCase: GetSelectedDeliveryTimeUseCase,
    private val getCartListUseCase: GetCartInfoUseCase,
    private val getUuidUseCase: GetUuidUseCase,
    private val getMerchantByStoreIdUseCase: GetMerchantByStoreIdUseCase,
    private val addProductToCartInCartUserCase: AddProductToCartUseCase,
    private val reduceOneProductInCartUserCase: ReduceOneProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val getDeliveryPaymentUseCase: GetDeliveryPaymentUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val getUserProfileLocalUseCase: GetUserProfileLocalUseCase,
    private val getDeliveryFeeUseCase: GetDeliveryFeeUseCase,
    private val getUserAddressListUseCase: GetUserAddressListUseCase,
    private val saveDeliveryAddressUseCase: SaveDeliveryAddressUseCase,
    private val redeemCartUseCase: RedeemCartUseCase,
    private val getSelectedCouponUseCase: GetSelectedCouponUseCase,
    private val saveSelectedCouponUseCase: SaveSelectedCouponUseCase,
    private val removeSelectedCouponUseCase: RemoveSelectedCouponUseCase,
    private val eventTrackingManager: EventTrackingManager,
    private val saveDeliveryTimeUseCase: SaveDeliveryTimeUseCase
) : BaseViewModel() {

    val cartMerchantList = MutableLiveData<List<CartMerchantModel>>()
    val cartModelData = MutableLiveData<CartModel>()
    val isCartListLoading = MutableLiveData<Boolean>()
    val deliveryAddress = MutableLiveData<AddressModel>()
    val deliveryDateTime = MutableLiveData<Pair<Boolean, String>>()
    val totalQuantity = MutableLiveData<Int>()
    val totalProductPrice = MutableLiveData<Double>()
    val finalTotalPrice = MutableLiveData<Double>()
    val totalDeliveryFee = MutableLiveData<Double>()
    val selectedPaymentMethod = MutableLiveData<PaymentMethodModel>()
    val discount = MutableLiveData<Double>()
    val emptyCartMerchantItem = MutableLiveData<Boolean>()
    val renderCouponWithCart = MutableLiveData<Int>()
    val renderCurrentPromoCode = MutableLiveData<String>()
    val showCurrentPromoCode = MutableLiveData<Boolean>()
    val renderCampaignName = MutableLiveData<String>()

    val openTimeSlotDialog = SingleLiveEvent<CartMerchantModel>()
    val openEditAddressDialog = SingleLiveEvent<AddressModel>()
    val openMyAddressDialog = SingleLiveEvent<Unit>()
    val openPaymentMethodScreen = SingleLiveEvent<Unit>()
    val openPromoCodeDialog = SingleLiveEvent<Int>()
    val createOrderSuccess = SingleLiveEvent<OrderModel>()
    val openLoginScreen = SingleLiveEvent<Unit>()
    val missDeliveryAddressError = SingleLiveEvent<Unit>()
    val addDeliveryAddressDetailError = SingleLiveEvent<AddressModel>()
    val missDeliveryTimeSlotError = SingleLiveEvent<CartMerchantModel>()
    val missDeliveryPaymentError = SingleLiveEvent<Unit>()
    val missChooseCartError = SingleLiveEvent<Unit>()
    val cartListEmptyError = SingleLiveEvent<Unit>()
    val showOtherErrorMessage = SingleLiveEvent<String>()
    val openMerchantDetailScreen = SingleLiveEvent<MerchantInfoItem>()
    val openCouponCartDialog = SingleLiveEvent<Int>()
    val showLoadingDialog = SingleLiveEvent<Boolean>()

    private var selectedTimeSlotData: TimeSlot? = null
    private var selectedAddressData: AddressModel? = null
    private var selectedPaymentData: PaymentMethodModel? = null
    private var cartMerchantListData = listOf<CartMerchantModel>()
    private var selectedCart: CartMerchantModel? = null
    private var selectedCoupon: CouponModel? = null

    init {
        getSelectedCoupon()
    }

    fun loadCartContent() {
        launch {
            isCartListLoading.value = true
            //Get UUID
            val uuidResult = withContext(Dispatchers.IO) { getUuidUseCase.execute() }
            if (uuidResult is UseCaseResult.Success) {
                //Load Cart data
                val cartsResult =
                    getCartListUseCase.execute(uuidResult.data!!, 0)
                if (cartsResult is UseCaseResult.Success) {
                    val newCartMerchantListData = cartsResult.data!!.merchants.toMutableList()

                    //Sort cart item and product inside (asc)
                    newCartMerchantListData.sortCartAndProduct()

                    //Mapping merchant info by store id
                    newCartMerchantListData.mapMerchantByStoreId()

                    //Remove cart belong unknown merchant
                    cartMerchantListData = removeCartUnknownMerchant(newCartMerchantListData)

                    //Mapping delivery fee by cart id
                    cartMerchantListData.mapDeliveryFeeByCartId()

                    if (cartMerchantListData.isNotEmpty()) {
                        //preselected the first item
                        if (selectedCoupon != null) {
                            val selectedCouponMerchantId = selectedCoupon?.merchantInfoItem?.id!!
                            val isMachWithSelectedCoupon = cartMerchantListData.firstOrNull {
                                it.merchantId == selectedCouponMerchantId
                            }

                            if (isMachWithSelectedCoupon != null) {
                                setSelectedCartMerchant(selectedCouponMerchantId)
                            } else {
                                setSelectedCartMerchant(0)
                            }
                        } else {
                            setSelectedCartMerchant(0)
                        }

                        loadDeliveryTimeSlot()
                        cartModelData.value = cartsResult.data!!
                        trackCartView(cartMerchantList = cartMerchantListData)
                    }
                } else {
                    cartMerchantListData = listOf()
                    renderMerchantList()
                }
            }
            isCartListLoading.value = false
        }
    }

    fun openCouponMerchantDialog() {
        selectedCart?.let {
            openCouponCartDialog.value = it.id
        }
    }

    fun redeemCoupon(couponCode: CouponModel) {
        saveSelectedCouponUseCase.execute(couponCode)
        selectedCart?.id?.let { cartId ->
            launch {
                showLoadingDialog.value = true
                val redeemResult =
                    redeemCartUseCase.execute(cartId = cartId, couponCode = couponCode.code!!)
                if (redeemResult is UseCaseResult.Success) {
                    selectedCoupon = couponCode

                    trackRedeemCoupon(
                        couponType = redeemResult.data!!.couponType,
                        amountDiscount = redeemResult.data!!.discount,
                        couponFormat = TrackingValue.VALUE_CAMPAIGN_COLLECTABLE
                    )

                    saveSelectedCouponUseCase.execute(couponCode)
                    renderCurrentPromoCode.value = couponCode.code
                    renderCampaignName.value = couponCode.name ?: ""
                    showCurrentPromoCode.value = true
                    val redeem = redeemResult.data!!
                    if (redeem.couponType == CouponType.FIXED || redeem.couponType == CouponType.PERCENTAGE) {
                        renderTotalQuantityPrice(selectedCart!!, redeem.discount)
                        renderTotalDeliveryFee(selectedCart!!)
                    } else if (redeem.couponType == CouponType.DELIVERY_FEE) {
                        renderTotalQuantityPrice(selectedCart!!, selectedCart!!.deliveryFee)
                        renderTotalDeliveryFee(selectedCart!!, selectedCart!!.deliveryFee)
                    }
                } else if (redeemResult is UseCaseResult.Error) {
                    clearCurrentPromoCode()
                    showOtherErrorMessage.value = redeemResult.exception.message
                    showCurrentPromoCode.value = false
                }
                showLoadingDialog.value = false
            }
        }
    }

    fun redeemCoupon(redeemCart: RedeemCart) {
        trackRedeemCoupon(
            couponType = redeemCart.couponType,
            amountDiscount = redeemCart.discount,
            couponFormat = TrackingValue.VALUE_CAMPAIGN_PROMO_CODE
        )

        selectedCoupon = CouponModel(code = redeemCart.code).apply {
            merchantInfoItem = MerchantInfoItem(id = redeemCart.merchantId)
        }

        selectedCart?.let {
            renderCurrentPromoCode.value = redeemCart.code ?: ""
            renderCampaignName.value = redeemCart.code ?: ""
            renderCampaignName.value = ""
            showCurrentPromoCode.value = true
            if (redeemCart.couponType == CouponType.FIXED || redeemCart.couponType == CouponType.PERCENTAGE) {
                renderTotalQuantityPrice(it, redeemCart.discount)
                renderTotalDeliveryFee(it)
            } else if (redeemCart.couponType == CouponType.DELIVERY_FEE) {
                renderTotalQuantityPrice(it, it.deliveryFee)
                renderTotalDeliveryFee(it, it.deliveryFee)
            }
        }
    }

    fun clearCurrentPromoCode() {
        removeSelectedCouponUseCase.execute()
        selectedCoupon = null
        selectedCart?.let {
            renderTotalQuantityPrice(it)
            renderTotalDeliveryFee(it)
            renderMerchantList()
            loadCouponAvailable()
            applySelectedCouponToCart()
        }
        showCurrentPromoCode.value = false
    }

    private fun refreshCartContent() {
        launch {
            val uuidResult = withContext(Dispatchers.IO) { getUuidUseCase.execute() }
            if (uuidResult is UseCaseResult.Success) {

                //Load Cart data
                val cartsResult =
                    getCartListUseCase.execute(uuidResult.data!!, 0)
                if (cartsResult is UseCaseResult.Success) {
                    val newCartMerchantListData = cartsResult.data!!.merchants.toMutableList()

                    //Sort cart item and product inside (asc)
                    newCartMerchantListData.sortCartAndProduct()

                    //Mapping merchant info by store id
                    newCartMerchantListData.mapMerchantByStoreId()

                    //Remove cart belong unknown merchant
                    val merchantSelectedId =
                        cartMerchantListData.firstOrNull { it.isSelected }?.merchantId
                    cartMerchantListData = removeCartUnknownMerchant(newCartMerchantListData)

                    //Mapping delivery fee by cart id
                    cartMerchantListData.mapDeliveryFeeByCartId()

                    if (cartMerchantListData.isNotEmpty()) {
                        //preselected the first item
                        selectedCart = null
                        merchantSelectedId?.let {
                            loadCouponAvailable()
                            setSelectedCartMerchant(it)
                        }

                        renderMerchantList()

                        cartModelData.value = cartsResult.data!!
                        trackCartView(cartMerchantList = cartMerchantListData)
                    }
                } else {
                    cartMerchantListData = listOf()
                    renderMerchantList()

                    cartModelData.value = CartModel()
                    resetTotalQuantityPrice()
                }
            }
        }
    }

    private fun loadCouponAvailable() {
        selectedCart?.let {
            renderCouponWithCart.value = it.id
        }
    }

    private suspend fun List<CartMerchantModel>.mapDeliveryFeeByCartId() {
        val feeDeferredList =
            mutableListOf<Pair<CartMerchantModel, Deferred<UseCaseResult<Double>>>>()
        this.forEach {
            val feeDeferred = async {
                getDeliveryFeeUseCase.execute(it.id)
            }
            feeDeferredList.add(Pair(it, feeDeferred))
        }
        feeDeferredList.forEach {
            val feeResult = it.second.await()
            if (feeResult is UseCaseResult.Success) {
                it.first.deliveryFee = feeResult.data!!
            }
        }
    }

    private suspend fun List<CartMerchantModel>.mapMerchantByStoreId() {
        val merchantDeferredList =
            mutableListOf<Pair<CartMerchantModel, Deferred<UseCaseResult<MerchantInfoItem>>>>()
        this.forEach {
            val merchantDeferred = async {
                getMerchantByStoreIdUseCase.execute(it.merchantId)
            }
            merchantDeferredList.add(Pair(it, merchantDeferred))
        }
        merchantDeferredList.forEach {
            val merchantDeferred = it.second.await()
            if (merchantDeferred is UseCaseResult.Success) {
                it.first.merchant = merchantDeferred.data!!
            }
        }
    }

    private fun MutableList<CartMerchantModel>.sortCartAndProduct() {
        sortBy { it.id }
        forEach { merchant -> merchant.products.sortBy { it.id } }
    }

    private fun removeCartUnknownMerchant(listInput: MutableList<CartMerchantModel>): List<CartMerchantModel> {
        return listInput.filter { it.merchant != null }
    }

    private fun renderMerchantList() {
        emptyCartMerchantItem.value = cartMerchantListData.isEmpty()
        cartMerchantList.value = cartMerchantListData
    }

    fun loadDeliveryAddress() {
        val deliveryDataResult = getDeliveryAddressUseCase.execute()
        if (deliveryDataResult is UseCaseResult.Success) {
            selectedAddressData = deliveryDataResult.data!!
            deliveryAddress.value = selectedAddressData
        }
    }

    fun checkCreateOrderFromEditAddressDialog() {
        if (selectedAddressData != null && selectedAddressData!!.id > 0) {
            createOrder()
        }
    }

    fun presentEditDeliveryAddressDialog() {
        val userResult = getUserProfileLocalUseCase.execute()
        if (userResult is UseCaseResult.Success && userResult.data!!.id > 0) {
            deliveryAddress.value?.let {
                if (it.phone.isNullOrBlank()) {
                    it.phone = userResult.data!!.mobile.removeNationNumber()
                }
                openEditAddressDialog.value = it
            } ?: run {
                openMyAddressDialog.call()
            }
        } else {
            openLoginScreen.call()
        }
    }

    fun presentMyAddressDialog() {
        val userResult = getUserProfileLocalUseCase.execute()
        if (userResult is UseCaseResult.Success && userResult.data!!.id > 0) {
            openMyAddressDialog.call()
        } else {
            openLoginScreen.call()
        }
    }

    fun presentPaymentMethodScreen() {
        val userResult = getUserProfileLocalUseCase.execute()
        if (userResult is UseCaseResult.Success && userResult.data!!.id > 0) {
            openPaymentMethodScreen.call()
        } else {
            openLoginScreen.call()
        }
    }

    fun presentPromoCodeDialog() {
        selectedCart?.let {
            openPromoCodeDialog.value = it.id
        }
    }

    fun loadDeliveryTimeSlot() {
        if (selectedTimeSlotData != null) {
            return
        }

        selectedCart?.merchantId?.let {
            val deliveryTimeSelected = getSelectedDeliveryTimeUseCase.execute(it)
            if (deliveryTimeSelected is UseCaseResult.Success) {
                setSelectedTimeSlot(deliveryTimeSelected.data!!)
            } else {
                fetchCurrentDeliveryTimeSlotFromApi()
            }
        } ?: kotlin.run {
            fetchCurrentDeliveryTimeSlotFromApi()
        }
    }

    private fun fetchCurrentDeliveryTimeSlotFromApi() {
        selectedCart?.let {
            launch {
                val getDeliveryTime = getCurrentDeliveryTimeSlotDataUseCase.execute(
                    merchantId = it.merchantId
                )
                if (getDeliveryTime is UseCaseResult.Success) {
                    val timeSlotList = getDeliveryTime.data!!.deliveryDates
                    day_loop@ for (day in timeSlotList) {
                        if (day.isDeliveryNow && day.isSelected) {
                            setSelectedTimeSlot(timeSlot = day)
                            break
                        } else {
                            for (time in day.slots) {
                                if (day.isSelected && time.isSelected) {
                                    setSelectedTimeSlot(timeSlot = day)
                                    break@day_loop
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun setSelectedTimeSlot(timeSlot: TimeSlot) {
        selectedTimeSlotData = timeSlot
        selectedCart?.merchantId?.let { merchantId ->
            saveDeliveryTimeUseCase.execute(merchantId, timeSlot)
            if (timeSlot.isDeliveryNow.not()) {
                val currentDate = timeSlot.date.convertToDate(format = DATE_TIME_FORMAT_YYYY_MM_DD)
                val selectedTime = timeSlot.slots.first { it.isSelected }

                var dayMonthTxt = ""
                if (!currentDate.checkTodayTomorrowTime()) {
                    dayMonthTxt = ". ${currentDate.convertToString(DATE_TIME_FORMAT_MMM_DD)}"
                }

                deliveryDateTime.value = Pair(
                    first = false,
                    second = "${currentDate.convertTodayTomorrowTime(DAY_OF_WEEK_SHORT)}${dayMonthTxt}, ${selectedTime.hour.replace(
                        " ",
                        ""
                    )}"
                )

            } else {
                deliveryDateTime.value = Pair(first = true, second = "")
            }
        }
    }

    fun presentTimeSlotDialog() {
        selectedCart?.let {
            openTimeSlotDialog.value = it
        }
    }

    fun setSelectedCartMerchant(indexChecked: Int) {
        if (selectedCart != null && selectedCart!!.id == cartMerchantListData[indexChecked].id) {
            cartMerchantListData[indexChecked].isSelected = false
            selectedCart = null
            resetTotalQuantityPrice()
        } else {
            cartMerchantListData.forEachIndexed { index, data ->
                data.isSelected = indexChecked == index
            }
            selectedCart = cartMerchantListData[indexChecked]
            selectedCart?.let {
                renderTotalQuantityPrice(it)
                renderTotalDeliveryFee(it)
            }
        }
        renderMerchantList()
        loadCouponAvailable()
        applySelectedCouponToCart()
        selectedTimeSlotData = null
        loadDeliveryTimeSlot()
    }

    private fun applySelectedCouponToCart() {
        if (selectedCoupon != null
            && selectedCart != null
            && selectedCoupon?.merchantInfoItem?.id == selectedCart?.merchantId
        ) {
            redeemCoupon(selectedCoupon!!)
        } else {
            selectedCoupon = null
            showCurrentPromoCode.value = false
        }
    }

    private fun setSelectedCartMerchant(selectedMerchantId: String) {
        cartMerchantListData.forEach {
            if (it.merchantId == selectedMerchantId) {
                it.isSelected = true
                selectedCart = it
            } else {
                it.isSelected = false
            }
        }

        selectedCart?.let {
            renderTotalQuantityPrice(it)
            renderTotalDeliveryFee(it)
            renderMerchantList()
            loadCouponAvailable()
            applySelectedCouponToCart()
            selectedTimeSlotData = null
            loadDeliveryTimeSlot()
        }
    }

    private fun renderTotalQuantityPrice(merchant: CartMerchantModel, discountPrice: Double = 0.0) {
        val quantityPriceProductPair =
            CartUtils.calculateQuantityPriceMerchant(merchant)
        //todo TMP
        totalQuantity.value = quantityPriceProductPair.first
        totalProductPrice.value = quantityPriceProductPair.second
        if (discountPrice > 0) {
            discount.value = discountPrice
        }

        finalTotalPrice.value =
            quantityPriceProductPair.second + merchant.deliveryFee - discountPrice
    }

    private fun renderTotalDeliveryFee(
        merchant: CartMerchantModel,
        discountDelivery: Double = 0.0
    ) {
        if (discountDelivery > 0) {
            discount.value = discountDelivery
        }
        totalDeliveryFee.value = merchant.deliveryFee - discountDelivery
    }

    private fun resetTotalQuantityPrice() {
        //todo TMP
        totalQuantity.value = 0
        totalProductPrice.value = 0.0
        finalTotalPrice.value = 0.0
        totalDeliveryFee.value = 0.0
    }

    //todo try new solution
    fun addOneItemProductOptimistic(parentIndex: Int, childIndex: Int) {
        val merchant = cartMerchantListData[parentIndex]
        val productItem = merchant.products[childIndex]

        productItem.quantity += 1
        cartModelData.value = CartUtils.convertToCartModel(cartMerchantListData)
        renderMerchantList()

        if (selectedCoupon == null) {
            renderTotalQuantityPrice(selectedCart!!)
            renderTotalDeliveryFee(selectedCart!!)
        }

        launch {
            val secureIdResult = withContext(Dispatchers.IO) { getUuidUseCase.execute() }
            if (secureIdResult is UseCaseResult.Success) {
                productItem.product?.let { product ->
                    trackAddProduct(productData = product, quantity = productItem.quantity)
                }

                val result = addProductToCartInCartUserCase.execute(
                    product = productItem.product!!,
                    secureId = secureIdResult.data!!,
                    quantity = 1
                )
                if (result is UseCaseResult.Success) {
                    getCartDeliveryFee(merchant)
                    selectedCoupon?.let {
                        redeemCoupon(it)
                    }
                } else {
                    refreshCartContent()
                }
            }
        }
    }

    //todo try new solution
    fun reduceOneItemProductOptimistic(parentIndex: Int, childIndex: Int) {
        val merchant = cartMerchantListData[parentIndex]
        val productItem = merchant.products[childIndex]

        if (productItem.quantity > 1) {

            productItem.quantity -= 1
            cartModelData.value = CartUtils.convertToCartModel(cartMerchantListData)
            renderMerchantList()

            if (selectedCoupon == null) {
                renderTotalQuantityPrice(selectedCart!!)
                renderTotalDeliveryFee(selectedCart!!)
            }

            launch {
                val secureIdResult = withContext(Dispatchers.IO) { getUuidUseCase.execute() }
                if (secureIdResult is UseCaseResult.Success) {
                    val result = reduceOneProductInCartUserCase.execute(
                        productItem.product!!,
                        secureIdResult.data!!
                    )
                    if (result is UseCaseResult.Success) {
                        getCartDeliveryFee(merchant)
                        selectedCoupon?.let {
                            redeemCoupon(it)
                        }
                    } else {
                        refreshCartContent()
                    }
                }
            }
        } else {
            updateSwipeItem(
                parentSwipeIndex = parentIndex,
                childSwipeIndex = childIndex,
                isExpand = true
            )
        }
    }

    private suspend fun getCartDeliveryFee(cartMerchant: CartMerchantModel) {
        val deliveryFeeResult = getDeliveryFeeUseCase.execute(cartMerchant.id)
        if (deliveryFeeResult is UseCaseResult.Success) {
            cartMerchant.deliveryFee = deliveryFeeResult.data!!
            renderMerchantList()
        }
    }

    fun deleteItemProduct(parentIndex: Int, childIndex: Int) {
        val merchant = cartMerchantListData[parentIndex]
        val productItem = merchant.products[childIndex]
        launch {
            val secureIdResult = withContext(Dispatchers.IO) { getUuidUseCase.execute() }
            if (secureIdResult is UseCaseResult.Success) {
                val result = deleteProductUseCase.execute(
                    productItem.id,
                    secureIdResult.data!!
                )
                if (result is UseCaseResult.Success) {
                    refreshCartContent()
                }
            }
        }
    }

    fun updateSwipeItem(parentSwipeIndex: Int, childSwipeIndex: Int, isExpand: Boolean) {
        val merchantSwipe = cartMerchantListData[parentSwipeIndex]
        if (isExpand) {
            cartMerchantListData.forEachIndexed { merchantIndex, merchant ->
                merchant.products.forEachIndexed { productIndex, product ->
                    product.isSwiped =
                        (merchantIndex == parentSwipeIndex && childSwipeIndex == productIndex)
                }
            }

            renderMerchantList()
        } else {
            merchantSwipe.products[childSwipeIndex].isSwiped = isExpand
        }
    }

    fun updateSelectedPaymentMethod(data: PaymentMethodModel) {
        selectedPaymentData = data
        selectedPaymentMethod.value = selectedPaymentData
    }

    fun checkCurrentPaymentMethod() {
        val currentPayment = getDeliveryPaymentUseCase.execute()
        if (currentPayment is UseCaseResult.Success) {
            updateSelectedPaymentMethod(currentPayment.data!!)
        }
    }

    fun createOrder() {
        launch {
            val userResult = getUserProfileLocalUseCase.execute()
            if (userResult is UseCaseResult.Success && userResult.data!!.id > 0) {
                when {
                    cartMerchantListData.isEmpty() -> {
                        cartListEmptyError.call()
                    }
                    selectedCart == null -> {
                        missChooseCartError.call()
                    }
                    selectedAddressData == null -> {
                        missDeliveryAddressError.call()
                    }
                    selectedAddressData!!.id <= 0 -> {
                        checkAddressDetailInfo(userResult.data!!)
                        return@launch
                    }
                    selectedTimeSlotData == null -> {
                        missDeliveryTimeSlotError.value = selectedCart
                    }
                    selectedPaymentData == null -> {
                        missDeliveryPaymentError.call()
                    }
                    else -> {
                        showLoadingDialog.value = true
                        performCreateOrder(userResult.data!!)
                    }
                }
            } else {
                openLoginScreen.call()
            }
            showLoadingDialog.value = false
        }
    }

    private suspend fun checkAddressDetailInfo(userModel: UserModel) {
        var isMapSuccess = false
        val addressListResult = getUserAddressListUseCase.execute(userModel.id)
        if (addressListResult is UseCaseResult.Success) {
            for (item in addressListResult.data!!) {
                if (item.address == selectedAddressData!!.address) {
                    saveDeliveryAddressUseCase.execute(item)
                    isMapSuccess = true
                    loadDeliveryAddress()
                    createOrder()
                    break
                }
            }
        }
        if (isMapSuccess.not()) {
            addDeliveryAddressDetailError.value = selectedAddressData
        }
    }

    private suspend fun performCreateOrder(userModel: UserModel) {
        val userPaymentMethodId = if (selectedPaymentData!!.type == PaymentType.CASH) {
            null
        } else {
            selectedPaymentData!!.id
        }

        val campaignCode = if (selectedCoupon != null
            && selectedCoupon?.merchantInfoItem?.id == selectedCart?.merchantId) {
            selectedCoupon?.code
        } else {
            ""
        }

        val timeSlot = selectedTimeSlotData!!.slots.firstOrNull { it.isSelected }?.pickupAt

        val createOrderResult = createOrderUseCase.execute(
            merchantCartId = selectedCart!!.id,
            timeSLot = timeSlot,
            paymentMethodId = selectedPaymentData!!.paymentMethodId,
            userPaymentMethodId = userPaymentMethodId,
            address = selectedAddressData!!,
            userInfo = userModel,
            campaignCode = campaignCode
        )

        if (createOrderResult is UseCaseResult.Success) {
            clearCurrentPromoCode()

            val orderData = createOrderResult.data!!
            trackCreateOrder(
                paymentStatus = TrackingValue.VALUE_PAYMENT_STATUS_SUCCESS,
                paymentMethod = selectedPaymentData!!.type,
                orderAmount = orderData.orderPayment?.paymentAmount ?: 0.0,
                userId = userModel.id,
                orderId = orderData.secureKey,
                couponCode = campaignCode ?: "",
                timeSlot = timeSlot ?: "",
                location = selectedAddressData?.addressDetail ?: ""
            )
            createOrderSuccess.value = orderData
        } else if (createOrderResult is UseCaseResult.Error) {
            trackCreateOrder(
                paymentStatus = TrackingValue.VALUE_PAYMENT_STATUS_ERROR,
                paymentMethod = selectedPaymentData!!.type,
                orderAmount = 0.0,
                userId = userModel.id,
                orderId = "",
                couponCode = campaignCode ?: "",
                timeSlot = timeSlot ?: "",
                location = selectedAddressData?.addressDetail ?: ""
            )
            when (createOrderResult.exception.message) {
                ERROR_CAMPAIGN_QUOTA_EXCEED -> {
                    clearCurrentPromoCode()
                }
                ERROR_CANNOT_CREATE_ORDER_WITH_TIME_SLOT -> {
                    selectedTimeSlotData = null
                    missDeliveryTimeSlotError.value = selectedCart
                }
                else -> {
                    showOtherErrorMessage.value = createOrderResult.exception.message
                }
            }
        }
    }

    fun presentMerchantDetailScreen(itemIndex: Int) {
        val merchant = cartMerchantListData[itemIndex]
        openMerchantDetailScreen.value = merchant.merchant
    }

    private fun getSelectedCoupon() {
        val getSelectedCouponResult = getSelectedCouponUseCase.execute()
        if (getSelectedCouponResult is UseCaseResult.Success) {
            selectedCoupon = getSelectedCouponResult.data!!
        }
    }

    private fun trackRedeemCoupon(
        couponFormat: String,
        couponType: CouponType,
        amountDiscount: Double
    ) {

        val userProfileResult = getUserProfileLocalUseCase.execute()
        if (userProfileResult is UseCaseResult.Success) {
            eventTrackingManager.trackCouponRedeem(
                userId = userProfileResult.data!!.id,
                couponFormat = couponFormat,
                amountDiscount = amountDiscount,
                couponType = couponType
            )
        }
    }

    private fun trackAddProduct(productData: ProductItem, quantity: Int = 1) {
        val optionNameList = mutableListOf<String>()
        productData.optionGroupSelected?.forEach {
            optionNameList.add(it.nameEn)
        }
        eventTrackingManager.trackAddToCart(
            productId = productData.id,
            productName = productData.nameEn,
            merchantId = productData.merchant?.id ?: "",
            merchantName = productData.merchant?.title ?: "",
            quantity = quantity,
            categoryIds = productData.categoryIds,
            options = optionNameList
        )
    }

    private fun trackCartView(cartMerchantList: List<CartMerchantModel>) {
        val cartIds = mutableListOf<Int>()
        val merchantIds = mutableListOf<String>()
        val merchantNames = mutableListOf<String>()
        val productIds = mutableListOf<Int>()
        val productNames = mutableListOf<String>()
        val categoryIdList = mutableListOf<List<Int>>()
        cartMerchantList.forEach { cart ->
            cartIds.add(cart.id)
            merchantIds.add(cart.merchantId)
            merchantNames.add(cart.merchant?.title ?: "")
            cart.products.forEach { product ->
                productIds.add(product.product?.id ?: 0)
                productNames.add(product.product?.nameEn ?: product.product?.nameTh ?: "")
                categoryIdList.add(product.product?.categoryIds ?: listOf())
            }
        }

        eventTrackingManager.trackCartView(
            cartId = cartIds,
            merchantId = merchantIds,
            merchantName = merchantNames,
            productId = productIds,
            productName = productNames,
            categoryIds = categoryIdList
        )
    }

    private fun trackCreateOrder(
        paymentStatus: String,
        paymentMethod: PaymentType,
        orderAmount: Double,
        userId: Int,
        orderId: String,
        couponCode: String,
        timeSlot: String,
        location: String) {
        eventTrackingManager.trackCreateOrder(
            paymentStatus = paymentStatus,
            paymentMethod = paymentMethod,
            orderAmount = orderAmount,
            userId = userId,
            orderId = orderId,
            couponCode = couponCode,
            timeSlot = timeSlot,
            location = location
        )
    }
}