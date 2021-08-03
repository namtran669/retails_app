package namit.retail_app.story.di

import namit.retail_app.core.data.entity.BaseStoryContent
import namit.retail_app.core.di.DI_GRAPHQL_APOLLO
import namit.retail_app.story.data.repository.*
import namit.retail_app.story.domain.*
import namit.retail_app.story.presentation.details.StoryDetailDialogViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val storyModule = module {
    //-DI VIEW MODEL BELOW HERE
    viewModel { (baseStoryContent: BaseStoryContent) ->
        StoryDetailDialogViewModel(
            baseStoryContent = baseStoryContent,
            eventTrackingManager = get()
        )
    }

    //-DI USECASE BELOW HERE
    factory<GetAnouncementUseCase> {
        GetAnouncementUseCaseImpl(annoucementRepository = get())
    }

    factory<GetWeeklyPromotionUseCase> {
        GetWeeklyPromotionUseCaseImpl(weeklyPromotionRepository = get())
    }

    factory<GetFoodStoryUseCase> {
        GetFoodStoryUseCaseImpl(foodStoryRepository = get())
    }

    //-DI REPOSITORY BELOW HERE
    factory<AnnoucementRepository> {
        AnnoucementRepositoryImpl(apollo = get(named(DI_GRAPHQL_APOLLO)))
    }

    factory<WeeklyPromotionRepository> {
        WeeklyPromotionRepositoryImpl(apollo = get(named(DI_GRAPHQL_APOLLO)))
    }

    factory<FoodStoryRepository> {
        FoodStoryRepositoryImpl(apollo = get(named(DI_GRAPHQL_APOLLO)))
    }

    //-DI API BELOW HERE
}