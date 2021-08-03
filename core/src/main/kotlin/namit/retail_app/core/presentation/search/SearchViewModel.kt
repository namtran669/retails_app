package namit.retail_app.core.presentation.search

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.FilterModel
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.data.entity.SortModel
import namit.retail_app.core.data.repository.ProductRepositoryImpl
import namit.retail_app.core.data.repository.ProductRepositoryImpl.Companion.FIRST_PAGE
import namit.retail_app.core.domain.SearchProductUseCase
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.utils.UseCaseResult
import kotlinx.coroutines.launch

class SearchViewModel(
    private val merchantInfoItem: MerchantInfoItem,
    private val searchProductUseCase: SearchProductUseCase,
    private val eventTrackingManager: EventTrackingManager
) : BaseViewModel() {

    companion object {
        private const val MINIMUM_KEYWORDS_LENGTH = 2
    }

    val scrollToTop = MutableLiveData<Unit>()
    val searchResultList = MutableLiveData<List<ProductItem>>()
    val showDefaultSearchStatus = MutableLiveData<Unit>()
    val showEmptySearchStatus = MutableLiveData<Unit>()
    val showNoMoreProductData = MutableLiveData<Boolean>()

    private val currentFilterList = mutableListOf<FilterModel>()
    private var currentSort: SortModel? = null
    private var currentProductPage: Int = FIRST_PAGE
    private val currentProductList = mutableListOf<ProductItem>()
    private var isNoMoreProductData = false
    private var currentKeyWord = ""

    fun updateFilterList(filterList: List<FilterModel>) {
        currentFilterList.clear()
        currentFilterList.addAll(filterList)
    }

    fun updateSort(sort: SortModel) {
        currentSort = sort
    }

    fun searchProduct(keywords: String) {

        val replaceKeyword = keywords.trim().replaceFirst(" ", "|$$|^")
            .replace("\\s".toRegex(), "")
        val spiltKeyword = replaceKeyword.replace("|$$|^", " ").split(" ")

        var newWord =
            spiltKeyword.toString()
                .replace("[", "").trim()
                .replace(",", "|").trim()
                .replace("]", ")").trim()
                .replace("| ", "|").trim()
                .replace(" | ", "").trim()

        val newKeyword = if (newWord.contains("|")) {
            "%(${replaceKeyword.trim().replace("|$$|^", " ")}|$newWord%"
        } else {
            "%(${replaceKeyword.trim()})%"
        }

        if (currentKeyWord != newKeyword) {
            currentProductList.clear()
            currentKeyWord = newKeyword
            isNoMoreProductData = false
            currentProductPage = FIRST_PAGE
            scrollToTop.value = Unit
        }

        if (isNoMoreProductData) {
            return
        }

        if (currentProductPage == FIRST_PAGE) {
            currentProductList.add(ProductItem())
            currentProductList.add(ProductItem())
            currentProductList.add(ProductItem())
            currentProductList.add(ProductItem())
            currentProductList.add(ProductItem())
            currentProductList.add(ProductItem())
            searchResultList.value = currentProductList
        }

        if (replaceKeyword.trim().replace("|$$|^", " ").length >= MINIMUM_KEYWORDS_LENGTH) {
            launch {
                val searchResult = searchProductUseCase.execute(
                    keywords = currentKeyWord,
                    merchantId = merchantInfoItem.id,
                    page = currentProductPage
                )
                if (searchResult is UseCaseResult.Success) {
                    val newProductList = searchResult.data!!.resultList

                    if (currentProductPage == FIRST_PAGE) {
                        currentProductList.clear()
                    }

                    if (newProductList.isEmpty()) {
                        trackSearch(foundItem = false)
                        searchResultList.value = listOf()
                        showEmptySearchStatus.value = Unit
                        return@launch
                    }

                    trackSearch(foundItem = true)
                    currentProductList.addAll(newProductList)

                    val productListForRender = mutableListOf<ProductItem>()
                    productListForRender.addAll(currentProductList)

                    if (newProductList.size < ProductRepositoryImpl.SIZE_PRODUCT_EACH_REQUEST) {
                        isNoMoreProductData = true
                        showNoMoreProductData.value = true
                    } else {
                        //Add two skeleton item on bottom
                        productListForRender.add(ProductItem())
                        productListForRender.add(ProductItem())
                    }

                    if (searchResult.data.keyword == currentKeyWord) {
                        searchResultList.value = productListForRender
                        currentProductPage++
                    }
                } else {
                    searchResultList.value = listOf()
                    showEmptySearchStatus.value = Unit
                }
            }
        } else {
            searchResultList.value = listOf()
            showDefaultSearchStatus.value = Unit
        }
    }

    private fun trackSearch(foundItem: Boolean) {
        eventTrackingManager.trackSearch(
            merchantId = merchantInfoItem.id,
            merchantName = merchantInfoItem.title,
            foundItem = foundItem
        )
    }
}