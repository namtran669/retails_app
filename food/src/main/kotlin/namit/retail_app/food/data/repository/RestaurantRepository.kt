package namit.retail_app.food.data.repository

import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.enums.MerchantType
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import hasura.GroceryMerchantListQuery
import hasura.fragment.MerchantElement

interface RestaurantRepository {
    suspend fun getRestaurantList(lat: Double, lng: Double): List<MerchantInfoItem>
}

class RestaurantRepositoryImpl(private val apollo: ApolloClient) : RestaurantRepository {

    override suspend fun getRestaurantList(lat: Double, lng: Double): List<MerchantInfoItem> {
        val query =
            GroceryMerchantListQuery.builder().lat(lat).lng(lng).type(MerchantType.RESTAURANT.value)
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
                && it.map()?.lat() != null
                && it.map()?.long_() != null
            ) {
                //todo hard code location of restaurant to test UI
                //todo blank data from API -> disable tmp
//                val merchantLat = merchant.map()?.lat()!!.toDouble()
//                val merchantLng = merchant.map()?.long_()!!.toDouble()
                MerchantInfoItem(
                    id = it.store()!!.store_id()!!,
                    title = it.title()!!,
                    imageUrl = it.logo()?.url() ?: "",
                    cover = it.cover() ?: "",
                    description = it.description(),
//                        lat = merchantLat,
//                        lng = merchantLng,
                    distance = 0.5,
                    openingPeriod = it.opening_period(),
                    type = MerchantType.RESTAURANT
                )
            } else {
                null
            }
        }
    }
}