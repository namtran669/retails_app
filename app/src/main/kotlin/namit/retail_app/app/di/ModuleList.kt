package namit.retail_app.app.di

import namit.retail_app.address.di.addressModule
import namit.retail_app.auth.di.authModule
import namit.retail_app.cart.di.cartModule
import namit.retail_app.core.di.coreModule
import namit.retail_app.coupon.di.couponModule
import namit.retail_app.fcm.di.fcmModule
import namit.retail_app.food.di.foodModule
import namit.retail_app.grocery.di.groceryModule
import namit.retail_app.home.di.homeModule
import namit.retail_app.maps.di.mapsModule
import namit.retail_app.order.di.orderModule
import namit.retail_app.payment.di.paymentModule
import namit.retail_app.story.di.storyModule

private val featureModules = listOf(
    appModule,
    homeModule,
    paymentModule,
    groceryModule,
    storyModule,
    couponModule,
    cartModule,
    authModule,
    orderModule,
    mapsModule,
    addressModule,
    foodModule,
    fcmModule
)

val koinModuleList = listOf(
    coreModule,
    appNavigationModule
) + featureModules