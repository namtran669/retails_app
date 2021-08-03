package namit.retail_app.auth.viewmodel

import namit.retail_app.auth.domain.*
import namit.retail_app.auth.presentation.input_otp.InputOTPViewModel
import namit.retail_app.core.domain.*
import namit.retail_app.core.manager.AppSettingManager
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.fcm.domain.CreateNotificationTokenUseCase
import namit.retail_app.fcm.domain.UpdateNotificationTokenUseCase
import namit.retail_app.testutils.BaseViewModelTest
import namit.retail_app.testutils.TestObserver
import namit.retail_app.testutils.testObserver
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

@ExperimentalCoroutinesApi
class InputOTPViewModelTest : BaseViewModelTest() {
    private lateinit var sendOtpResult: TestObserver<Boolean>
    private lateinit var verifyOtpResult: TestObserver<Boolean>
    private lateinit var phoneNumberFormatted: TestObserver<String>

    private val sendOtpUseCase: SendOtpUseCase = mock()
    private val verifyOtpUseCase: VerifyOtpUseCase = mock()
    private val getUserProfileUseCase: GetUserProfileUserCase = mock()
    private val saveAccessTokenUseCase: SaveAccessTokenUseCase = mock()
    private val saveCurrentUserUseCase: SaveCurrentUserUseCase = mock()
    private val appSettingManager: AppSettingManager = mock()
    private val deleteCurrentUserUseCase: DeleteCurrentUserUseCase = mock()
    private val removeAccessTokenUseCase: RemoveAccessTokenUseCase = mock()
    private val createNotificationTokenUseCase: CreateNotificationTokenUseCase = mock()
    private val updateNotificationTokenUseCase: UpdateNotificationTokenUseCase = mock()
    private val eventTrackingManager: EventTrackingManager = mock()

    private val getUuidUseCase: GetUuidUseCase = mock()
    private val claimCartUseCase: ClaimCartUseCase = mock()

    private lateinit var viewModel: InputOTPViewModel

    private val phoneNumber = "0839748189"
    private val region = "TH"

    override fun setup() {
        super.setup()
        viewModel = InputOTPViewModel(
            phoneNumber = phoneNumber,
            region = region,
            sendOtpUseCase = sendOtpUseCase,
            verifyOtpUseCase = verifyOtpUseCase,
            getUserProfileUseCase = getUserProfileUseCase,
            saveAccessTokenUseCase = saveAccessTokenUseCase,
            saveCurrentUserUseCase = saveCurrentUserUseCase,
            getUuidUseCase = getUuidUseCase,
            claimCartUseCase = claimCartUseCase,
            appSettingManager = appSettingManager,
            deleteCurrentUserUseCase = deleteCurrentUserUseCase,
            removeAccessTokenUseCase = removeAccessTokenUseCase,
            createNotificationTokenUseCase = createNotificationTokenUseCase,
            updateNotificationTokenUseCase = updateNotificationTokenUseCase,
            eventTrackingManager = eventTrackingManager
        )
        initTestObserver()
    }

    private fun initTestObserver() {
        sendOtpResult = viewModel.sendOtpResult.testObserver()
        verifyOtpResult = viewModel.verifyOtpResult.testObserver()
        phoneNumberFormatted = viewModel.phoneNumberFormatted.testObserver()
    }

    @Test
    fun sendOtp_success() = runBlocking {
        whenever(sendOtpUseCase.execute(any())).thenReturn(
            UseCaseResult.Success(
                true
            )
        )

        viewModel.sendOtp()

        Assert.assertEquals(true, sendOtpResult.observedValues[0]!!)

        //other
        assert(verifyOtpResult.observedValues.isEmpty())
    }

    @Test
    fun sendOtp_fail() = runBlocking {
        whenever(sendOtpUseCase.execute(any())).thenReturn(
            UseCaseResult.Error(Throwable(SendOtpUseCaseImpl.ERROR_SEND_OTP_CASE))
        )

        viewModel.sendOtp()

        Assert.assertEquals(false, sendOtpResult.observedValues[0]!!)

        //other
        assert(verifyOtpResult.observedValues.isEmpty())
    }

    //TODO find new way to solve it
//    @Test
//    fun verifyOtp_success() = runBlocking {
//        whenever(getUuidUseCase.execute()).thenReturn(
//            UseCaseResult.Success("UUID")
//        )
//
//        whenever(verifyOtpUseCase.execute(any(), any(), any())).thenReturn(
//            UseCaseResult.Success("test-accessToken-1234")
//        )
//
//        whenever(getUserProfileUseCase.execute()).thenReturn(
//            UseCaseResult.Success(UserModel())
//        )
//
//        viewModel.verifyOtp("123")
//
//        assert(verifyOtpResult.observedValues[0] == true)
//
//        //other
//        assert(sendOtpResult.observedValues.isEmpty())
//    }

    @Test
    fun verifyOtp_fail() = runBlocking {
        whenever(verifyOtpUseCase.execute(any(), any(), any())).thenReturn(
            UseCaseResult.Error(Throwable(VerifyOtpUseCaseImpl.ERROR_VERIFY_OTP_CASE))
        )

        viewModel.verifyOtp("123")

        assert(verifyOtpResult.observedValues[0]!!.not())

        //other
        assert(sendOtpResult.observedValues.isEmpty())
    }
}