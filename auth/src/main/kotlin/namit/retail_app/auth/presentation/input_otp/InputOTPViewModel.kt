package namit.retail_app.auth.presentation.input_otp

import androidx.lifecycle.MutableLiveData
import namit.retail_app.auth.domain.GetUserProfileUserCase
import namit.retail_app.auth.domain.SendOtpUseCase
import namit.retail_app.auth.domain.VerifyOtpUseCase
import namit.retail_app.core.domain.*
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
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InputOTPViewModel(
    private val phoneNumber: String,
    private val region: String,
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val saveCurrentUserUseCase: SaveCurrentUserUseCase,
    private val getUserProfileUseCase: GetUserProfileUserCase,
    private val saveAccessTokenUseCase: SaveAccessTokenUseCase,
    private val getUuidUseCase: GetUuidUseCase,
    private val claimCartUseCase: ClaimCartUseCase,
    private val appSettingManager: AppSettingManager,
    private val deleteCurrentUserUseCase: DeleteCurrentUserUseCase,
    private val removeAccessTokenUseCase: RemoveAccessTokenUseCase,
    private val createNotificationTokenUseCase: CreateNotificationTokenUseCase,
    private val updateNotificationTokenUseCase: UpdateNotificationTokenUseCase,
    private val eventTrackingManager: EventTrackingManager
) : BaseViewModel() {

    val phoneNumberFormatted = MutableLiveData<String>()
    val sendOtpResult = MutableLiveData<Boolean>()
    val verifyOtpResult = SingleLiveEvent<Boolean>()
    val presentNext = SingleLiveEvent<Pair<String, String>>()
    val presentTermOfService = SingleLiveEvent<Unit>()
    val presentBackToLogin = SingleLiveEvent<Unit>()

    private lateinit var phoneNumberObj: Phonenumber.PhoneNumber
    private var userToken = ""

    init {
        renderPhoneNumber()
    }

    private fun renderPhoneNumber() {
        val phoneUtil = PhoneNumberUtil.getInstance()
        phoneNumberObj = phoneUtil.parse(phoneNumber, region)
        phoneNumberFormatted.value =
            phoneUtil.format(phoneNumberObj, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
    }

    fun sendOtp() {
        phoneNumberObj.apply {
            launch {
                val phoneParam = countryCode.toString() + nationalNumber
                val result = sendOtpUseCase.execute(phoneParam)
                sendOtpResult.value = result is UseCaseResult.Success
            }
        }
    }

    fun verifyOtp(otp: String) {
        phoneNumberObj.apply {
            launch {
                val uuidResult = withContext(Dispatchers.IO) {
                    getUuidUseCase.execute()
                }
                if (uuidResult is UseCaseResult.Success) {
                    val phoneParam = countryCode.toString() + nationalNumber
                    val verifyOtpCodeResult =
                        verifyOtpUseCase.execute(phoneParam, otp, uuidResult.data!!)
                    if (verifyOtpCodeResult is UseCaseResult.Success) {
                        //save token
                        userToken = verifyOtpCodeResult.data!!
                        saveAccessTokenUseCase.execute(userToken)

                        //todo [TMP] call claim cart and don't care result
                        claimCartUseCase.execute(uuidResult.data!!)
                        createFcmNotificationToken(uuidResult.data!!)

                        //save user profile keep token
                        val getUserProfileResult = getUserProfileUseCase.execute()
                        if (getUserProfileResult is UseCaseResult.Success) {
                            val profileResult = getUserProfileResult.data!!
                            eventTrackingManager.trackLoggedIn(
                                userId = profileResult.id,
                                loginMethod = TrackingValue.VALUE_LOGGED_IN_TYPE_PHONE_NUMBER,
                                phoneNumber = profileResult.mobile
                            )
                            saveCurrentUserUseCase.execute(profileResult)
                            verifyOtpResult.value = true
                        } else {
                            verifyOtpResult.value = false
                        }

                    } else {
                        verifyOtpResult.value = false
                    }
                } else {
                    verifyOtpResult.value = false
                }
            }
        }
    }

    fun userDontAgreeWithTermOfService() {
        launch {
            removeAccessTokenUseCase.execute()
            deleteCurrentUserUseCase.execute()
            presentBackToLogin.call()
        }
    }

    fun presentSelectUserLocation() {
        if (userToken.isNotEmpty()) {
            presentNext.value = Pair(phoneNumber, userToken)
        }
    }

    fun presentTermOfService() {
        presentTermOfService.call()
    }

    private suspend fun createFcmNotificationToken(uuid: String) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                task.result?.token?.let { fcmToken ->
                    launch {
                        val createResult = createNotificationTokenUseCase.execute(uuid, fcmToken)
                        if (createResult is UseCaseResult.Success) {
                            updateNotificationTokenUseCase.execute(uuid, true)
                            appSettingManager.setEnableNotification(true)
                        }
                    }
                }
            })
    }
}