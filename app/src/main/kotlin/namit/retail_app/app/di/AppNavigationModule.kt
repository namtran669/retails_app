package namit.retail_app.app.di

import namit.retail_app.app.navigation.*
import namit.retail_app.core.navigation.*
import org.koin.dsl.module

val appNavigationModule = module {
    single<SettingsNavigator> { SettingsNavigatorImpl() }

    single<CouponNavigator> { CouponNavigatorImpl() }

    single<CoreNavigator> { CoreNavigatorImpl() }

    single<AuthNavigator> { AuthNavigatorImpl() }

    single<MainTabNavigator> { MainTabNavigatorImpl() }

    single<HomeNavigator> { HomeNavigatorImpl() }

    single<GroceryNavigator> { GroceryNavigatorImpl() }

    single<CartNavigator> { CartNavigatorImpl() }

    single<OrderNavigator> { OrderNavigatorImpl() }

    single<NotificationNavigator> { NotificationNavigatorImpl() }

    single<AddressNavigator> { AddressNavigatorImpl() }

    single<FoodNavigator> { FoodNavigatorImpl() }

    single<PaymentNavigator> { PaymentNavigatorImpl() }

    single<StoryNavigator> { StoryNavigatorImpl() }

    single<AppNavigator> { AppNavigatorImpl() }
}