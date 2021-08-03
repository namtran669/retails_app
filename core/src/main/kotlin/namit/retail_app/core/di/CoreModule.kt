package namit.retail_app.core.di

import namit.retail_app.core.config.GRAPHQL_HASURA_API_URL
import namit.retail_app.core.config.GRAPHQL_STRAPI_API_URL
import namit.retail_app.core.data.api.BaseOkHttpClientBuilder
import namit.retail_app.core.data.api.BaseUrl
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.data.graphql.ApolloBuilder
import namit.retail_app.core.data.interceptor.AuthHeaderInterceptor
import namit.retail_app.core.data.interceptor.HasuraInterceptor
import namit.retail_app.core.data.repository.*
import namit.retail_app.core.domain.*
import namit.retail_app.core.extension.base64ToPlain
import namit.retail_app.core.manager.AppSettingManager
import namit.retail_app.core.manager.AppSettingManagerImpl
import namit.retail_app.core.presentation.cart.CartViewModel
import namit.retail_app.core.presentation.dialog.delivery.DeliveryTimeViewModel
import namit.retail_app.core.presentation.filter.FilterSortBarViewModel
import namit.retail_app.core.presentation.product_detail.ProductDetailViewModel
import namit.retail_app.core.presentation.search.SearchViewModel
import namit.retail_app.core.presentation.viewmodel.CartFloatButtonViewModel
import namit.retail_app.core.provider.AdvertisingIdProvider
import namit.retail_app.core.provider.AdvertisingIdProviderImpl
import namit.retail_app.core.provider.PreferenceProvider
import namit.retail_app.core.provider.SharedPreferenceProvider
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.tracking.EventTrackingManagerImpl
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.converter.gson.GsonConverterFactory

//-DI NAME FOR WEFRESH GRAPHQL
private const val DI_GRAPHQL_STRAPI_URL = "DI_GRAPHQL_STRAPI_URL"
private const val DI_GRAPHQ_HASURAL_URL = "DI_GRAPHQL_HASURA_URL"
private const val DI_GRAPHQL_GET_TOKEN_USECASE = "DI_GRAPHQL_GET_TOKEN_USECASE"
private const val DI_AUTH_HEADER_INTERCEPTOR = "DI_AUTH_HEADER_INTERCEPTOR"
private const val DI_HASURA_INTERCEPTOR = "DI_HASURA_INTERCEPTOR"
private const val DI_OK_HTTP_STRAPI = "DI_OK_HTTP_STRAPI"
const val DI_GRAPHQL_APOLLO = "DI_GRAPHQL_APOLLO"
const val DI_GRAPHQL_APOLLO_HASURA = "DI_GRAPHQL_APOLLO_HASURA"
const val DI_GRAPHQL_APOLLO_HASURA_AUTH = "DI_GRAPHQL_APOLLO_HASURA_AUTH"

