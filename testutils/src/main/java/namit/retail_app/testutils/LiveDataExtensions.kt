package namit.retail_app.testutils

import androidx.lifecycle.LiveData

fun <T> LiveData<T>.testObserver() = TestObserver<T>().also {
    observeForever(it)
}