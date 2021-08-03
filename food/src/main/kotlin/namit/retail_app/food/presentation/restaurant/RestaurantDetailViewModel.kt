package namit.retail_app.food.presentation.restaurant

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.domain.GetMerchantCategoryUseCase
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.LocaleUtils
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RestaurantDetailViewModel(
    private val restaurantData: MerchantInfoItem,
    private val getCategoryListUseCase: GetMerchantCategoryUseCase
) :
    BaseViewModel() {

    val restaurantInfo = MutableLiveData<MerchantInfoItem>()
    val selectedCategory = MutableLiveData<Pair<MerchantInfoItem, CategoryItem>>()
    val categoryList = MutableLiveData<List<CategoryItem>>()

    val checkGoToBottom = SingleLiveEvent<CategoryItem>()

    private var categoryListData = mutableListOf<CategoryItem>()

    init {
        restaurantInfo.value = restaurantData
    }

    fun renderRestaurantInfo() {
        restaurantData.openingPeriod?.let {
            val localeLanguage = LocaleUtils.getCurrentLanguage()
            if (!localeLanguage.equals("th", true)
                && !it.contains("am")
                && !it.contains("pm")
            ) {
                restaurantData.openingPeriod = convert24ThTo12En(it)
            }
        }

        restaurantInfo.value = restaurantData
    }

    fun loadCategoryList() {
        launch {
            val categoryListResult = getCategoryListUseCase.execute(restaurantData.id)
            if (categoryListResult is UseCaseResult.Success) {
                categoryListData = categoryListResult.data!!.toMutableList()

                //Select the first item
                categoryListData.first().isSelected = true
                categoryList.value = categoryListData
                selectedCategory.value = Pair(first = restaurantData, second = categoryListData[0])
            }
        }
    }

    fun updateCategory(checkedIndex: Int) {
        categoryListData.forEachIndexed { index, sortModel ->
            sortModel.isSelected = index == checkedIndex
        }

        selectedCategory.value =
            Pair(first = restaurantData, second = categoryListData[checkedIndex])
        categoryList.value = categoryListData
    }

    fun checkGotoBottomList() {
        selectedCategory.value?.let { checkGoToBottom.value = it.second }
    }

    private fun convert24ThTo12En(timeTh: String): String {
        val openCloseTimeTxt = timeTh.replace(" ", "").replace("""[น.]""".toRegex(), "")
        val openTime24Txt = openCloseTimeTxt.substringBefore("-")
        val closeTime24Txt = openCloseTimeTxt.substringAfter("-")

        var sdf = SimpleDateFormat("HH:mm", LocaleUtils.getCurrentLocale())
        val openTime = sdf.parse(openTime24Txt)
        val closeTime = sdf.parse(closeTime24Txt)

        sdf = SimpleDateFormat("KK:mmaa", LocaleUtils.getCurrentLocale())

        return "${sdf.format(openTime).toLowerCase(Locale.UK)} - ${sdf.format(closeTime).toLowerCase(
            Locale.UK
        )}"
    }
}