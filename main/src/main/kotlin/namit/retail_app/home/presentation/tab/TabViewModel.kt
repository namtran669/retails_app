package namit.retail_app.home.presentation.tab

import namit.retail_app.core.domain.GetAccessTokenUseCase
import namit.retail_app.core.manager.AppSettingManager
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import kotlinx.coroutines.launch

class TabViewModel(
    private val appSettingManager: AppSettingManager,
    private val getAccessTokenUseCase: GetAccessTokenUseCase
): BaseViewModel() {

    val presentToLogin = SingleLiveEvent<Unit>()
    val presentToProfile = SingleLiveEvent<Unit>()

    fun checkUserLogin() {
        launch {
            val isUseLoginResult = getAccessTokenUseCase.execute()
            if (isUseLoginResult is UseCaseResult.Error) {
                presentToLogin.call()
            } else {
                presentToProfile.call()
            }
        }
    }
}