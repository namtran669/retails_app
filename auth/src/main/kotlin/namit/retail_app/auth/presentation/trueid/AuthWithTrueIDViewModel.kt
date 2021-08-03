package namit.retail_app.auth.presentation.trueid

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class AuthWithTrueIDViewModel: BaseViewModel() {

    val showLoadingView = MutableLiveData<Boolean>()

    fun loginWithTrueID(uid: String, phoneNumber: String) {
        showLoadingView.value = true
        launch {
            //TODO start authenticate with our api
        }
    }
}