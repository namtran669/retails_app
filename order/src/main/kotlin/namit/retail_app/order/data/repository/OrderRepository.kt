package namit.retail_app.order.data.repository

import namit.retail_app.core.data.entity.*
import namit.retail_app.core.enums.OrderStatus
import namit.retail_app.core.extension.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MM_SS
import namit.retail_app.core.extension.convertToDate
import namit.retail_app.core.utils.RepositoryResult
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.toDeferred
import hasura.CancelOrderMutation
import hasura.CreateOrderMutation
import hasura.GetListOrderQuery
import hasura.GetOrderStatusQuery
import hasura.fragment.OrderElement
import hasura.type.ShippingAddressInput
import java.util.*

interface OrderRepository {
    suspend fun createOrder(
        merchantCartId: Int,
        timeSLot: String?,
        address: AddressModel,
        paymentMethodId: Int,
        userInfo: UserModel,
        userPaymentMethodId: Int?,
        campaignCode: String?
    ): RepositoryResult<OrderModel>

    suspend fun getListOrder(
        page: Int,
        limit: Int,
        type: String
    ): RepositoryResult<List<OrderModel>>

    suspend fun getOrderStatus(secureKey: String): RepositoryResult<String>

    suspend fun cancelOrder(secureKey: String): RepositoryResult<Boolean>
}

class OrderRepositoryImpl(private val apolloAuth: ApolloClient) :
    OrderRepository {

    companion object {
        private const val EMPTY_FIELD = "N/A"
    }

    override suspend fun createOrder(
        merchantCartId: Int,
        timeSLot: String?,
        address: AddressModel,
        paymentMethodId: Int,
        userInfo: UserModel,
        userPaymentMethodId: Int?,
        campaignCode: String?
    ): RepositoryResult<OrderModel> {
        val mutateBuilder = CreateOrderMutation.builder()
            .cartId(merchantCartId)
            .paymentMethodId(paymentMethodId)
            .shipAddress(convertToShipAddressInput(address, userInfo))

        timeSLot?.let {
            mutateBuilder.pickUpSlot(it)
        }

        userPaymentMethodId?.let {
            mutateBuilder.userPaymentMethodId(it)
        }

        mutateBuilder.campaignCodesInput(Input.fromNullable(listOf(campaignCode ?: "")))

        val deferred = apolloAuth.mutate(mutateBuilder.build()).toDeferred()
        val response = deferred.await()
        if (response.hasErrors()) {
            return RepositoryResult.Error(response.errors()[0].message().toString())
        } else {
            val result = response.data()?.createOrder()
            result?.fragments()?.orderElement()?.let {

                val orderModel = convertOrderModel(result.fragments().orderElement())
                if (orderModel?.secureKey?.isNotEmpty() == true) {
                    return RepositoryResult.Success(data = orderModel)
                }
            }
            return RepositoryResult.Success(null)
        }
    }


    override suspend fun getListOrder(
        page: Int,
        limit: Int,
        type: String
    ): RepositoryResult<List<OrderModel>> {
        val query = GetListOrderQuery.builder()
            .limit(limit)
            .page(page)
            .type(type)
            .build()

        val deferred = apolloAuth.query(query).toDeferred()
        val response = deferred.await()
        if (response.hasErrors()) {
            return RepositoryResult.Error(response.errors()[0].message().toString())
        } else {
            val result = response.data()?.orders()?.items()
            val data = mutableListOf<OrderModel>()
            result?.forEach { item ->
                convertOrderModel(item.fragments().orderElement())?.let { data.add(it) }
            }
            data.sortByDescending {
                it.createdAt?.convertToDate(DATE_TIME_FORMAT_YYYY_MM_DD_HH_MM_SS)?.time
            }
            return RepositoryResult.Success(data)
        }
    }

    override suspend fun getOrderStatus(secureKey: String): RepositoryResult<String> {
        val query = GetOrderStatusQuery.builder()
            .secureKey(secureKey).build()
        val deferred = apolloAuth.query(query).toDeferred()
        val response = deferred.await()

        return if (response.hasErrors()) {
            RepositoryResult.Error(response.errors()[0].message().toString())
        } else {
            val result = response.data()?.order()?.current_order_status()
            RepositoryResult.Success(result)
        }
    }

    override suspend fun cancelOrder(secureKey: String): RepositoryResult<Boolean> {
        val mutation = CancelOrderMutation.builder()
            .secureKey(secureKey).build()
        val deferred = apolloAuth.mutate(mutation).toDeferred()
        val response = deferred.await()

        return if (response.hasErrors()) {
            RepositoryResult.Error(response.errors()[0].message().toString())
        } else {
            val result = response.data()?.cancelOrder()?.fragments()?.orderElement()?.secure_key()
            if (!result.isNullOrBlank()) {
                RepositoryResult.Success(true)
            } else {
                RepositoryResult.Success(false)
            }
        }
    }

    private fun convertOrderModel(orderElement: OrderElement): OrderModel? {
        orderElement.apply {
            if (secure_key() != null) {
                val productList = mutableListOf<OrderProduct>()
                order_product().forEach { product ->
                    productList.add(
                        OrderProduct(
                            title = product.title(),
                            quantity = product.quantity(),
                            finalPrice = product.final_price(),
                            productId = product.product_id()

                        )
                    )
                }

                val store = OrderStoreInfo(
                    order_store_info()?.store_id(),
                    order_store_info()?.store_name(),
                    cover = order_store_info()?.cover()
                )
                val payment = OrderPayment(
                    discount = discount(),
                    paymentAmount = payment()?.payment_amount(),
                    paymentMethod = payment()?.payment_method(),
                    brand = payment_detail()?.source()?.brand()
                )

                return OrderModel(
                    total = total(),
                    finalPrice = final_price(),
                    createdAt = created_at(),
                    currentOrderStatus = convertOrderStatus(current_order_status()),
                    secureKey = secure_key()!!,
                    orderAddress = order_address()?.delivery_address_1(),
                    orderProduct = productList,
                    orderStoreInfo = store,
                    deliveryFee = shipping()?.shipping_price(),
                    orderPayment = payment,
                    pickupAt = pickup_at()
                )
            } else {
                return null
            }
        }
    }

    private fun convertOrderStatus(orderTxt: String): OrderStatus {
        return when (orderTxt.toUpperCase(locale = Locale.ENGLISH)) {
            OrderStatus.PENDING.value -> OrderStatus.PENDING
            OrderStatus.CONFIRMED.value -> OrderStatus.CONFIRMED
            OrderStatus.SHIPPING.value -> OrderStatus.SHIPPING
            OrderStatus.IN_PROGRESS.value -> OrderStatus.IN_PROGRESS
            OrderStatus.READY_TO_SHIP.value -> OrderStatus.READY_TO_SHIP
            OrderStatus.COMPLETED.value -> OrderStatus.COMPLETED
            OrderStatus.CANCELLED.value -> OrderStatus.CANCELLED
            else -> OrderStatus.UNKNOWN
        }
    }

    private fun convertToShipAddressInput(
        addressModel: AddressModel,
        userInfo: UserModel
    ): ShippingAddressInput {
        return addressModel.let {

            val phoneNumber = if (!it.phone.isNullOrBlank()) {
                it.phone!!
            } else {
                userInfo.mobile
            }
            ShippingAddressInput.builder()
                .address(it.address ?: "")
                .lat(it.lat).lng(it.lng)
                .note(addressModel.note ?: "")
                .firstName(EMPTY_FIELD).lastName(EMPTY_FIELD)
                .note(addressModel.note ?: "")
                .phone(phoneNumber).build()
        }
    }

}