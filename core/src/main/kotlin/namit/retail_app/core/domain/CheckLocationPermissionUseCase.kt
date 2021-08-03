package namit.retail_app.core.domain

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import namit.retail_app.core.utils.UseCaseResult


interface CheckLocationPermissionUseCase {
    fun execute(): UseCaseResult<Boolean>
}

class CheckLocationPermissionUseCaseImpl(
    private val context: Context
): CheckLocationPermissionUseCase {

    companion object {
        private const val ERROR_MESSAGE_HAS_NOT_PERMISSION = "ERROR_MESSAGE_HAS_NOT_PERMISSION"
    }

    override fun execute(): UseCaseResult<Boolean> {
        return try {
            val hasPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
            if (hasPermission == PackageManager.PERMISSION_GRANTED){
                UseCaseResult.Success(true)
            } else {
                UseCaseResult.Error(Throwable(ERROR_MESSAGE_HAS_NOT_PERMISSION))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            UseCaseResult.Error(e)
        }
    }
}