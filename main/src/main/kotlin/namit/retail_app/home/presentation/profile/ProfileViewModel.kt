package namit.retail_app.home.presentation.profile

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.domain.GetUserProfileLocalUseCase
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.utils.UseCaseResult

class ProfileViewModel(
    private val getUserProfileLocalUseCase: GetUserProfileLocalUseCase,
    private val eventTrackingManager: EventTrackingManager
): BaseViewModel() {

    val phoneNumber = MutableLiveData<String>()

    fun renderUserProfile() {
        val getUserProfileResult = getUserProfileLocalUseCase.execute()
        if (getUserProfileResult is UseCaseResult.Success) {
            val userProfile = getUserProfileResult.data!!
            phoneNumber.value = userProfile.mobile
        }
    }

    fun trackClickHelpCenter() {
        eventTrackingManager.trackHelpCenter()
    }
}