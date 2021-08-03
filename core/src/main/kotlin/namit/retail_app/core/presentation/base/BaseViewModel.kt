package namit.retail_app.core.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

open class BaseViewModel: ViewModel(), CoroutineScope {

    companion object {
        const val DELAY_TO_RENDER_DATA = 300L
    }

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.Main

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}