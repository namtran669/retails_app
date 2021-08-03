package namit.retail_app.food.di

import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.di.DI_GRAPHQL_APOLLO_HASURA
import namit.retail_app.food.data.repository.RestaurantRepository
import namit.retail_app.food.data.repository.RestaurantRepositoryImpl
import namit.retail_app.food.domain.GetRestaurantListUseCase
import namit.retail_app.food.domain.GetRestaurantListUseCaseImpl
import namit.retail_app.food.presentation.restaurant.RestaurantDetailViewModel
import namit.retail_app.food.presentation.restaurant.menu.MenuPageViewModel
import namit.retail_app.food.presentation.vertical.FoodVerticalViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val foodModule = module {

    //-DI VIEW MODEL BELOW HERE
    viewModel {
        FoodVerticalViewModel(
            getRestaurantListUseCase = get()
        )
    }

    viewModel { (restaurantData: MerchantInfoItem) ->
        RestaurantDetailViewModel(
            restaurantData = restaurantData,
            getCategoryListUseCase = get()
        )
    }

    viewModel { (restaurantData: MerchantInfoItem, categoryData: CategoryItem) ->
        MenuPageViewModel(
            restaurantData = restaurantData,
            categoryData = categoryData,
            getProductByCategoryUseCase = get()
        )
    }

    //-DI USECASE BELOW HERE
    factory<GetRestaurantListUseCase> { GetRestaurantListUseCaseImpl(restaurantRepository = get()) }

    //-DI REPOSITORY BELOW HERE
    factory<RestaurantRepository> {
        RestaurantRepositoryImpl(
            apollo = get(
                named(
                    DI_GRAPHQL_APOLLO_HASURA
                )
            )
        )
    }
}