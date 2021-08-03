package namit.retail_app.coupon.di

import namit.retail_app.core.data.entity.CouponModel
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.di.DI_GRAPHQL_APOLLO_HASURA
import namit.retail_app.coupon.data.repository.CouponRepository
import namit.retail_app.coupon.data.repository.CouponRepositoryImpl
import namit.retail_app.coupon.domain.*
import namit.retail_app.coupon.presentation.coupon.CouponViewModel
import namit.retail_app.coupon.presentation.detail.CouponDetailViewModel
import namit.retail_app.coupon.presentation.dialog.CouponMerchantListViewModel
import namit.retail_app.coupon.presentation.promo_code.PromoCodeViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val couponModule = module {

    //-DI VIEW MODEL BELOW HERE
    viewModel { (merchantList: List<MerchantInfoItem>, cartId: Int) ->
        CouponMerchantListViewModel(
            merchantList = merchantList,
            cartId = cartId
        )
    }

    viewModel { (couponModel: CouponModel) ->
        CouponDetailViewModel(couponModel = couponModel, eventTrackingManager = get())
    }

    viewModel {
        CouponViewModel(
            getAllCouponListUseCase = get(),
            getCouponFilterListUseCase = get(),
            getCouponListByMerchantUseCase = get(),
            getCouponListByVerticalUseCase = get(),
            saveSelectedCouponUseCase = get(),
            getMerchantByStoreIdUseCase = get(),
            getCouponListWithCartUseCase = get()
        )
    }

    viewModel { (cartId: Int) ->
        PromoCodeViewModel(cartId = cartId, redeemCartUseCase = get())
    }

    //-DI USECASE BELOW HERE
    factory<GetCouponListWithCartUseCase> {
        GetCouponListWithCartUseCaseImpl(couponRepository = get())
    }
    factory<RemoveSelectedCouponUseCase> {
        RemoveSelectedCouponUseCaseImpl(couponRepository = get())
    }
    factory<GetSelectedCouponUseCase> {
        GetSelectedCouponUseCaseImpl(couponRepository = get())
    }
    factory<SaveSelectedCouponUseCase> {
        SaveSelectedCouponUseCaseImpl(couponRepository = get())
    }
    factory<GetCouponFilterListUseCase> {
        GetCouponFilterListUseCaseImpl(couponRepository = get())
    }
    factory<GetCouponListByVerticalUseCase> {
        GetCouponListByVerticalUseCaseImpl(couponRepository = get())
    }
    factory<GetCouponListByMerchantUseCase> {
        GetCouponListByMerchantUseCaseImpl(couponRepository = get())
    }
    factory<GetAllCouponListUseCase> {
        GetAllCouponListUseCaseImpl(couponRepository = get())
    }

    //-DI REPOSITORY BELOW HERE
    factory<CouponRepository> {
        CouponRepositoryImpl(
            apollo = get(named(DI_GRAPHQL_APOLLO_HASURA)),
            preferenceProvider = get()
        )
    }

    //-DI API BELOW HERE

}