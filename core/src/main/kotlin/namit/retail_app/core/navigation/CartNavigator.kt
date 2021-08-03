package namit.retail_app.core.navigation

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

interface CartNavigator {
    fun getCartDetailFragment(): Fragment

    fun getCartActivity(context: Context): Intent
}