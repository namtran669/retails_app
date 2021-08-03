package namit.retail_app.address.di

import namit.retail_app.address.data.repository.*
import namit.retail_app.address.domain.*
import namit.retail_app.address.presentation.broken_location.BrokenLocationViewModel
import namit.retail_app.address.presentation.edit.EditAddressBottomDialogViewModel
import namit.retail_app.address.presentation.edit_location.EditLocationViewModel
import namit.retail_app.address.presentation.manage.MyAddressDialogViewModel
import namit.retail_app.address.presentation.search.SearchLocationViewModel
import namit.retail_app.address.presentation.set_location.SetLocationViewModel
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.di.DI_GRAPHQL_APOLLO_HASURA
import namit.retail_app.core.di.DI_GRAPHQL_APOLLO_HASURA_AUTH
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val addressModule = module {

    //-DI VIEW MODEL BELOW HERE
    viewModel {
        EditLocationViewModel(
            locationProvider = get(),
            checkLocationServiceEnableUseCase = get(),
            getPlaceDescriptionUseCase = get(),
            getLatLngLocationUseCase = get(),
            getListUserAddressUseCase = get(),
            searchAddressUseCase = get(),
            getUserProfileUseCase = get(),
            checkLocationPermissionUseCase = get()
        )
    }

    viewModel {
        SetLocationViewModel(
            locationProvider = get(),
            checkLocationServiceEnableUseCase = get(),
            getPlaceDescriptionUseCase = get(),
            getDeliveryAddressUseCase = get(),
            saveDeliveryAddressUseCase = get(),
            getLatLngLocationUseCase = get(),
            getListUserAddressUseCase = get(),
            searchAddressUseCase = get(),
            getUserProfileUseCase = get(),
            checkLocationPermissionUseCase = get(),
            getMerchantListUseCase = get(),
            eventTrackingManager = get()
        )
    }

    viewModel {
        SearchLocationViewModel(
            searchAddressUseCase = get()
        )
    }

    viewModel { (addressData: AddressModel, saveToDeliveryAddress: Boolean) ->
        EditAddressBottomDialogViewModel(
            editAddressData = addressData,
            saveToDeliveryAddress = saveToDeliveryAddress,
            updateMyAddressUseCase = get(),
            createUserAddressUseCase = get(),
            getUserProfileLocalUseCase = get(),
            saveDeliveryAddressUseCase = get()
        )
    }

    viewModel {
        MyAddressDialogViewModel(
            saveDeliveryAddressUseCase = get(),
            getDeliveryAddressUseCase = get(),
            getUserAddressListUseCase = get(),
            deleteMyAddressUseCase = get(),
            getUserProfileLocalUseCase = get()
        )
    }

    viewModel {
        BrokenLocationViewModel(
            eventTrackingManager = get()
        )
    }

    //-DI USECASE BELOW HERE

    factory<SaveDeliveryAddressUseCase> { SaveDeliveryAddressUseCaseImpl(deliveryAddressManager = get()) }

    factory<GetDeliveryAddressUseCase> { GetDeliveryAddressUseCaseImpl(deliveryAddressManager = get()) }

    factory<CreateMyAddressUseCase> { CreateMyAddressUseCaseImpl(addressRepository = get()) }

    factory<UpdateMyAddressUseCase> { UpdateMyAddressUseCaseImpl(addressRepository = get()) }

    factory<DeleteMyAddressUseCase> { DeleteUserAddressUseCaseImpl(addressRepository = get()) }

    factory<GetUserAddressListUseCase> { GetUserAddressListUseCaseImpl(addressRepository = get()) }

    factory<GetMerchantListUseCase> { GetMerchantListUseCaseImpl(merchantRepository = get()) }

    //-DI REPOSITORY BELOW HERE

    single<DeliveryAddressManager> { DeliveryAddressManagerImpl() }

    //-DI REPOSITORY BELOW HERE
    factory<AddressRepository> {
        AddressRepositoryImpl(
            apollo = get(named(DI_GRAPHQL_APOLLO_HASURA_AUTH))
        )
    }

    factory<MerchantListRepository> {
        MerchantListRepositoryImpl(
            apollo = get(named(DI_GRAPHQL_APOLLO_HASURA))
        )
    }
}