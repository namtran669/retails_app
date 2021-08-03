package namit.retail_app.order.di

import namit.retail_app.core.data.entity.OrderModel
import namit.retail_app.core.data.entity.OrderType
import namit.retail_app.core.di.DI_GRAPHQL_APOLLO_HASURA_AUTH
import namit.retail_app.order.data.repository.OrderRepository
import namit.retail_app.order.data.repository.OrderRepositoryImpl
import namit.retail_app.order.domain.*
import namit.retail_app.order.presentation.order_detail.OrderDetailViewModel
import namit.retail_app.order.presentation.order_list.OrderListChildViewModel
import namit.retail_app.order.presentation.tracking.TrackingOrderViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val orderModule = module {

    //-DI VIEW MODEL BELOW HERE
    viewModel { (orderData: OrderModel) ->
        TrackingOrderViewModel(
            orderData = orderData,
            getOrderStatusUseCase = get(),
            cancelOrderUseCase = get()
        )
    }

    viewModel { (orderType: OrderType) ->
        OrderListChildViewModel(
            orderType = orderType,
            getListOrderUseCase = get()
        )
    }

    viewModel { (orderData: OrderModel) -> OrderDetailViewModel(orderData = orderData) }

    //-DI USECASE BELOW HERE

    factory<CreateOrderUseCase> { CreateOrderUseCaseImpl(orderRepository = get()) }

    factory<GetListOrderUseCase> { GetListOrderUseCaseImpl(orderRepository = get()) }

    factory<GetOrderStatusUseCase> { GetOrderStatusUseCaseImpl(orderRepository = get()) }

    factory<CancelOrderUseCase> { CancelOrderUseCaseImpl(orderRepository = get()) }

    //-DI REPOSITORY BELOW HERE

    factory<OrderRepository> { OrderRepositoryImpl(apolloAuth = get(named(name = DI_GRAPHQL_APOLLO_HASURA_AUTH))) }
}