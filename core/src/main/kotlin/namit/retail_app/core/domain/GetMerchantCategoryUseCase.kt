package namit.retail_app.core.domain

import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.repository.CategoryMerchantRepository
import namit.retail_app.core.utils.UseCaseResult

interface GetMerchantCategoryUseCase {
    suspend fun execute(merchantId: String): UseCaseResult<List<CategoryItem>>
}

class GetMerchantCategoryUseCaseImpl(private val repository: CategoryMerchantRepository) :
    GetMerchantCategoryUseCase {

    companion object {
        private const val ERROR_EMPTY_CATEGORY_CASE = "ERROR_EMPTY_CATEGORY_CASE"
    }

    override suspend fun execute(merchantId: String): UseCaseResult<List<CategoryItem>> {
        return try {
            val categories = repository.getRootCategoryList(merchantId = merchantId)
            if (categories.isNotEmpty()) {
                UseCaseResult.Success(categories)
            } else {
                UseCaseResult.Error(Throwable(ERROR_EMPTY_CATEGORY_CASE))
            }
        } catch (ex: Exception) {
            UseCaseResult.Error(ex)
        }
    }
}