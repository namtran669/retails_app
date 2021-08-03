package namit.retail_app.core.extension

import android.util.Log
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun CoroutineScope.timer(interval: Long, action: suspend TimerScope.() -> Unit): Job {
    return launch {
        val scope = TimerScope()

        while (true) {
            val time = measureTimeMillis {
                try {
                    action(scope)
                } catch (ex: Exception) {
                    Log.e("Coroutine Timer", ex.localizedMessage, ex)
                }
            }

            if (scope.isCanceled) {
                break
            }

            delay(interval)
            yield()
        }
    }
}

class TimerScope {
    var isCanceled: Boolean = false
        private set

    fun cancel() {
        isCanceled = true
    }
}

fun CoroutineScope.start(block: suspend CoroutineScope.() -> Unit) {
    launch(block = block)
}