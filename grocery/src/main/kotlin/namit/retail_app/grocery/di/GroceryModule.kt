package namit.retail_app.grocery.di

import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.di.DI_GRAPHQL_APOLLO_HASURA
import namit.retail_app.core.domain.GetMerchantProductUseCase
import namit.retail_app.core.domain.GetMerchantProductUseCaseImpl
import namit.retail_app.grocery.data.domain.*
import namit.retail_app.grocery.data.repository.GroceryMerchantRepository
import namit.retail_app.grocery.data.repository.GroceryMerchantRepositoryImpl
import namit.retail_app.grocery.presentation.category_all.GroceryAllCategoryViewModel
import namit.retail_app.grocery.presentation.category_detail.GroceryCategoryDetailViewModel
import namit.retail_app.grocery.presentation.category_sub.GrocerySubCategoryViewModel
import namit.retail_app.grocery.presentation.category_sub_detail.GrocerySubCategoryDetailViewModel
import namit.retail_app.grocery.presentation.main.GroceryMainViewModel
import namit.retail_app.grocery.presentation.merchant.GroceryMerchantDetailViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val groceryModule = module {

    //-DI VIEW MODEL BELOW HERE
    viewModel {
        GroceryMainViewModel(
            getMerchantListUseCase = get(),
            getFoodStoryUseCase = get(),
            getFeatureProductCategoryUseCase = get(),
            eventTrackingManager = get()
        )
    }

    viewModel { (merchantInfoData: MerchantInfoItem) ->
        GroceryMerchantDetailViewModel(
            merchantInfoData = merchantInfoData,
            getProductListUseCase = get(),
            getCurrentDeliveryTimeSlotDataUseCase = get(),
            saveDeliveryTimeUseCase = get(),
            getSelectedDeliveryTimeUseCase = get(),
            eventTrackingManager = get()
        )
    }

    viewModel { (selectedCategoryData: CategoryItem, merchantData: MerchantInfoItem) ->
        GroceryCategoryDetailViewModel(
            selectedCategoryData = selectedCategoryData,
            merchantData = merchantData,
            getProductByCategoryUseCase = get(),
            eventTrackingManager = get()
        )
    }

    viewModel { (rootCategoryData: CategoryItem, selectedCategoryData: CategoryItem) ->
        GrocerySubCategoryDetailViewModel(
            rootCategoryData = rootCategoryData,
            selectedCategoryData = selectedCategoryData,
            getProductByCategoryUseCase = get()
        )
    }

    viewModel { (rootCategoryData: CategoryItem, childCategoryData: List<CategoryItem>) ->
        GrocerySubCategoryViewModel(
            rootCategoryData = rootCategoryData,
            childCategoryData = childCategoryData.toMutableList(),
            getSubCategoryListUseCase = get(),
            eventTrackingManager = get()
        )
    }

    viewModel { (merchantInfoItem: MerchantInfoItem) ->
        GroceryAllCategoryViewModel(
            merchantInfoItem = merchantInfoItem,
            getCategoryListUseCase = get(),
            getSubCategoryListUseCase = get()
        )
    }

    //-DI USECASE BELOW HERE
    factory<GetGroceryMerchantUseCase> { GetGroceryMerchantUseCaseImpl(merchantRepository = get()) }

    factory<GetMerchantProductUseCase> {
        GetMerchantProductUseCaseImpl(
            productRepository = get()
        )
    }

    factory<GetMerchantSubCategoryUseCase> { GetMerchantSubCategoryUseCaseImpl(repository = get()) }

    factory<GetFeatureProductCategoryUseCase> { GetFeatureProductCategoryUseCaseImpl(repository = get()) }

    //-DI REPOSITORY BELOW HERE
    factory<GroceryMerchantRepository> {
        GroceryMerchantRepositoryImpl(apollo = get(named(DI_GRAPHQL_APOLLO_HASURA)))
    }
}