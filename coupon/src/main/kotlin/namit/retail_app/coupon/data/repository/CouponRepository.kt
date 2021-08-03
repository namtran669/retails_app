package namit.retail_app.coupon.data.repository

import namit.retail_app.core.data.entity.CouponModel
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.enums.CouponType
import namit.retail_app.core.enums.MerchantType
import namit.retail_app.core.provider.PreferenceProvider
import namit.retail_app.core.utils.LocaleUtils
import namit.retail_app.coupon.data.entity.CouponFilterModel
import namit.retail_app.coupon.enums.CouponFilterType
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import com.google.gson.Gson
import hasura.*
import hasura.fragment.CampaignElement
import java.util.*

interface CouponRepository {
    suspend fun loadAllCouponList(page: Int): List<CouponModel>
    suspend fun loadCouponListWithCart(cartId: Int): List<CouponModel>
    suspend fun loadCouponByVertical(page: Int, vertical: String): List<CouponModel>
    suspend fun loadCouponByMerchant(page: Int, merchantIds: List<String>): List<CouponModel>
    suspend fun loadVerticalList(): List<CouponFilterModel>
    fun saveSelectedCoupon(couponModel: CouponModel)
    fun getSelectedCoupon(): CouponModel
    fun removeSelectedCoupon()
    fun hasSelectedCoupon(): Boolean
}

