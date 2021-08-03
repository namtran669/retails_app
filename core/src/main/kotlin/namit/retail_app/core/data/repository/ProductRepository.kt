package namit.retail_app.core.data.repository


import namit.retail_app.core.data.entity.*
import namit.retail_app.core.extension.applyWithBaseUrl
import namit.retail_app.core.extension.convertSatangToBaht
import namit.retail_app.core.utils.DateUtils
import namit.retail_app.core.utils.LocaleUtils
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import hasura.FetchFeaturedProductsCategoryRootQuery
import hasura.FetchMerchantProductByCategoryQuery
import hasura.FetchMerchantProductsQuery
import hasura.SearchProductQuery
import hasura.fragment.ProductElement
import org.json.JSONArray

interface ProductRepository {
    suspend fun searchProduct(keywords: String, merchantId: String, page: Int): SearchResultModel

    suspend fun getRelatedProduct(
        merchantId: String,
        categoryIds: List<Int>,
        productId: Int,
        page: Int
    ): List<ProductItem>

    suspend fun getProductByCategory(
        merchantId: String,
        categoryIds: List<Int>,
        page: Int
    ): List<ProductItem>

    suspend fun getProductList(merchantId: String, page: Int): List<ProductItem>

    suspend fun getFeatureProductCategoryList(merchantIds: List<String>): List<CategoryItem>
}

