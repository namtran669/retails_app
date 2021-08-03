package namit.retail_app.maps.di

import android.content.Context
import android.location.LocationManager
import namit.retail_app.core.data.api.BaseOkHttpClientBuilder
import namit.retail_app.core.data.api.BaseRetrofitBuilder
import namit.retail_app.core.data.api.BaseUrl
import namit.retail_app.maps.BuildConfig
import namit.retail_app.maps.data.api.MapsApi
import namit.retail_app.maps.data.repository.PlaceRepository
import namit.retail_app.maps.data.repository.PlaceRepositoryImpl
import namit.retail_app.maps.data.repository.SearchAddressRepository
import namit.retail_app.maps.data.repository.SearchAddressRepositoryImpl
import namit.retail_app.maps.domain.*
import namit.retail_app.maps.utils.LocationProvider
import namit.retail_app.maps.utils.LocationProviderImpl
import com.patloew.rxlocation.RxLocation
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module


private const val DI_MAPS_BASE_RETROFIT = "di_maps_base_retrofit"
private const val DI_MAPS_BASE_API_URL = "di_maps_base_api_url"
private const val DU_MAPS_BASE_OK_HTTP = "di_maps_ok_http"
val mapsModule = module {

    factory<CheckLocationServiceEnableUseCase> {
        CheckLocationServiceEnableUseCaseImpl(
            locationManager = (androidContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager)
        )
    }

    factory<GetPlaceDescriptionUseCase> { GetPlaceDescriptionUseCaseImpl(placeRepository = get()) }

    factory<SearchAddressUseCase> {SearchAddressUseCaseImpl(searchRepository = get())}

    factory<PlaceRepository> { PlaceRepositoryImpl(api = get()) }

    factory<SearchAddressRepository> { SearchAddressRepositoryImpl(api = get()) }

    factory<GetLatLngLocationUseCase> {
        GetLatLngPlaceUseCaseImpl(
            placeRepository = get()
        )
    }

    single<LocationProvider> { LocationProviderImpl(rxLocation = get()) }

    single { RxLocation(androidContext()) }

    single(named(DU_MAPS_BASE_OK_HTTP)) {
        BaseOkHttpClientBuilder().init()
    }

    single(named(DI_MAPS_BASE_RETROFIT)) {
        BaseRetrofitBuilder(
            baseOkHttpClintBuilder = get(named(DU_MAPS_BASE_OK_HTTP)),
            converterFactory = get(),
            defaultBaseUrl = get(named(DI_MAPS_BASE_API_URL))
        )
    }

    single<MapsApi> { get<BaseRetrofitBuilder>(named(DI_MAPS_BASE_RETROFIT)).build() }

    single(named(DI_MAPS_BASE_API_URL)) { BaseUrl(url = BuildConfig.MAPS_URL) }
}