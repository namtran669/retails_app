package namit.retail_app.grocery.data.repository

import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.enums.MerchantType
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import hasura.GroceryMerchantListQuery
import hasura.fragment.MerchantElement
import java.util.*

interface GroceryMerchantRepository {
    suspend fun getMerchantList(lat: Double, lng: Double): List<MerchantInfoItem>
}

class GroceryMerchantRepositoryImpl(val apollo: ApolloClient) : GroceryMerchantRepository {

    override suspend fun getMerchantList(lat: Double, lng: Double): List<MerchantInfoItem> {
        val query =
            GroceryMerchantListQuery.builder()
                .lat(lat)
                .lng(lng)
                .type(
                    MerchantType.GROCERY.value.toLowerCase(
                        Locale.ENGLISH
                    )
                )
                .build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()
        val merchantList = response.data()?.merchants()?.items() ?: listOf()
        val result = mutableListOf<MerchantInfoItem>()
        merchantList.forEach { merchant ->
            merchant.fragments().merchantElement().apply {
                convertToMerchantInfoItem(this)?.apply {
                    result.add(this)
                }
            }
        }
        return result
    }

    private fun convertToMerchantInfoItem(merchantElement: MerchantElement): MerchantInfoItem? {
        return merchantElement.let {
            if (it.id() != null
                && it.store()?.store_id() != null
                && it.title() != null
            ) {
                MerchantInfoItem(
                    id = it.store()!!.store_id()!!,
                    title = it.title()!!,
                    imageUrl = it.logo()?.url() ?: "",
                    cover = it.cover() ?: "",
                    type = MerchantType.GROCERY
                )
            } else {
                null
            }
        }
    }
}