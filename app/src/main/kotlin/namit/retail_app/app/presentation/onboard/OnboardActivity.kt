package namit.retail_app.app.presentation.onboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import namit.retail_app.R
import namit.retail_app.app.enums.OnboardType
import namit.retail_app.core.extension.gone
import namit.retail_app.core.extension.visible
import namit.retail_app.core.navigation.AppNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_onboard_screen.*
import org.koin.android.ext.android.inject

class OnboardActivity : BaseActivity() {
    companion object {
        const val STORE_LIST_FRAGMENT_POSITION = 0
        const val COUPON_LIST_FRAGMENT_POSITION = 1
        const val PAY_LIST_FRAGMENT_POSITION = 2
        const val UPDATE_LIST_FRAGMENT_POSITION = 3

        fun openActivity(context: Context): Intent {
            return Intent(context, OnboardActivity::class.java)
        }
    }

    private val appNavigator: AppNavigator by inject()

    private lateinit var viewPagerAdapter: OnboardPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard_screen)

        initView()
        initViewPager()
    }

    private fun initView() {
        backImageView.setOnClickListener { goToPreviousPage() }

        nextButton.setOnClickListener { goToNextPage() }

        skipTextView.setOnClickListener { finishContinueStep() }
    }

    override fun onBackPressed() {
        goToPreviousPage()
    }

    private fun initViewPager() {
        viewPagerAdapter = OnboardPagerAdapter(supportFragmentManager)
        viewPagerAdapter.apply {
            addFrag(appNavigator.getOnBoardChildFragment(OnboardType.STORE.value))
            addFrag(appNavigator.getOnBoardChildFragment(OnboardType.COUPON.value))
            addFrag(appNavigator.getOnBoardChildFragment(OnboardType.PAY.value))
            addFrag(appNavigator.getOnBoardChildFragment(OnboardType.UPDATE.value))
        }

        onboardViewPager.apply {
            adapter = viewPagerAdapter
            offscreenPageLimit = viewPagerAdapter.count
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                    //do nothing
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    //do nothing
                }

                override fun onPageSelected(position: Int) {
                    handleIndicator(position)
                }
            })
        }
    }

    private fun handleIndicator(position: Int) {
        indicatorStoreView.focusIndicator(false)
        indicatorCouponView.focusIndicator(false)
        indicatorPayView.focusIndicator(false)
        indicatorUpdateView.focusIndicator(false)

        when (position) {
            STORE_LIST_FRAGMENT_POSITION -> {
                backImageView.gone()
                indicatorStoreView.focusIndicator(true)
            }
            COUPON_LIST_FRAGMENT_POSITION -> {
                backImageView.visible()
                indicatorCouponView.focusIndicator(true)
            }
            PAY_LIST_FRAGMENT_POSITION -> {
                backImageView.visible()
                indicatorPayView.focusIndicator(true)
            }
            UPDATE_LIST_FRAGMENT_POSITION -> {
                backImageView.visible()
                indicatorUpdateView.focusIndicator(true)
            }
        }
    }

    private fun View.focusIndicator(isFocus: Boolean) {
        val backgroundRes = if (isFocus) {
            R.drawable.bg_dodgerbule_2_radius
        } else {
            R.drawable.bg_mischka_2_radius
        }
        setBackgroundResource(backgroundRes)
    }

    private fun goToNextPage() {
        if (onboardViewPager.currentItem == UPDATE_LIST_FRAGMENT_POSITION) {
            finishContinueStep()
        } else {
            onboardViewPager.currentItem += 1
        }
    }

    private fun goToPreviousPage() {
        if (onboardViewPager.currentItem == STORE_LIST_FRAGMENT_POSITION) {
            finishAffinity()
        } else {
            onboardViewPager.currentItem -= 1
        }
    }

    private fun finishContinueStep() {
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }
}