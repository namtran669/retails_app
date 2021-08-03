package namit.retail_app.core.presentation.base

import android.content.pm.ActivityInfo
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import namit.retail_app.core.extension.loadCircleImage


abstract class BaseActivity : AppCompatActivity() {

    private var toolbar: Toolbar? = null
    private var toolbarTitleTextView: TextView? = null
    private var toolbarIconImageView: ImageView? = null
    private var toolbarBackImageView: ImageView? = null
    open var containerResId: Int = 0

    fun initToolbar(
        toolbarId: Int = 0,
        toolbarIconImageViewId: Int = 0,
        toolbarTitleTextViewId: Int = 0,
        toolbarBackImageViewId: Int = 0,
        onBackButtonClick: () -> Unit = {}
    ) {
        if (toolbarId > 0) {
            toolbar = findViewById(toolbarId)
            setSupportActionBar(toolbar)
        }

        if (toolbarIconImageViewId > 0) {
            toolbarIconImageView = findViewById(toolbarIconImageViewId)
        }

        if (toolbarTitleTextViewId > 0) {
            toolbarTitleTextView = findViewById(toolbarTitleTextViewId)
        }

        if (toolbarBackImageViewId > 0) {
            findViewById<View>(toolbarBackImageViewId)?.setOnClickListener {
                onBackButtonClick.invoke()
            }
            toolbarBackImageView = findViewById(toolbarBackImageViewId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    fun setToolbarIcon(url: String) {
        toolbarIconImageView?.loadCircleImage(imageUrl = url)
    }

    fun setToolbarIcon(resourceId: Int) {
        toolbarIconImageView?.setImageResource(resourceId)
    }

    fun setToolbarIconSize(width: Int, height: Int) {
        toolbarIconImageView?.apply {
            layoutParams.width = width
            layoutParams.height = height
        }
    }

    fun setToolbarTitle(title: String) {
        toolbarTitleTextView?.text = title
    }

    fun setBackButtonImage(id: Int) {
        toolbarBackImageView?.setImageResource(id)
    }

    fun setBackButtonColor(color: Int) {
        toolbarBackImageView?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    fun showToolbarIconImage() {
        toolbarIconImageView?.visibility = View.VISIBLE
    }

    fun hideToolbarIconImage() {
        toolbarIconImageView?.visibility = View.GONE
    }

    fun showToolbarBackImageView() {
        toolbarBackImageView?.visibility = View.VISIBLE
    }

    fun hideToolbarBackImageView() {
        toolbarBackImageView?.visibility = View.GONE
    }

    fun showToolbar() {
        toolbar?.visibility = View.VISIBLE
    }

    fun hideToolbar() {
        toolbar?.visibility = View.GONE
    }

    fun replaceFragment(
        fragment: Fragment, addToBackStack: Boolean = false,
        containerId: Int = containerResId, tag: String
    ) {
        if (!isFinishing) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(containerId, fragment, tag)

            if (addToBackStack) {
                fragmentTransaction.addToBackStack(tag)
            }
            fragmentTransaction.commit()
        }
    }

    fun addFragment(
        fragment: Fragment,
        addToBackStack: Boolean,
        containerId: Int = containerResId, tag: String
    ) {
        if (!isFinishing) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(containerId, fragment, tag)
            if (addToBackStack) {
                fragmentTransaction.addToBackStack(tag)
            }
            fragmentTransaction.commit()
        }
    }

    fun clearBackStack() {
        try {
            supportFragmentManager.run {
                if (backStackEntryCount > 0) {
                    for (i in 0 until backStackEntryCount) {
                        popBackStack()
                    }
                }
            }
        } catch (e: IllegalStateException) {
        }
    }

    private fun finishOrPopBackStack(fragmentManager: FragmentManager) {
        if (fragmentManager.backStackEntryCount > 1) {
            fragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    override fun onBackPressed() {
        finishOrPopBackStack(supportFragmentManager)
    }
}