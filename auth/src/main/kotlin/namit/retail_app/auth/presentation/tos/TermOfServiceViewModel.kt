package namit.retail_app.auth.presentation.tos

import androidx.lifecycle.MutableLiveData
import namit.retail_app.auth.domain.GetTermOfServiceUseCase
import namit.retail_app.core.manager.AppSettingManager
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.UseCaseResult
import kotlinx.coroutines.launch

class TermOfServiceViewModel(
    private val appSettingManager: AppSettingManager,
    private val getTermOfServiceUseCase: GetTermOfServiceUseCase
): BaseViewModel() {

    val termOfService = MutableLiveData<String>()

    fun loadTermOfService() {
        launch {
            val termOfServiceResult = getTermOfServiceUseCase.execute()
            if (termOfServiceResult is UseCaseResult.Success) {
                termOfService.value = termOfServiceResult.data
            }
        }
    }
}