class CouponRepositoryImpl(val apollo: ApolloClient, val preferenceProvider: PreferenceProvider) :
    CouponRepository {

    companion object {
        private const val PREF_SELECTED_COUPON = "PREF_SELECTED_COUPON"
        const val VERTICAL = "VERTICAL"
        const val VERTICAL_CAFE = "CAFE"
        const val VERTICAL_RESTAURANTS = "RESTAURANTS"
        const val VERTICAL_SUPERMARKET = "SUPERMARKET"
        const val VERTICAL_CONVENIENCE_STORE = "CONVENIENCE_STORE"

        const val SIZE_COUPON_EACH_REQUEST = 20
        const val FIRST_PAGE = 0
    }

    override suspend fun loadCouponListWithCart(cartId: Int): List<CouponModel> {
        val query =
            GetCampaignListWithCartIdQuery
                .builder()
                .cartId(cartId)
                .build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()
        val campaigns = response.data()?.redeemed_cart() ?: listOf()
        val result = mutableListOf<CouponModel>()
        campaigns.forEach {
            val campaign = it.campaign()?.fragments()?.campaignElement()
            campaign?.let {
                val campaignScope = campaign.scope()
                    ?.fragments()
                    ?.campaignScope()
                val merchantType = mapScopeTypeToMerchantType(
                    scopeType = campaignScope
                        ?.scope_type()
                        .toString()
                        .toUpperCase(Locale.ENGLISH),
                    title = campaignScope
                        ?.title()
                        .toString()
                        .toUpperCase(Locale.ENGLISH)
                )
                if (campaign.code().isNullOrEmpty().not() && merchantType != MerchantType.UNKNOWN) {
                    result.add(convertCoupon(campaign = campaign, merchantType = merchantType))
                }
            }
        }
        return result
    }

    override suspend fun loadAllCouponList(page: Int): List<CouponModel> {
        val offset = page * SIZE_COUPON_EACH_REQUEST

        val query =
            GetAllCampaignListQuery
                .builder()
                .limit(SIZE_COUPON_EACH_REQUEST)
                .offset(offset)
                .build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()
        val campaigns = response.data()?.campaigns()?.edges() ?: listOf()
        val result = mutableListOf<CouponModel>()
        campaigns.forEach {
            val campaign = it.fragments().campaignElement()
            val campaignScope = campaign.scope()
                ?.fragments()
                ?.campaignScope()
            val merchantType = mapScopeTypeToMerchantType(
                scopeType = campaignScope
                    ?.scope_type()
                    .toString()
                    .toUpperCase(Locale.ENGLISH),
                title = campaignScope
                    ?.title()
                    .toString()
                    .toUpperCase(Locale.ENGLISH)
            )
            if (campaign.code().isNullOrEmpty().not() && merchantType != MerchantType.UNKNOWN) {
                result.add(convertCoupon(campaign = campaign, merchantType = merchantType))
            }
        }

        return result
    }

    override suspend fun loadCouponByVertical(
        page: Int,
        vertical: String
    ): List<CouponModel> {
        val offset = page * SIZE_COUPON_EACH_REQUEST

        val query =
            GetCampaignListByVerticalQuery.builder()
                .limit(SIZE_COUPON_EACH_REQUEST)
                .offset(offset)
                .vertical(listOf(vertical))
                .build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()
        val campaigns = response.data()?.campaigns()?.edges() ?: listOf()
        val result = mutableListOf<CouponModel>()
        campaigns.forEach {
            val campaign = it.fragments().campaignElement()
            val campaignScope = campaign.scope()
                ?.fragments()
                ?.campaignScope()
            val merchantType = mapScopeTypeToMerchantType(
                scopeType = campaignScope
                    ?.scope_type()
                    .toString()
                    .toUpperCase(Locale.ENGLISH),
                title = campaignScope
                    ?.title()
                    .toString()
                    .toUpperCase(Locale.ENGLISH)
            )
            if (campaign.code().isNullOrEmpty().not() && merchantType != MerchantType.UNKNOWN) {
                result.add(convertCoupon(campaign = campaign, merchantType = merchantType))
            }
        }

        return result
    }

    override suspend fun loadCouponByMerchant(
        page: Int,
        merchantIds: List<String>
    ): List<CouponModel> {
        val offset = page * SIZE_COUPON_EACH_REQUEST

        val query =
            GetCampaignListByMerchantQuery.builder()
                .limit(SIZE_COUPON_EACH_REQUEST)
                .offset(offset)
                .merchantId(merchantIds)
                .build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()
        val campaigns = response.data()?.campaigns()?.edges() ?: listOf()
        val result = mutableListOf<CouponModel>()
        campaigns.forEach {
            val campaign = it.fragments().campaignElement()
            val campaignScope = campaign.scope()
                ?.fragments()
                ?.campaignScope()
            val merchantType = mapScopeTypeToMerchantType(
                scopeType = campaignScope
                    ?.scope_type()
                    .toString()
                    .toUpperCase(Locale.ENGLISH),
                title = campaignScope
                    ?.title()
                    .toString()
                    .toUpperCase(Locale.ENGLISH)
            )
            if (campaign.code().isNullOrEmpty().not() && merchantType != MerchantType.UNKNOWN) {
                result.add(convertCoupon(campaign = campaign, merchantType = merchantType))
            }
        }

        return result
    }

    override suspend fun loadVerticalList(): List<CouponFilterModel> {
        val query =
            GetAllVerticalQuery.builder()
                .build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()
        val campaignTypes = response.data()?.verticals() ?: listOf()
        val result = mutableListOf<CouponFilterModel>()
        campaignTypes.forEach { campaignType ->
            result.add(
                CouponFilterModel(
                    id = campaignType.id(),
                    nameEn = campaignType?.name_en() ?: "",
                    nameTh = campaignType?.name_th() ?: "",
                    slug = campaignType.slug(),
                    filterType = mapVerticalTypeToCouponFilter(campaignType.slug()),
                    name = if (LocaleUtils.isThai()) campaignType.name_th() else campaignType.name_en()
                )
            )
        }
        return result
    }

    private fun convertCoupon(campaign: CampaignElement, merchantType: MerchantType): CouponModel {
        val campaignType =
            CouponType.valueOf(campaign.campaign_type()?.toString() ?: CouponType.UNKNOWN.value)

        val coupon = CouponModel(
            code = campaign.code()!!,
            name = campaign.name() ?: "",
            description = campaign.description() ?: "",
            isFlashDeals = campaign.flash_deal() ?: false,
            isRanOut = false,
            endDate = campaign.end_date()?.toString()?.toLong() ?: 0,
            endTime = campaign.end_time() ?: "",
            couponValue = mapCouponValueByType(campaignElement = campaign),
            couponType = campaignType,
            couponMerchantType = merchantType
        )

        val campaignScope = campaign.scope()?.fragments()?.campaignScope()
        val merchantInfoItem = MerchantInfoItem().apply {
            id = campaign.customer_gets()?.firstOrNull()?.from_value() ?: ""
            title = campaignScope?.title() ?: campaignScope?.title() ?: ""
            imageUrl = campaignScope?.logo()?.url()
        }

        coupon.merchantInfoItem = merchantInfoItem

        return coupon
    }

    private fun mapVerticalTypeToCouponFilter(verticalType: String?): CouponFilterType {
        return verticalType?.let {
            when (it) {
                VERTICAL_CAFE -> {
                    CouponFilterType.CAFE
                }
                VERTICAL_RESTAURANTS -> {
                    CouponFilterType.RESTAURANTS
                }
                VERTICAL_SUPERMARKET -> {
                    CouponFilterType.SUPERMARKET
                }
                else -> {
                    CouponFilterType.CONVENIENCE_STORE
                }
            }
        } ?: CouponFilterType.UNKNOWN
    }

    private fun mapScopeTypeToMerchantType(scopeType: String?, title: String?): MerchantType {
        return scopeType?.let { scopeType ->
            if (scopeType == VERTICAL) {
                when (title) {
                    VERTICAL_CAFE -> {
                        MerchantType.CAFE
                    }
                    VERTICAL_RESTAURANTS -> {
                        MerchantType.RESTAURANT
                    }
                    VERTICAL_SUPERMARKET -> {
                        MerchantType.GROCERY
                    }
                    VERTICAL_CONVENIENCE_STORE -> {
                        MerchantType.CONVENIENCE
                    }
                    else -> {
                        MerchantType.UNKNOWN
                    }
                }
            } else {
                MerchantType.MERCHANT
            }
        } ?: MerchantType.UNKNOWN
    }

    private fun mapCouponValueByType(
        campaignElement: CampaignElement
    ): String {
        return if (campaignElement.customer_gets()?.isNotEmpty() == true) {
            campaignElement.customer_gets()!!.first().value()?.toInt().toString()
        } else {
            "0"
        }
    }

    override fun saveSelectedCoupon(couponModel: CouponModel) {
        preferenceProvider.setPreference(
            key = PREF_SELECTED_COUPON,
            value = Gson().toJson(couponModel)
        )
    }

    override fun getSelectedCoupon(): CouponModel {
        val jsonString = preferenceProvider.getPreference(
            key = PREF_SELECTED_COUPON,
            defValue = ""
        )
        return Gson().fromJson(jsonString, CouponModel::class.java)
    }

    override fun removeSelectedCoupon() {
        preferenceProvider.clearPreference(PREF_SELECTED_COUPON)
    }

    override fun hasSelectedCoupon(): Boolean {
        return preferenceProvider.getPreference(
            key = PREF_SELECTED_COUPON,
            defValue = ""
        ).isNotEmpty()
    }
}