package namit.retail_app.home.di

import namit.retail_app.core.di.DI_GRAPHQL_APOLLO_HASURA
import namit.retail_app.home.data.repository.WeatherRepository
import namit.retail_app.home.data.repository.WeatherRepositoryImpl
import namit.retail_app.home.domain.GetCurrentWeatherUseCase
import namit.retail_app.home.domain.GetCurrentWeatherUseCaseImpl
import namit.retail_app.home.presentation.home.HomeViewModel
import namit.retail_app.home.presentation.profile.ProfileViewModel
import namit.retail_app.home.presentation.settings.SettingsViewModel
import namit.retail_app.home.presentation.settings.language.SelectLanguageViewModel
import namit.retail_app.home.presentation.tab.TabViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val homeModule = module {

    //-DI VIEW MODEL BELOW HERE
    viewModel {
        TabViewModel(
            appSettingManager = get(),
            getAccessTokenUseCase = get()
        )
    }

    viewModel {
        HomeViewModel(
            getFoodStoryUseCase = get(),
            getAnouncementUseCase = get(),
            getWeeklyPromotionUseCase = get(),
            getMerchantByStoreIdUseCase = get(),
            getCurrentWeatherUseCase = get()
        )
    }

    viewModel {
        ProfileViewModel(
            getUserProfileLocalUseCase = get(),
            eventTrackingManager = get()
        )
    }

    viewModel {
        SelectLanguageViewModel(
            appSettingManager = get()
        )
    }

    viewModel {
        SettingsViewModel(
            appSettingManager = get(),
            getUuidUseCase = get(),
            createNotificationTokenUseCase = get(),
            updateNotificationTokenUseCase = get(),
            removeAccessTokenUseCase = get(),
            getUserProfileLocalUseCase = get(),
            eventTrackingManager = get()
        )
    }

    //-DI USECASE BELOW HERE

    factory<GetCurrentWeatherUseCase> { GetCurrentWeatherUseCaseImpl(weatherRepository = get()) }

    //-DI REPOSITORY BELOW HERE
    factory<WeatherRepository> {
        WeatherRepositoryImpl(
            apolloUnAuth = get(
                named(
                    DI_GRAPHQL_APOLLO_HASURA
                )
            )
        )
    }

    //-DI API BELOW HERE

}