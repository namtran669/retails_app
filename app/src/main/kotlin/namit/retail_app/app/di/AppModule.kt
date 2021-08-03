package namit.retail_app.app.di

import namit.retail_app.app.domain.CheckRootDeviceUseCase
import namit.retail_app.app.domain.CheckRootDeviceUseCaseImpl
import namit.retail_app.app.presentation.splash.SplashScreenViewModel
import com.scottyab.rootbeer.RootBeer
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel {
        SplashScreenViewModel(
            appSettingManager = get(),
            getAccessTokenUseCase = get(),
            getUuidUseCase = get(),
            createNotificationTokenUseCase = get(),
            updateNotificationTokenUseCase = get(),
            getUserProfileLocalUseCase = get(),
            checkRootDeviceUseCase = get()
        )
    }

    factory<CheckRootDeviceUseCase> {
        CheckRootDeviceUseCaseImpl(rootBeer = get())
    }

    factory {
        RootBeer(androidContext())
    }
}