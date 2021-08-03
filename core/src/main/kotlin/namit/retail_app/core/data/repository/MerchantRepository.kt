package namit.retail_app.core.data.repository

import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.enums.MerchantType
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import hasura.GetMerchantByStoreIdQuery
import hasura.GroceryMerchantListQuery
import hasura.fragment.MerchantElement

interface MerchantRepository {
    suspend fun getMerchantList(
        lat: Double,
        lng: Double,
        type: MerchantType
    ): List<MerchantInfoItem>

    suspend fun getMerchantByStoreId(
        storeId: String
    ): MerchantInfoItem
}

class MerchantRepositoryImpl(val apollo: ApolloClient) : MerchantRepository {

    override suspend fun getMerchantList(
        lat: Double,
        lng: Double,
        type: MerchantType
    ): List<MerchantInfoItem> {
        val query =
            GroceryMerchantListQuery.builder().lat(lat).lng(lng).type(type.value)
                .build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()
        val merchantList = response.data()?.merchants()?.items() ?: listOf()
        val result = mutableListOf<MerchantInfoItem>()
        merchantList.forEach { merchant ->
            merchant.fragments().merchantElement().apply {
                convertToMerchantInfoItem(this)?.apply {
                    this.type = type
                    result.add(this)
                }
            }
        }
        return result
    }

    override suspend fun getMerchantByStoreId(storeId: String): MerchantInfoItem {
        val query =
            GetMerchantByStoreIdQuery.builder().storeId(storeId)
                .build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()
        val merchantResponse = response.data()?.merchant()
        var result = MerchantInfoItem()
            merchantResponse?.fragments()?.merchantElement()?.apply {
                convertToMerchantInfoItem(this)?.apply {
                    result = this
                }
        }
        return result
    }

    private fun convertToMerchantInfoItem(
        merchantElement: MerchantElement
    ): MerchantInfoItem? {
        //todo tmp, wait for back end update logic
        return merchantElement.let {
            if (it.store()?.store_id() != null
                && it.title() != null
                && it.store_id() != null
            ) {
                MerchantInfoItem(
                    id = it.store()!!.store_id()!!,
                    title = it.title()!!,
                    imageUrl = it.logo()?.url() ?: "",
                    cover = it.cover() ?: ""
                )
            } else {
                null
            }
        }
    }
}