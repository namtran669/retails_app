package namit.retail_app.home.presentation.home

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.domain.GetMerchantByStoreIdUseCase
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.LocaleUtils
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.home.data.entity.WeatherModel
import namit.retail_app.home.domain.GetCurrentWeatherUseCase
import namit.retail_app.story.data.entity.AnnoucementContent
import namit.retail_app.story.data.entity.StoryShelf
import namit.retail_app.story.data.repository.FoodStoryRepositoryImpl.Companion.KEY_CONTENT_FOOD_STORY
import namit.retail_app.story.domain.GetAnouncementUseCase
import namit.retail_app.story.domain.GetFoodStoryUseCase
import namit.retail_app.story.domain.GetWeeklyPromotionUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getFoodStoryUseCase: GetFoodStoryUseCase,
    private val getAnouncementUseCase: GetAnouncementUseCase,
    private val getWeeklyPromotionUseCase: GetWeeklyPromotionUseCase,
    private val getMerchantByStoreIdUseCase: GetMerchantByStoreIdUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase
) : BaseViewModel() {

    val userId = MutableLiveData<String>()
    val annoucementList = MutableLiveData<List<AnnoucementContent>>()
    val storyShelfList = MutableLiveData<List<StoryShelf>>()
    val openMerchantDetailByStoreId = SingleLiveEvent<MerchantInfoItem>()
    val isAnnoucementListLoading = MutableLiveData<Boolean>()
    val isStoryListLoading = MutableLiveData<Boolean>()
    val showLoading = MutableLiveData<Boolean>()
    val currentWeather = MutableLiveData<WeatherModel>()
    val hideWeather = MutableLiveData<Unit>()

    fun loadPaymentCollection() {
        userId.value = ""
    }

    fun loadAnnoucementList() {
        launch {
            isAnnoucementListLoading.value = true
            val annoucementListResult = getAnouncementUseCase.execute()
            if (annoucementListResult is UseCaseResult.Success) {
                annoucementList.value = annoucementListResult.data
                isAnnoucementListLoading.value = false
            }
        }
    }

    fun loadStoryList() {
        launch {
            isStoryListLoading.value = true
            val foodStoryResult = async {
                getFoodStoryUseCase.execute(KEY_CONTENT_FOOD_STORY)
            }
            val weeklyPromotionResult = async {
                getWeeklyPromotionUseCase.execute()
            }

            val foodStoryResponse = foodStoryResult.await()
            val weeklyPromotionResponse = weeklyPromotionResult.await()

            val shelfList = mutableListOf<StoryShelf>()

            if (weeklyPromotionResponse is UseCaseResult.Success) {
                shelfList.add(
                    StoryShelf().apply {
                        title = getTitleWeeklyPromotions()
                        contentList = weeklyPromotionResponse.data!!
                    }
                )
            }

            if (foodStoryResponse is UseCaseResult.Success) {
                shelfList.add(
                    StoryShelf().apply {
                        title = getTitleFoodStores()
                        contentList = foodStoryResponse.data!!
                    }
                )
            }

            storyShelfList.value = shelfList
            isStoryListLoading.value = false
        }
    }


    private fun getTitleFoodStores(): String {
        if (LocaleUtils.isThai()) {
            return "เกร็ดความรู้"
        }
        return "Stories"
    }

    private fun getTitleWeeklyPromotions(): String {
        if (LocaleUtils.isThai()) {
            return "โปรโมชั่น"
        }
        return "Promotions"
    }

    fun loadMerchantByStoreId(storeId: String) {
        launch {
            showLoading.value = true
            val merchantByStoreIdResult = getMerchantByStoreIdUseCase.execute(storeId)
            if (merchantByStoreIdResult is UseCaseResult.Success) {
                openMerchantDetailByStoreId.value = merchantByStoreIdResult.data
                showLoading.value = false
            }
        }
    }

    fun loadCurrentWeather(lat: Double, lng: Double) {
        launch {
            val weatherResult = getCurrentWeatherUseCase.execute(lat, lng)
            if (weatherResult is UseCaseResult.Success) {
                currentWeather.value = weatherResult.data!!
            } else {
                hideWeather.value = Unit
            }
        }
    }
}