package namit.retail_app.app.presentation.splash

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import namit.retail_app.app.data.entity.FirebaseRemoteConfigKey
import namit.retail_app.app.data.entity.ForceUpdateModel
import namit.retail_app.app.data.entity.StoreClosedModel
import namit.retail_app.app.domain.CheckRootDeviceUseCase
import namit.retail_app.core.domain.GetAccessTokenUseCase
import namit.retail_app.core.domain.GetUserProfileLocalUseCase
import namit.retail_app.core.domain.GetUuidUseCase
import namit.retail_app.core.manager.AppSettingManager
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.LocaleUtils
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.fcm.domain.CreateNotificationTokenUseCase
import namit.retail_app.fcm.domain.UpdateNotificationTokenUseCase
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SplashScreenViewModel(
    private val appSettingManager: AppSettingManager,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val getUuidUseCase: GetUuidUseCase,
    private val createNotificationTokenUseCase: CreateNotificationTokenUseCase,
    private val updateNotificationTokenUseCase: UpdateNotificationTokenUseCase,
    private val getUserProfileLocalUseCase: GetUserProfileLocalUseCase,
    private val checkRootDeviceUseCase: CheckRootDeviceUseCase
) : BaseViewModel() {

    companion object {
        private const val TAG = "SplashScreenViewModel"
    }

    val presentToLogin = SingleLiveEvent<Unit>()
    val presentToSelectLocation = SingleLiveEvent<Unit>()
    val setLanguage = MutableLiveData<String>()
    val presentOnboard = SingleLiveEvent<Unit>()
    val startSplashScreen = MutableLiveData<Unit>()
    val presentStoreClosed = SingleLiveEvent<StoreClosedModel>()
    val presentForceUpdate = SingleLiveEvent<ForceUpdateModel>()
    val presentSoftUpdate = SingleLiveEvent<ForceUpdateModel>()
    val deviceIsRooted = SingleLiveEvent<Boolean>()

    fun checkRootDevice() {
        deviceIsRooted.value = checkRootDeviceUseCase.execute() is UseCaseResult.Success
    }

    fun checkUserLogin() {
        launch {
            if (appSettingManager.isFirstOpen()) {
                appSettingManager.saveFirstOpen(false)
                presentOnboard.call()
                return@launch
            }

            val isUseLoginResult = getAccessTokenUseCase.execute()
            if (isUseLoginResult is UseCaseResult.Success) {
                createFcmNotificationToken()
                presentToSelectLocation.call()
            } else {
                presentToLogin.call()
            }
        }
    }

    fun setLanguageApp() {
        if (TextUtils.equals("default", appSettingManager.getLanguage())) {
            val default: String = Locale.getDefault().language
            if (TextUtils.equals(LocaleUtils.Thai, default)) {
                appSettingManager.setLanguage(LocaleUtils.Thai)
            } else {
                appSettingManager.setLanguage(LocaleUtils.English)
            }
        }
        setLanguage.value = appSettingManager.getLanguage()
    }

    fun handleRemoteConfig(
        versionCode: Int,
        remoteVersionCode: Int,
        storeClosedData: String,
        forceUpdateData: String
    ) {
        try {
            if (storeClosedData.isNotEmpty()) {
                val storeClosedModel =
                    Gson().fromJson(storeClosedData, StoreClosedModel::class.java)
                if (storeClosedModel.status) {
                    presentStoreClosed.value = storeClosedModel
                } else {
                    if (remoteVersionCode > versionCode) {
                        val forceUpdateModel =
                            Gson().fromJson(forceUpdateData, ForceUpdateModel::class.java)
                        when (forceUpdateModel.mode) {
                            FirebaseRemoteConfigKey.FORCE_UPDATE_TYPE_FORCE -> {
                                presentForceUpdate.value = forceUpdateModel
                            }
                            FirebaseRemoteConfigKey.FORCE_UPDATE_TYPE_SOFT -> {
                                presentSoftUpdate.value = forceUpdateModel
                            }
                            else -> {
                                startSplashScreen.value = Unit
                            }
                        }
                    } else {
                        startSplashScreen.value = Unit
                    }
                }
            }
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "JsonSyntaxException", e)
            startSplashScreen.value = Unit
        }
    }

    private suspend fun createFcmNotificationToken() {
        val uuidResult = withContext(Dispatchers.IO) { getUuidUseCase.execute() }
        if (uuidResult is UseCaseResult.Success) {
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        return@OnCompleteListener
                    }
                    task.result?.token?.let { fcmToken ->
                        launch {
                            val createResult =
                                createNotificationTokenUseCase.execute(uuidResult.data!!, fcmToken)
                            val userProfileResult = getUserProfileLocalUseCase.execute()
                            //if create token successfully and user logged, update fcm token status
                            if (createResult is UseCaseResult.Success && userProfileResult is UseCaseResult.Success) {
                                updateNotificationTokenUseCase.execute(
                                    uuidResult.data!!,
                                    appSettingManager.isEnableNotification()
                                )
                            }
                        }
                    }
                })
        }
    }
}