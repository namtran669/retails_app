package namit.retail_app.core.utils

sealed class RepositoryResult<out T : Any?> {
    class Success<out T : Any>(val data: T?) : RepositoryResult<T>()
    class Error(val message: String) : RepositoryResult<Nothing>()
}