class ProductRepositoryImpl(
    private val apollo: ApolloClient
) : ProductRepository {

    companion object {
        const val SIZE_PRODUCT_EACH_REQUEST = 10
        const val FIRST_PAGE = 0
    }

    override suspend fun getRelatedProduct(
        merchantId: String,
        categoryIds: List<Int>,
        productId: Int,
        page: Int
    ): List<ProductItem> {
        val offset = page * SIZE_PRODUCT_EACH_REQUEST

        val query = FetchMerchantProductByCategoryQuery.builder()
            .merchantId(merchantId)
            .offset(offset)
            .categoryId(categoryIds)
            .productId(productId)
            .limit(SIZE_PRODUCT_EACH_REQUEST).build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()

        val result = mutableListOf<ProductItem>()

        response.data()?.products()?.forEach { product ->
            product.fragments().productElement().apply {
                result.add(convertProductItem(this))
            }
        }
        return result
    }

    override suspend fun getProductByCategory(
        merchantId: String,
        categoryIds: List<Int>,
        page: Int
    ): List<ProductItem> {
        val offset = page * SIZE_PRODUCT_EACH_REQUEST

        val query = FetchMerchantProductByCategoryQuery.builder()
            .merchantId(merchantId)
            .offset(offset)
            .categoryId(categoryIds)
            .limit(SIZE_PRODUCT_EACH_REQUEST).build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()

        val result = mutableListOf<ProductItem>()

        response.data()?.products()?.forEach { product ->
            product.fragments().productElement().apply {
                result.add(convertProductItem(this))
            }
        }
        return result
    }

    override suspend fun getProductList(merchantId: String, page: Int): List<ProductItem> {
        val offset = page * SIZE_PRODUCT_EACH_REQUEST

        val query = FetchMerchantProductsQuery.builder().merchantId(merchantId).offset(offset)
            .limit(SIZE_PRODUCT_EACH_REQUEST).build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()

        val productList = response.data()?.products()
        val result = mutableListOf<ProductItem>()

        productList?.forEach { product ->
            product.fragments().productElement().apply {
                result.add(convertProductItem(this))
            }
        }

        return result
    }

    private fun convertProductItem(productElement: ProductElement): ProductItem {
        return productElement.let {
            val categoryList = mutableListOf<Int>()
            it.products_categories().forEach { item ->
                categoryList.add(item.category().id())
            }

            val optionGroupList = mutableListOf<OptionGroup>()
            it.option_groups().forEach { group ->
                val optionList = mutableListOf<OptionPick>()
                group.fragments().productOptionGroup().options().forEach { option ->
                    if (option.name_en() != null && option.name_th() != null) {
                        optionList.add(
                            OptionPick(
                                id = option.id(),
                                name =
                                if (LocaleUtils.isThai()) {
                                    option.name_th() ?: ""
                                } else {
                                    option.name_en() ?: ""
                                },
                                nameEn = option.name_en()!!,
                                nameTh = option.name_th()!!,
                                currency = option.currency(),
                                price = option.price()?.toString()?.toDouble()
                                    ?.convertSatangToBaht()
                                    ?: 0.0,
                                description = option.description()
                            )
                        )
                    }
                }

                group.fragments().productOptionGroup().apply {
                    if (name_th() != null) {
                        optionGroupList.add(
                            OptionGroup(
                                id = id(),
                                name =
                                if (LocaleUtils.isThai()) {
                                    name_th() ?: ""
                                } else {
                                    name_en() ?: ""
                                },
                                nameEn = name_en(),
                                nameTh = name_th()!!,
                                selection = convertSelectionType(selection() ?: ""),
                                maxLimit = max_limit(),
                                minLimit = min_limit(),
                                options = optionList,
                                type = convertOptionType(type())
                            )
                        )
                    }
                }
            }

            val productImageList = if (it.images().toString().isNotEmpty()) {
                val jsonArray = JSONArray(it.images().toString())
                val resultList = mutableListOf<String>()
                for (index in 0 until jsonArray.length()) {
                    resultList.add(jsonArray[index].toString().applyWithBaseUrl())
                }
                resultList.toList()
            } else {
                listOf()
            }

            ProductItem(
                id = it.id(),
                nameEn = it.name_en() ?: it.name_th(),
                nameTh = it.name_th(),
                quantityInStock = it.quantity() ?: 0,
                merchantId = it.merchant_id(),
                retailPriceWithTax = it.retail_price_with_tax()
                    ?.toString()
                    ?.toDouble()
                    ?.convertSatangToBaht()
                    ?: 0.0,
                weightUnit = it.weight_unit(),
                weight = it.weight()?.toFloat(),
                thumbnailUrl = it.thumbnail_url()?.applyWithBaseUrl(),
                country = it.country(),
                distributor = it.distributor(),
                updatedAt = it.updated_at().toString(),
                categoryIds = categoryList,
                descriptionEn = it.description_en(),
                descriptionTh = it.description_th(),
                optionGroup = optionGroupList,
                packageItem = it.package_(),
                packageUnit = it.package_unit(),
                shelfLife = it.shelf_life(),
                shelfLifeUnit = it.shelf_life_unit(),
                images = productImageList,
                sku = it.sku(),
                isAlcohol = it.is_alcohol ?: false,
                discountPercentage = it.discount_percentage()?.toString()?.toFloat(),
                isDiscountPrice = it.is_discount_price ?: false,
                discountPriceStartAt = it.discount_price_start_at().toString(),
                discountPriceEndAt = it.discount_price_end_at().toString(),
                discountPriceWithTax = it.discount_price_with_tax()
                    ?.toString()
                    ?.toDouble()
                    ?.convertSatangToBaht()
                    ?: 0.0,
                showDiscount = showDiscount(
                    it.is_discount_price ?: false,
                    it.discount_price_start_at().toString(),
                    it.discount_price_end_at().toString()
                ),
                name = if (LocaleUtils.isThai()) it.name_th() else it.name_en() ?: "",
                description = if (LocaleUtils.isThai()) it.description_th()
                    ?: "" else it.description_en() ?: ""

            )
        }
    }

    private fun showDiscount(
        is_discount_price: Boolean,
        discountPriceStartAt: String,
        discountPriceEndAt: String
    ): Boolean {
        if (is_discount_price) {
            val currentTimestamp = System.currentTimeMillis()
            val starTimeDiscount = DateUtils.getTimeStampFromDateTime(discountPriceStartAt)
            val endTimeDiscount = DateUtils.getTimeStampFromDateTime(discountPriceEndAt)
            if (starTimeDiscount != null && endTimeDiscount != null) {
                if (currentTimestamp > starTimeDiscount && currentTimestamp < endTimeDiscount) {
                    return true
                }
            }
        }
        return false
    }

    override suspend fun searchProduct(
        keywords: String,
        merchantId: String,
        page: Int
    ): SearchResultModel {
        val offset = page * SIZE_PRODUCT_EACH_REQUEST

        val query = SearchProductQuery
            .builder()
            .keywords(keywords)
            .merchantId(merchantId)
            .offset(offset)
            .limit(SIZE_PRODUCT_EACH_REQUEST)
            .build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()
        val productList = response.data()?.products()
        val result = mutableListOf<ProductItem>()
        productList?.forEach {
            result.add(convertProductItem(it.fragments().productElement()))
        }

        return SearchResultModel(keyword = keywords, resultList = result)
    }

    private fun convertOptionType(type: String): OptionType {
        return when {
            type.equals(OptionType.REQUIRED.value, true) -> {
                OptionType.REQUIRED
            }
            else -> {
                OptionType.OPTIONAL
            }
        }
    }

    private fun convertSelectionType(type: String): OptionSelection {
        return when {
            type.equals(OptionSelection.MULTIPLE.value, true) -> {
                OptionSelection.MULTIPLE
            }
            else -> {
                OptionSelection.SINGLE
            }
        }
    }

    override suspend fun getFeatureProductCategoryList(merchantIds: List<String>): List<CategoryItem> {
        val query =
            FetchFeaturedProductsCategoryRootQuery.builder().merchantIdList(merchantIds).build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()

        val categoryList = response.data()?.categories()
        val result = mutableListOf<CategoryItem>()

        categoryList?.forEach { category ->
            val featureProductCategory = category.fragments().featuredProductCategory()
            if (featureProductCategory.merchant_id() != null && featureProductCategory.products_categories()
                    .isNotEmpty()
            ) {
                val productList = mutableListOf<ProductItem>()
                featureProductCategory.products_categories().forEach { product ->
                    product.product().fragments().productElement().apply {
                        productList.add(convertProductItem(this))
                    }
                }

                result.add(
                    CategoryItem(
                        id = featureProductCategory.id(),
                        merchantId = featureProductCategory.merchant_id()!!,
                        parentId = featureProductCategory.parent_id(),
                        iconUrl = featureProductCategory.icon(),
                        nameEn = featureProductCategory.name_en(),
                        nameTh = featureProductCategory.name_th(),
                        descriptionEn = featureProductCategory.description_en(),
                        descriptionTh = featureProductCategory.description_th(),
                        productList = productList,
                        name = if (LocaleUtils.isThai()) featureProductCategory.name_th() else featureProductCategory.name_en()
                    )
                )
            }
        }
        return result
    }
}