@ObsoleteCoroutinesApi
val coreModule = module {

    //-DI VIEW MODEL BELOW HERE
    viewModel { FilterSortBarViewModel() }
    viewModel { (merchantInfoItem: MerchantInfoItem) ->
        SearchViewModel(
            merchantInfoItem = merchantInfoItem,
            searchProductUseCase = get(),
            eventTrackingManager = get()
        )
    }
    viewModel { (merchantId: String) ->
        DeliveryTimeViewModel(
            merchantId = merchantId,
            getTimeSlotUseCase = get(),
            saveDeliveryTimeUseCase = get(),
            userProfileLocalUseCase = get(),
            eventTrackingManager = get(),
            getSelectedDeliveryTimeUseCase = get()
        )
    }
    viewModel { (productData: ProductItem, merchantData: MerchantInfoItem) ->
        ProductDetailViewModel(
            getRelatedProductByCategoryUseCase = get(),
            productData = productData,
            merchantData = merchantData,
            eventTrackingManager = get()
        )
    }
    viewModel { CartFloatButtonViewModel(getCartInfoUseCase = get(), getUuidUseCase = get()) }

    viewModel {
        CartViewModel(
            addProductToCartUseCase = get(),
            reduceOneProductUseCase = get(),
            getUuidUseCase = get(),
            eventTrackingManager = get()
        )
    }

    //-DI USECASE BELOW HERE
    factory<RedeemCartUseCase> { RedeemCartUseCaseImpl(cartRepository = get()) }
    factory<RemoveAccessTokenUseCase> { RemoveAccessTokenUseCaseImpl(accessTokenRepository = get()) }
    factory<SearchProductUseCase> { SearchProductUseCaseImpl(productRepository = get()) }

    factory<GetDeliveryDataUseCase> { GetDeliveryDataUseCaseImpl(timeSlotRepository = get()) }
    factory<GetRelatedProductByCategoryUseCase> { GetRelatedProductByCategoryUseCaseImpl(get()) }
    factory<GetProductByCategoryUseCase> { GetProductByCategoryUseCaseImpl(get()) }

    factory<CheckLocationPermissionUseCase> {
        CheckLocationPermissionUseCaseImpl(context = androidContext())
    }

    factory<SaveCurrentUserUseCase> {
        SaveCurrentUserUseCaseImpl(
            currentUserLocalRepository = get()
        )
    }

    factory<GetUserProfileLocalUseCase> {
        GetUserProfileLocalUseCaseImpl(
            currentUserLocalRepository = get()
        )
    }

    factory<DeleteCurrentUserUseCase> {
        DeleteCurrentUserUseCaseImpl(
            currentUserLocalRepository = get()
        )
    }

    factory<GetMerchantCategoryUseCase> {
        GetMerchantCategoryUseCaseImpl(
            repository = get()
        )
    }

    factory<GetCurrentDeliveryTimeSlotDataUseCase> {
        GetCurrentDeliveryTimeSlotDataUseCaseImpl(
            timeSlotRepository = get()
        )
    }

    factory<SaveDeliveryTimeUseCase> {
        SaveDeliveryTimeUseCaseImpl(
            deliveryTimeManager = get()
        )
    }
    factory<GetSelectedDeliveryTimeUseCase> {
        GetSelectedDeliveryTimeUseCaseImpl(
            deliveryTimeManager = get()
        )
    }
    factory<DeleteDeliveryTimeUseCase> {
        DeleteDeliveryTimeUseCaseImpl(
            deliveryTimeManager = get()
        )
    }
    factory<GetUuidUseCase> {
        GetUuidUseCaseImpl(
            uuidRepository = get()
        )
    }

    factory<AddProductToCartUseCase> {
        AddProductToCartUseCaseImpl(cartRepository = get())
    }

    factory<ReduceOneProductUseCase> {
        ReduceOneProductUseCaseImpl(cartRepository = get())
    }

    factory<DeleteProductUseCase> {
        DeleteProductUseCaseImpl(cartRepository = get())
    }

    factory<GetCartInfoUseCase> {
        GetCartInfoUseCaseImpl(cartRepository = get())
    }

    factory<GetCartInfoLocalUseCase> {
        GetCartInfoLocalUseCaseImpl(cartRepository = get())
    }

    factory<SaveAccessTokenUseCase> {
        SaveAccessTokenUseCaseImpl(get())
    }

    factory<GetAccessTokenUseCase> {
        GetAccessTokenUseCaseImpl(accessTokenRepository = get())
    }

    factory<GetMerchantByStoreIdUseCase> { GetMerchantByStoreIdUseCaseImpl(merchantRepository = get()) }

    factory<ClaimCartUseCase> { ClaimCartUseCaseImpl(cartRepository = get()) }

    factory<GetDeliveryFeeUseCase> { GetDeliveryFeeUseCaseImpl(cartRepository = get()) }

    //-DI REPOSITORY BELOW HERE
    factory<TimeSlotRepository> {
        TimeSlotRepositoryImpl(apollo = get(named(DI_GRAPHQL_APOLLO_HASURA)))
    }

    factory<ProductRepository> {
        ProductRepositoryImpl(
            get(named(DI_GRAPHQL_APOLLO_HASURA))
        )
    }

    factory<UserProfileLocalRepository> {
        UserProfileLocalRepositoryImpl(
            preferenceProvider = get()
        )
    }

    factory<CategoryMerchantRepository> {
        CategoryMerchantRepositoryImpl(
            apollo = get(
                named(DI_GRAPHQL_APOLLO_HASURA)
            )
        )
    }

    factory<UuidRepository> {
        UuidRepositoryImpl(
            advertisingIdProvider = get(),
            preferenceProvider = get()
        )
    }

    factory<AccessTokenRepository> {
        AccessTokenRepositoryImpl(preferenceProvider = get())
    }

    factory<AdvertisingIdProvider> {
        AdvertisingIdProviderImpl(context = androidContext())
    }

    single<EventTrackingManager> {
        EventTrackingManagerImpl(
            context = androidContext(),
            firebaseAnalytics = FirebaseAnalytics.getInstance(androidContext())
        )
    }
    single<DeliveryTimeManager> { DeliveryTimeManagerImpl() }

    factory<MerchantRepository> {
        MerchantRepositoryImpl(
            apollo = get(
                named(DI_GRAPHQL_APOLLO_HASURA)
            )
        )
    }

    factory<CartRepository> {
        CartRepositoryImpl(
            apollo = get(
                named(
                    DI_GRAPHQL_APOLLO_HASURA_AUTH
                )
            )
        )
    }

    //-DI API BELOW HERE
    //--APOLLO GRAPHQL

    single(named(name = DI_AUTH_HEADER_INTERCEPTOR)) {
        AuthHeaderInterceptor(
            getAccessTokenUseCase = get()
        )
    }
    single(named(name = DI_HASURA_INTERCEPTOR)) {
        HasuraInterceptor()
    }

    single {
        BaseOkHttpClientBuilder(
            hasuraInterceptor = get(named(name = DI_HASURA_INTERCEPTOR))
        ).init()
    }

    single(named(DI_OK_HTTP_STRAPI)) {
        BaseOkHttpClientBuilder().init()
    }

    //Strapi
    single(named(name = DI_GRAPHQL_APOLLO)) {
        ApolloBuilder(
            baseOkHttpClientBuilder = get(named(name = DI_OK_HTTP_STRAPI)),
            defaultBaseUrl = get(named(name = DI_GRAPHQL_STRAPI_URL))
        ).build()
    }
    single(named(name = DI_GRAPHQL_STRAPI_URL)) { BaseUrl(url = GRAPHQL_STRAPI_API_URL) }

    //Hasura
    single(named(name = DI_GRAPHQL_APOLLO_HASURA)) {
        ApolloBuilder(
            baseOkHttpClientBuilder = get(),
            defaultBaseUrl = get(named(name = DI_GRAPHQ_HASURAL_URL))
        ).build()
    }

    single(named(name = DI_GRAPHQL_APOLLO_HASURA_AUTH)) {
        ApolloBuilder(
            baseOkHttpClientBuilder = get(),
            defaultBaseUrl = get(named(name = DI_GRAPHQ_HASURAL_URL))
        ).build(interceptors = *arrayOf(get(named(name = DI_AUTH_HEADER_INTERCEPTOR))))
    }

    single<Converter.Factory> { GsonConverterFactory.create() }

    single(named(name = DI_GRAPHQ_HASURAL_URL)) { BaseUrl(url = GRAPHQL_HASURA_API_URL.base64ToPlain()) }

    //-Shared Preference
    single<AppSettingManager> { AppSettingManagerImpl(preferenceProvider = get()) }

    single<PreferenceProvider> { SharedPreferenceProvider(androidContext()) }
}