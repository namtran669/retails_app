package namit.retail_app.core.extension

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

inline fun <reified T> Fragment.delegateTo(action: (T) -> Unit) {
    (targetFragment as? T)?.let {
        action(it)
        return
    }
    (parentFragment as? T)?.let {
        action(it)
        return
    }
    (activity as? T)?.let {
        action(it)
        return
    }
}

inline fun <reified T> Fragment.findDelegate(): T? {
    (targetFragment as? T)?.let {
        return it
    }
    (parentFragment as? T)?.let {
        return it
    }
    (activity as? T)?.let {
        return it
    }
    return null
}

inline fun <reified T> AppCompatActivity.findFragmentByTagExt(tag: String): T? = supportFragmentManager.findFragmentByTag(tag) as? T
