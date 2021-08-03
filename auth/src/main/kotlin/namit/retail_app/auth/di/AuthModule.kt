package namit.retail_app.auth.di

import namit.retail_app.auth.data.repository.LoginRepository
import namit.retail_app.auth.data.repository.LoginRepositoryImpl
import namit.retail_app.auth.data.repository.TermOfServiceRepository
import namit.retail_app.auth.data.repository.TermOfServiceRepositoryImpl
import namit.retail_app.auth.domain.*
import namit.retail_app.auth.presentation.input_otp.InputOTPViewModel
import namit.retail_app.auth.presentation.tos.TermOfServiceViewModel
import namit.retail_app.core.di.DI_GRAPHQL_APOLLO
import namit.retail_app.core.di.DI_GRAPHQL_APOLLO_HASURA_AUTH
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authModule = module {

    //-DI VIEW MODEL BELOW HERE
    viewModel {
        TermOfServiceViewModel(
            appSettingManager = get(),
            getTermOfServiceUseCase = get()
        )
    }
    viewModel { (phoneNumber: String, region: String) ->
        InputOTPViewModel(
            phoneNumber = phoneNumber,
            region = region,
            sendOtpUseCase = get(),
            verifyOtpUseCase = get(),
            saveCurrentUserUseCase = get(),
            getUserProfileUseCase = get(),
            saveAccessTokenUseCase = get(),
            getUuidUseCase = get(),
            claimCartUseCase = get(),
            appSettingManager = get(),
            deleteCurrentUserUseCase = get(),
            removeAccessTokenUseCase = get(),
            createNotificationTokenUseCase = get(),
            updateNotificationTokenUseCase = get(),
            eventTrackingManager = get()
        )
    }

    //-DI USECASE BELOW HERE
    factory<GetTermOfServiceUseCase> { GetTermOfServiceUseCaseImpl(termOfServiceRepository = get()) }
    factory<SendOtpUseCase> { SendOtpUseCaseImpl(loginRepository = get()) }
    factory<VerifyOtpUseCase> { VerifyOtpUseCaseImpl(loginRepository = get()) }
    factory<GetUserProfileUserCase> { GetUserProfileUserCaseImpl(loginRepository = get()) }

    //-DI REPOSITORY BELOW HERE
    factory<TermOfServiceRepository> {
        TermOfServiceRepositoryImpl(
            apollo = get(named(DI_GRAPHQL_APOLLO))
        )
    }
    factory<LoginRepository> {
        LoginRepositoryImpl(
            apollo = get(named(DI_GRAPHQL_APOLLO_HASURA_AUTH))
        )
    }
}