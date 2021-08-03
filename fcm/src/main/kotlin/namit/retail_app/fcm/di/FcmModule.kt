package namit.retail_app.fcm.di

import namit.retail_app.core.di.DI_GRAPHQL_APOLLO_HASURA
import namit.retail_app.core.di.DI_GRAPHQL_APOLLO_HASURA_AUTH
import namit.retail_app.fcm.data.repository.NotificationTokenRepository
import namit.retail_app.fcm.data.repository.NotificationTokenRepositoryImpl
import namit.retail_app.fcm.domain.CreateNotificationTokenUseCase
import namit.retail_app.fcm.domain.CreateNotificationTokenUseCaseImpl
import namit.retail_app.fcm.domain.UpdateNotificationTokenUseCase
import namit.retail_app.fcm.domain.UpdateNotificationTokenUseCaseImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val fcmModule = module {
    //-DI VIEW MODEL BELOW HERE
    //-DI USECASE BELOW HERE
    factory<CreateNotificationTokenUseCase> {
        CreateNotificationTokenUseCaseImpl(
            notificationTokenRepository = get()
        )
    }

    factory<UpdateNotificationTokenUseCase> {
        UpdateNotificationTokenUseCaseImpl(
            notificationTokenRepository = get()
        )
    }

    //-DI REPOSITORY BELOW HERE
    factory<NotificationTokenRepository> {
        NotificationTokenRepositoryImpl(
            apolloAuth = get(named(DI_GRAPHQL_APOLLO_HASURA_AUTH)),
            apolloUnAuth = get(named(DI_GRAPHQL_APOLLO_HASURA))
        )
    }
}