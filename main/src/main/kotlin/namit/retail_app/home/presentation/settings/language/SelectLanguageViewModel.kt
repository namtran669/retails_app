package namit.retail_app.home.presentation.settings.language

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.manager.AppSettingManager
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.SingleLiveEvent

class SelectLanguageViewModel(private val appSettingManager: AppSettingManager) : BaseViewModel() {

    val presentToTabActivity = SingleLiveEvent<String>()
    val renderCurrentLanguage = MutableLiveData<String>()

    fun setLanguage(language: String) {
        appSettingManager.setLanguage(language)
        presentToTabActivity.value = language
    }

    fun getLanguage() {
        renderCurrentLanguage.value = appSettingManager.getLanguage()
    }
}