package namit.retail_app.core.utils

import namit.retail_app.core.data.entity.CartMerchantModel
import namit.retail_app.core.data.entity.CartModel
import namit.retail_app.core.data.entity.ProductItem

object CartUtils {
    const val INTERVAL_TIME_ADD_TO_CART = 300

    fun convertToCartModel(merchantList: List<CartMerchantModel>): CartModel {
        var totalQuantityData = 0
        var totalPriceData = 0.0
        merchantList.forEach { merchant ->
            val quantityPricePair = calculateQuantityPriceMerchant(merchant)
            totalQuantityData += quantityPricePair.first
            totalPriceData += quantityPricePair.second
        }

        return CartModel(
            totalPrice = totalPriceData,
            amount = totalQuantityData,
            merchants = merchantList.toMutableList()
        )
    }

    fun calculateQuantityPriceMerchant(merchant: CartMerchantModel): Pair<Int, Double> {
        var totalQuantity = 0
        var totalPrice = 0.0
        merchant.products.forEach { product ->
            totalQuantity += product.quantity
            product.product?.let {
                totalPrice += product.quantity.times(
                    getTotalPriceProduct(
                        product = it
                    )
                )
            }
        }
        return Pair(first = totalQuantity, second = totalPrice)
    }

    fun getTotalPriceProduct(product: ProductItem): Double {
        var totalPrice: Double = product.retailPriceWithTax ?: 0.0
        product.optionGroupSelected?.forEach { group ->
            group.options.forEach { option ->
                totalPrice += option.price ?: 0.0

            }
        }
        return totalPrice
    }
}