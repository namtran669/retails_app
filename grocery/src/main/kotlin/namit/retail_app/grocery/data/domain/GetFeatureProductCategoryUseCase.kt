package namit.retail_app.grocery.data.domain

import android.util.Log
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.repository.ProductRepository
import namit.retail_app.core.utils.UseCaseResult

interface GetFeatureProductCategoryUseCase {
    suspend fun execute(merchantIds: List<String>): UseCaseResult<List<CategoryItem>>
}

class GetFeatureProductCategoryUseCaseImpl(private val repository: ProductRepository) : GetFeatureProductCategoryUseCase {

    companion object {
        val TAG = GetFeatureProductCategoryUseCaseImpl::class.java.simpleName
        private const val ERROR_EMPTY_CATEGORY_CASE = "ERROR_EMPTY_CATEGORY_CASE"
    }

    override suspend fun execute(merchantIds: List<String>): UseCaseResult<List<CategoryItem>> {
        return try {
            val categories = repository.getFeatureProductCategoryList(merchantIds = merchantIds)
            if (categories.isNotEmpty()) {
                UseCaseResult.Success(categories)
            } else {
                UseCaseResult.Error(Throwable(ERROR_EMPTY_CATEGORY_CASE))
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            UseCaseResult.Error(e)
        }
    }
}