package namit.retail_app.cart.di

import namit.retail_app.cart.presentation.detail.CartDetailViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val cartModule = module {

    //-DI VIEW MODEL BELOW HERE

    viewModel {
        CartDetailViewModel(
            getDeliveryAddressUseCase = get(),
            getCurrentDeliveryTimeSlotDataUseCase = get(),
            getSelectedDeliveryTimeUseCase = get(),
            getCartListUseCase = get(),
            getUuidUseCase = get(),
            getMerchantByStoreIdUseCase = get(),
            addProductToCartInCartUserCase = get(),
            reduceOneProductInCartUserCase = get(),
            deleteProductUseCase = get(),
            getDeliveryPaymentUseCase = get(),
            createOrderUseCase = get(),
            getUserProfileLocalUseCase = get(),
            getDeliveryFeeUseCase = get(),
            getUserAddressListUseCase = get(),
            saveDeliveryAddressUseCase = get(),
            redeemCartUseCase = get(),
            getSelectedCouponUseCase = get(),
            saveSelectedCouponUseCase = get(),
            removeSelectedCouponUseCase = get(),
            eventTrackingManager = get(),
            saveDeliveryTimeUseCase = get()
        )
    }

    //-DI USECASE BELOW HERE

    //-DI REPOSITORY BELOW HERE

}