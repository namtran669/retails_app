package namit.retail_app.core.presentation.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import namit.retail_app.core.R
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.navigation.CoreNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import org.koin.android.ext.android.inject

class SearchActivity: BaseActivity() {

    companion object {
        private const val EXTRA_MERCHANT_INFO = "EXTRA_MERCHANT_INFO"
        fun getStartIntent(context: Context,
                           merchantInfoItem: MerchantInfoItem): Intent {
            return Intent(context, SearchActivity::class.java).apply {
                putExtra(EXTRA_MERCHANT_INFO, merchantInfoItem)
            }
        }
    }

    override var containerResId: Int = R.id.searchFragmentContainer

    private val coreNavigator: CoreNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initView()
        initFragment()
    }

    private fun initView() {
        initToolbar(
            toolbarId = R.id.toolbar,
            toolbarIconImageViewId = R.id.iconImageView,
            toolbarTitleTextViewId = R.id.titleTextView,
            toolbarBackImageViewId = R.id.backImageView,
            onBackButtonClick = {
                onBackPressed()
            })
    }

    private fun initFragment() {
        replaceFragment(
            fragment = coreNavigator.getSearchFragment(
                intent.getParcelableExtra(EXTRA_MERCHANT_INFO)
            ) as SearchFragment,
            tag = SearchFragment.TAG
        )
    }
}