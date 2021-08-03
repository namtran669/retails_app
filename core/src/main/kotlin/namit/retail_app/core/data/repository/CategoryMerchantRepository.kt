package namit.retail_app.core.data.repository

import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.utils.LocaleUtils
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import hasura.FetchMerchantCategoryQuery
import hasura.FetchMerchantSubCategoryQuery
import hasura.fragment.CategoryElement

interface CategoryMerchantRepository {
    suspend fun getRootCategoryList(merchantId: String): List<CategoryItem>
    suspend fun getSubCategoryList(merchantId: String, parentId: Int): List<CategoryItem>
}

class CategoryMerchantRepositoryImpl(val apollo: ApolloClient) :
    CategoryMerchantRepository {

    override suspend fun getRootCategoryList(merchantId: String): List<CategoryItem> {
        val query = FetchMerchantCategoryQuery.builder().merchantId(merchantId).build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()

        val categoryList = response.data()?.categories()
        val result = mutableListOf<CategoryItem>()

        categoryList?.forEach { category ->
            val categoryElement = category.fragments().categoryElement()
            if (categoryElement.merchant_id() != null) {
                result.add(createCategory(categoryElement))
            }
        }
        return result
    }

    override suspend fun getSubCategoryList(merchantId: String, parentId: Int): List<CategoryItem> {
        val query =
            FetchMerchantSubCategoryQuery.builder().merchantId(merchantId).parentId(parentId)
                .build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()

        val categoryList = response.data()?.categories()
        val result = mutableListOf<CategoryItem>()

        categoryList?.forEach { category ->
            val categoryElement = category.fragments().categoryElement()
            if (categoryElement.merchant_id() != null) {
                result.add(
                    CategoryItem(
                        id = categoryElement.id(),
                        merchantId = categoryElement.merchant_id()!!,
                        parentId = categoryElement.parent_id(),
                        iconUrl = categoryElement.icon(),
                        nameEn = categoryElement.name_en(),
                        nameTh = categoryElement.name_th(),
                        descriptionEn = categoryElement.description_en(),
                        descriptionTh = categoryElement.description_th(),
                        productCount = categoryElement.products_categories_aggregate().aggregate()
                            ?.count() ?: 0,
                        name = if (LocaleUtils.isThai()) categoryElement.name_th() else categoryElement.name_en()
                    )
                )
            }
        }
        return result
    }

    private fun createCategory(categoryElement: CategoryElement): CategoryItem {
        return CategoryItem(
            id = categoryElement.id(),
            merchantId = categoryElement.merchant_id()!!,
            parentId = categoryElement.parent_id(),
            iconUrl = categoryElement.icon(),
            nameEn = categoryElement.name_en(),
            nameTh = categoryElement.name_th(),
            descriptionEn = categoryElement.description_en(),
            descriptionTh = categoryElement.description_th(),
            productCount = categoryElement.products_categories_aggregate().aggregate()?.count()
                ?: 0,
            name = if (LocaleUtils.isThai()) categoryElement.name_th() else categoryElement.name_en()
        )
    }
}