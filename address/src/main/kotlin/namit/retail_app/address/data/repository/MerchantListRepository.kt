package namit.retail_app.address.data.repository

import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.enums.MerchantType
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import hasura.GroceryMerchantListQuery
import hasura.fragment.MerchantElement


interface MerchantListRepository {
    suspend fun getMerchantList(lat: Double, lng: Double, type: String): List<MerchantInfoItem>
}

class MerchantListRepositoryImpl(val apollo: ApolloClient) : MerchantListRepository {

    override suspend fun getMerchantList(
        lat: Double,
        lng: Double,
        type: String
    ): List<MerchantInfoItem> {
        val query =
            GroceryMerchantListQuery.builder()
                .lat(lat)
                .lng(lng)
                .type(type)
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
                    type = MerchantType.UNKNOWN
                )
            } else {
                null
            }
        }
    }
}