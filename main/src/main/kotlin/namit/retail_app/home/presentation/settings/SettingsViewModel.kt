package namit.retail_app.home.presentation.settings

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.domain.GetUserProfileLocalUseCase
import namit.retail_app.core.domain.GetUuidUseCase
import namit.retail_app.core.domain.RemoveAccessTokenUseCase
import namit.retail_app.core.manager.AppSettingManager
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.tracking.TrackingValue
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.fcm.domain.CreateNotificationTokenUseCase
import namit.retail_app.fcm.domain.UpdateNotificationTokenUseCase
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(
    private val appSettingManager: AppSettingManager,
    private val getUuidUseCase: GetUuidUseCase,
    private val createNotificationTokenUseCase: CreateNotificationTokenUseCase,
    private val updateNotificationTokenUseCase: UpdateNotificationTokenUseCase,
    private val removeAccessTokenUseCase: RemoveAccessTokenUseCase,
    private val getUserProfileLocalUseCase: GetUserProfileLocalUseCase,
    private val eventTrackingManager: EventTrackingManager
) : BaseViewModel() {

    val renderCurrentLanguage = MutableLiveData<String>()
    val renderNotificationState = MutableLiveData<Boolean>()
    val showErrorMessage = SingleLiveEvent<String>()
    val presentToTabActivity = SingleLiveEvent<Unit>()

    fun getLanguage() {
        renderCurrentLanguage.value = appSettingManager.getLanguage()
    }

    fun getNotificationState() {
        renderNotificationState.value = appSettingManager.isEnableNotification()
    }

    fun setNotificationState(isEnable: Boolean) {
        launch {
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
                                    createNotificationTokenUseCase.execute(
                                        uuidResult.data!!,
                                        fcmToken
                                    )
                                when (createResult) {
                                    is UseCaseResult.Error -> showErrorMessage.value =
                                        createResult.exception.message
                                    is UseCaseResult.Success -> {
                                        val updateResult = updateNotificationTokenUseCase.execute(
                                            uuidResult.data!!,
                                            isEnable
                                        )
                                        when (updateResult) {
                                            is UseCaseResult.Success -> changeNotificationState(
                                                isEnable
                                            )
                                            is UseCaseResult.Error -> showErrorMessage.value =
                                                updateResult.exception.message
                                        }
                                    }
                                }
                            }
                        }
                    })
            }
        }
    }

    private fun changeNotificationState(isEnable: Boolean) {
        renderNotificationState.value = isEnable
        appSettingManager.setEnableNotification(isEnable)
    }

    fun logout() {
        val userProfileResult = getUserProfileLocalUseCase.execute()
        if (userProfileResult is UseCaseResult.Success) {
            val userProfile = userProfileResult.data!!
            eventTrackingManager.trackLoggedOut(
                userId = userProfile.id,
                loginMethod = TrackingValue.VALUE_LOGGED_IN_TYPE_PHONE_NUMBER,
                phoneNumber = userProfile.mobile
            )
        }
        val isUseLoginResult = removeAccessTokenUseCase.execute()
        if (isUseLoginResult is UseCaseResult.Success) {
            presentToTabActivity.call()
        }

        setNotificationState(false)
    }
}