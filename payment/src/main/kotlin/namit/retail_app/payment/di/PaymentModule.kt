package namit.retail_app.payment.di

import namit.retail_app.core.config.OMISE_PUB_KEY
import namit.retail_app.core.di.DI_GRAPHQL_APOLLO_HASURA_AUTH
import namit.retail_app.core.extension.base64ToPlain
import namit.retail_app.payment.data.repository.DeliveryPaymentManager
import namit.retail_app.payment.data.repository.DeliveryPaymentManagerImpl
import namit.retail_app.payment.data.repository.PaymentRepository
import namit.retail_app.payment.data.repository.PaymentRepositoryImpl
import namit.retail_app.payment.domain.*
import namit.retail_app.payment.presentation.PaymentViewModel
import namit.retail_app.payment.presentation.card.PaymentCardViewModel
import namit.retail_app.payment.presentation.credit_card.AddCardDebitViewModel
import namit.retail_app.payment.presentation.payment.PaymentMethodListViewModel
import namit.retail_app.payment.presentation.payment_type.PaymentMethodTypeListViewModel
import namit.retail_app.payment.presentation.truemoney.input_phone.InputPhoneNumberViewModel
import namit.retail_app.payment.presentation.truemoney.otp.VerifyOtpForPaymentViewModel
import co.omise.android.api.Client
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val paymentModule = module {

    //VIEW MODEL

    viewModel {
        PaymentCardViewModel()
    }

    viewModel {
        AddCardDebitViewModel(omiseClient = get(), addNewCreditCardUseCase = get())
    }

    viewModel {
        PaymentMethodListViewModel(
            saveDeliveryPaymentUseCase = get(),
            getDeliveryPaymentUseCase = get(),
            getUserPaymentListUseCase = get(),
            getUserProfileLocalUseCase = get(),
            getAllowPaymentMethodListUseCase = get(),
            removeUserPaymentMethodUseCase = get()
        )
    }

    viewModel {
        PaymentMethodTypeListViewModel()
    }

    viewModel { (phoneNumber: String) ->
        VerifyOtpForPaymentViewModel(
            phoneNumber = phoneNumber
        )
    }

    viewModel { (pageTag: String) ->
        PaymentViewModel(
            pageTag = pageTag
        )
    }

    viewModel { (phoneNumber: String) ->
        InputPhoneNumberViewModel(
            phoneNumber = phoneNumber
        )
    }

    //USECASE
    factory<SaveDeliveryPaymentUseCase> { SaveDeliveryPaymentUseCaseImpl(deliveryPaymentManager = get()) }

    factory<GetDeliveryPaymentUseCase> { GetDeliveryPaymentUseCaseImpl(deliveryPaymentManager = get()) }

    factory<GetUserPaymentListUseCase> { GetUserPaymentListUseCaseImpl(paymentRepository = get()) }

    factory<GetAllowPaymentMethodListUseCase> {
        GetAllowPaymentMethodListUseCaseImpl(
            paymentRepository = get()
        )
    }

    factory<AddNewCreditCardUseCase> { AddNewCreditCardUseCaseImpl(paymentRepository = get()) }

    factory<RemoveUserPaymentMethodUseCase> { RemoveUserPaymentMethodUseCaseImpl(paymentRepository = get()) }

    //REPOSITORY
    single<DeliveryPaymentManager> { DeliveryPaymentManagerImpl() }

    factory<PaymentRepository> {
        PaymentRepositoryImpl(
            apolloAuth = get(
                named(
                    DI_GRAPHQL_APOLLO_HASURA_AUTH
                )
            )
        )
    }

    //OMISE SDK
    single { Client(OMISE_PUB_KEY.base64ToPlain()) }

}