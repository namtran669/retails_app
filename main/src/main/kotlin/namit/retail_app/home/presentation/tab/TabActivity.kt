package namit.retail_app.home.presentation.tab

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import namit.retail_app.core.navigation.AuthNavigator
import namit.retail_app.core.navigation.CouponNavigator
import namit.retail_app.core.navigation.HomeNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.fcm.AppFirebaseMessagingService
import namit.retail_app.home.R
import namit.retail_app.home.presentation.coupon.CouponFragment
import namit.retail_app.home.presentation.home.HomeFragment
import namit.retail_app.home.presentation.profile.ProfileFragment
import namit.retail_app.order.presentation.order_list.OrderListFragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import kotlinx.android.synthetic.main.activity_tab.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel


class TabActivity : BaseActivity() {

    companion object {
        private const val REQUEST_CODE_OPEN_LOGIN = 3001
        const val REQUEST_CODE_SETTING = 3002
        const val EXTRA_LOG_OUT = "EXTRA_KEY_LOG_OUT"
        fun getStartIntent(context: Context): Intent = Intent(context, TabActivity::class.java)
    }

    private val viewModel: TabViewModel by viewModel()

    private val homeNavigator: HomeNavigator by inject()
    private val couponNavigator: CouponNavigator by inject()
    private val authNavigator: AuthNavigator by inject()

    override var containerResId: Int = R.id.tabFragmentContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        renderFragment(tag = HomeFragment.TAG)
        initTabAction()
        bindViewModel()
        createNotificationChannel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_OPEN_LOGIN) {
            clearAliveFragment()
            viewModel.checkUserLogin()
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_SETTING) {
            if (data?.hasExtra(EXTRA_LOG_OUT) == true &&
                data.getBooleanExtra(EXTRA_LOG_OUT, false)) {
                startActivity(
                    authNavigator.openLogin(this)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun setCurrentTab(resourceId: Int) {
        bottomNavigationView.selectedItemId = resourceId
    }

    fun renderFragment(tag: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val addedFragment = supportFragmentManager.findFragmentByTag(tag)
        if (addedFragment?.isAdded == true) {
            supportFragmentManager.fragments.forEach { fragment ->
                if (fragment.tag == tag) {
                    fragmentTransaction.show(fragment)
                    if (tag == HomeFragment.TAG) {
                        (fragment as HomeFragment).getCurrentLocation()
                    }
                } else {
                    fragmentTransaction.hide(fragment)
                }
            }
        } else {
            val newFragment = when (tag) {
                HomeFragment.TAG -> {
                    homeNavigator.getHomeFragment()
                }
                OrderListFragment.TAG -> {
                    homeNavigator.getOrderFragment()
                }
                ProfileFragment.TAG -> {
                    homeNavigator.getProfileFragment()
                }
                else -> {
                    couponNavigator.getCouponFragment()
                }
            }
            fragmentTransaction.add(containerResId, newFragment, tag)
        }
        fragmentTransaction.commit()
    }

    private fun initTabAction() {
        bottomNavigationView.itemIconTintList = null
        bottomNavigationView.setOnNavigationItemSelectedListener {
            var value = true
            when (it.itemId) {
                R.id.actionHome -> {
                    renderFragment(tag = HomeFragment.TAG)
                }
                R.id.actionCoupon -> {
                    renderFragment(tag = CouponFragment.TAG)
                }
                R.id.actionOrders -> {
                    renderFragment(tag = OrderListFragment.TAG)
                }
                R.id.actionProfile -> {
                    viewModel.checkUserLogin()
                }
                else -> value = false
            }
            value
        }
    }

    private fun clearAliveFragment() {
        supportFragmentManager.fragments.forEach { fragment ->
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        }
    }

    private fun bindViewModel() {
        viewModel.presentToLogin.observe(this, Observer {
            startActivityForResult(authNavigator.openLogin(context = this), REQUEST_CODE_OPEN_LOGIN)
        })

        viewModel.presentToProfile.observe(this, Observer {
            renderFragment(tag = ProfileFragment.TAG)
        })
    }

    private fun addBadgeToTaskIcon(taskIndex: Int) {
        val menuView: BottomNavigationMenuView =
            bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        val itemView: BottomNavigationItemView =
            menuView.getChildAt(taskIndex) as BottomNavigationItemView

        val badgeCircle = LayoutInflater.from(this)
            .inflate(R.layout.notification_badge_circle_view, menuView, false)
        itemView.addView(badgeCircle)
        itemView.findViewById<TextView>(R.id.tv_badge_number).visibility = View.GONE
    }

    private fun updateBadgeToTaskIcon(taskIndex: Int, number: Int) {
        val menuView: BottomNavigationMenuView =
            bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        val itemView: BottomNavigationItemView =
            menuView.getChildAt(taskIndex) as BottomNavigationItemView

        val badgeTextView = itemView.findViewById<TextView>(R.id.tv_badge_number)
        badgeTextView?.apply {
            if (number > 0) {
                badgeTextView.text = if (number > 99) "99+" else number.toString()
                badgeTextView.visibility = View.VISIBLE
            } else {
                badgeTextView.text = ""
                badgeTextView.visibility = View.GONE
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = AppFirebaseMessagingService.RETAIL_APP_CHANNEL
            val descriptionText = AppFirebaseMessagingService.RETAIL_CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                AppFirebaseMessagingService.RETAIL_CHANNEL_ID,
                name, importance
            ).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
