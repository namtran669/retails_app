package namit.retail_app.core.data.repository


import namit.retail_app.core.data.entity.*
import namit.retail_app.core.enums.CouponType
import namit.retail_app.core.extension.applyWithBaseUrl
import namit.retail_app.core.extension.convertSatangToBaht
import namit.retail_app.core.utils.CartUtils
import namit.retail_app.core.utils.LocaleUtils
import namit.retail_app.core.utils.RepositoryResult
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import hasura.*

interface CartRepository {
    suspend fun addItemToCart(product: ProductItem, secureId: String, quantity: Int): Boolean

    suspend fun reduceOne(
        product: ProductItem,
        secureId: String,
        quantity: Int,
        cartItemId: Int
    ): Boolean

    suspend fun deleteItemInCart(itemId: Int, secureId: String): Boolean

    suspend fun loadCartList(secureId: String, page: Int): CartModel

    fun getCurrentCartInfo(): CartModel?

    suspend fun getCartDetail(secureId: String): List<CartItemDetail>

    suspend fun claimCart(secureId: String): Boolean

    suspend fun getDeliveryFee(cartId: Int): Double

    suspend fun redeemCart(cartId: Int, couponCode: String): RepositoryResult<RedeemCart>
}

class CartRepositoryImpl(
    private val apollo: ApolloClient
) : CartRepository {

    private var currentCartInfo: CartModel? = null

    companion object {
        const val SIZE_CART_EACH_REQUEST = 10
        const val CART_FIRST_PAGE = 0
    }

    override suspend fun addItemToCart(
        product: ProductItem,
        secureId: String,
        quantity: Int
    ): Boolean {
        val productOption = convertToCartOptionGroup(product.optionGroupSelected)
        val item = CartItemInput(
            productId = product.id,
            quantity = quantity,
            secureId = secureId,
            productOption = if (productOption.isNotEmpty()) productOption else null,
            note = product.note
        )

        val mutate = AddItemToCartMutation.builder()
            .product_id(item.productId)
            .quantity(item.quantity)
            .secure_id(item.secureId)
            .note(item.note)
            .product_options(item.productOption)
            .build()

        val deferred = apollo.mutate(mutate).toDeferred()
        val response = deferred.await()
        val result = response.data()?.addToCart()?.cart_id() ?: -1
        return result > 0
    }

    override suspend fun reduceOne(
        product: ProductItem,
        secureId: String,
        quantity: Int,
        cartItemId: Int
    ): Boolean {
        val productOption = convertToCartOptionGroup(product.optionGroupSelected)
        val item = CartItemInput(
            cartItemId = cartItemId,
            quantity = quantity,
            secureId = secureId,
            productOption = if (productOption.isNotEmpty()) productOption else null
        )

        val mutate = UpdateItemCartMutation.builder()
            .cart_item_id(item.cartItemId)
            .secure_id(item.secureId)
            .note(item.note)
            .product_options(item.productOption)
            .quantity(item.quantity)
            .build()

        val deferred = apollo.mutate(mutate).toDeferred()
        val response = deferred.await()
        val result = response.data()?.updateCartItem()?.modified() ?: 0
        return result > 0
    }

    override suspend fun deleteItemInCart(itemId: Int, secureId: String): Boolean {
        val mutate = DeleteItemCartMutation.builder()
            .secure_id(secureId)
            .item_id(itemId)
            .build()

        val deferred = apollo.mutate(mutate).toDeferred()
        val response = deferred.await()
        val result = response.data()?.deleteCartItem()?.total_deleted() ?: 0
        return result > 0
    }

    override suspend fun loadCartList(secureId: String, page: Int): CartModel {
        val skip = page * SIZE_CART_EACH_REQUEST
        val query = GetCartDetailQuery.builder().skip(skip).limit(SIZE_CART_EACH_REQUEST)
            .secure_id(secureId).build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()

        val cartMerchantList = mutableListOf<CartMerchantModel>()

        response.data()?.carts()?.edges()?.forEach { cart ->
            if (cart.items().isNotEmpty()) {

                val cartItemList = mutableListOf<CartProductModel>()
                cart.items().forEach { cartItem ->
                    //Get selection option group (id only)
                    val selectOptionGroupIdMap = hashMapOf<Int, List<Int>>()
                    cartItem.product_options()?.forEach { productOption ->
                        val selectOption = mutableListOf<Int>()
                        productOption.options().forEach {
                            selectOption.add(it.id())
                        }
                        productOption.option_group()?.let {
                            selectOptionGroupIdMap[it.id()] = selectOption
                        }
                    }

                    //Mapping selection option group id to get price
                    val productItem = ProductItem()
                    val optionGroupList = mutableListOf<OptionGroup>()

                    cartItem.product().option_groups()?.forEach { group ->
                        //Check group is selected that be in option group list or not
                        if (selectOptionGroupIdMap[group.id()] != null) {
                            val optionList = mutableListOf<OptionPick>()

                            group.options().forEach { option ->
                                //If option is selected, add to list
                                selectOptionGroupIdMap[group.id()]?.firstOrNull { it == option.id() }
                                    ?.let {
                                        optionList.add(
                                            OptionPick(
                                                id = option.id(),
                                                price = option.price().toString().toDouble()
                                                    .convertSatangToBaht()
                                            )
                                        )
                                    }
                            }

                            group.apply {
                                optionGroupList.add(
                                    OptionGroup(
                                        id = id(),
                                        options = optionList
                                    )
                                )
                            }
                        }
                    }

                    //Get product info
                    cartItem.product().apply {
                        val categoryIds = mutableListOf<Int>()
                        products_categories().forEach { item ->
                            categoryIds.add(item.category().id())
                        }

                        productItem.id = id()
                        productItem.merchantId = merchant_id()
                        productItem.nameEn = name_en() ?: name_th()
                        productItem.nameTh = name_th()
                        productItem.thumbnailUrl = thumbnail_url()?.applyWithBaseUrl()
                        productItem.retailPriceWithTax =
                            retail_price_with_tax()?.toString()?.toDouble()?.convertSatangToBaht()
                                ?: 0.0
                        productItem.sku = sku()
                        productItem.optionGroupSelected = optionGroupList
                        productItem.categoryIds = categoryIds
                        productItem.note = cartItem.note() ?: ""
                        productItem.name = if (LocaleUtils.isThai()) name_th() else name_en() ?: ""
                        productItem.description =
                            if (LocaleUtils.isThai()) description_th() ?: "" else description_en()
                                ?: ""
                    }

                    cartItemList.add(
                        CartProductModel(
                            id = cartItem.id(),
                            quantity = cartItem.quantity(),
                            product = productItem
                        )
                    )
                }

                cartMerchantList.add(
                    CartMerchantModel(
                        id = cart.id(),
                        userId = cart.user_id(),
                        secureId = cart.secure_id(),
                        merchantId = cart.merchant_id(),
                        products = cartItemList
                    )
                )
            }
        }

        val cartModel = CartUtils.convertToCartModel(cartMerchantList)
        currentCartInfo = cartModel
        return cartModel
    }

    override fun getCurrentCartInfo(): CartModel? {
        return currentCartInfo
    }

    override suspend fun getCartDetail(secureId: String): List<CartItemDetail> {
        val query = GetCartDetailQuery.builder()
            .limit(SIZE_CART_EACH_REQUEST)
            .skip(0)
            .secure_id(secureId)
            .build()

        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()
        val result = response.data()?.carts()?.edges()
        val data = mutableListOf<CartItemDetail>()
        result?.forEach { cart ->
            val item = cart.items()
            OptionGroup(id = 0, options = listOf(), nameEn = "", nameTh = "")
            item.forEach { cartItem ->
                val optionGroups = mutableListOf<OptionGroup>()
                cartItem.product_options()?.forEach { productOption ->
                    val optionPick = mutableListOf<OptionPick>()
                    productOption.options().forEach { option ->
                        optionPick.add(
                            OptionPick(
                                id = option.id(), nameTh = "", nameEn = "", price = 0.0,
                                currency = "", description = "", isSelected = true
                            )
                        )
                    }

                    productOption.option_group()?.let {
                        optionGroups.add(
                            OptionGroup(
                                id = it.id(),
                                options = optionPick,
                                nameEn = "",
                                nameTh = ""
                            )
                        )
                    }
                }

                data.add(
                    CartItemDetail(
                        cartId = cartItem.cart_id(),
                        id = cartItem.id(),
                        productId = cartItem.product_id(),
                        note = cartItem.note() ?: "",
                        productOptions = optionGroups,
                        quantity = cartItem.quantity()
                    )
                )
            }
        }
        return data
    }

    //Need access token
    override suspend fun claimCart(secureId: String): Boolean {
        val mutate = ClaimCartMutation.builder()
            .secureId(secureId)
            .build()

        val deferred = apollo.mutate(mutate).toDeferred()
        val response = deferred.await()
        val result = response.data()?.claimCart()?.total() ?: -1
        return result > 0
    }

    override suspend fun getDeliveryFee(cartId: Int): Double {
        val query = GetDeliveryPriceQuery.builder()
            .cartId(cartId)
            .build()

        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()
        return response.data()?.delivery_price()?.shipping_fee() ?: 0.0
    }

    override suspend fun redeemCart(cartId: Int, couponCode: String): RepositoryResult<RedeemCart> {
        val query = RedeemCartQuery.builder()
            .cartId(cartId)
            .campaignCode(listOf(couponCode))
            .build()

        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()
        return if (response.hasErrors()) {
            RepositoryResult.Error(response.errors()[0].message().toString())
        } else {
            RepositoryResult.Success(
                RedeemCart().apply {
                    merchantId = response.data()
                        ?.redeemed_cart()
                        ?.firstOrNull()
                        ?.redeemed()
                        ?.firstOrNull()
                        ?.merchant_id() ?: ""
                    discount = response.data()
                        ?.redeemed_cart()
                        ?.firstOrNull()
                        ?.redeemed()
                        ?.firstOrNull()
                        ?.discount()
                        ?.convertSatangToBaht() ?: 0.0
                    couponType = CouponType.valueOf(
                        response.data()?.redeemed_cart()?.firstOrNull()
                            ?.campaign()
                            ?.campaign_type()
                            ?.toString() ?: CouponType.UNKNOWN.value
                    )
                })
        }
    }
}