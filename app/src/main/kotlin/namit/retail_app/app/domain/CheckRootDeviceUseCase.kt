package namit.retail_app.app.domain

import android.util.Log
import namit.retail_app.core.utils.UseCaseResult
import com.scottyab.rootbeer.RootBeer
import java.lang.Exception

interface CheckRootDeviceUseCase {
    fun execute(): UseCaseResult<Boolean>
}

class CheckRootDeviceUseCaseImpl(private val rootBeer: RootBeer): CheckRootDeviceUseCase {

    companion object {
        private const val TAG = "CheckRootDeviceUseCase"
    }

    override fun execute(): UseCaseResult<Boolean> {
        return try {
            UseCaseResult.Success(rootBeer.isRooted)
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Success(false)
        }
    }
}