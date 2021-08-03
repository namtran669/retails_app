package namit.retail_app.grocery.presentation.main_wrapper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.navigation.GroceryNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.grocery.R
import namit.retail_app.grocery.presentation.main.GroceryMainFragment
import namit.retail_app.grocery.presentation.merchant.GroceryMerchantDetailFragment
import org.koin.android.ext.android.inject

class GroceryWrapperActivity : BaseActivity() {

    companion object {
        private const val EXTRA_MERCHANT_INFO = "EXTRA_MERCHANT_INFO"
        fun newInstance(context: Context): Intent =
            Intent(context, GroceryWrapperActivity::class.java)

        fun newInstanceWithMerchantInfo(
            context: Context,
            merchantInfoItem: MerchantInfoItem): Intent {
            return Intent(context, GroceryWrapperActivity::class.java).apply {
                putExtra(EXTRA_MERCHANT_INFO, merchantInfoItem)
            }
        }
    }

    private val groceryNavigator: GroceryNavigator by inject()
    override var containerResId: Int = R.id.tabGroceryFragmentContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grocery_wrapper)
        initView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GroceryMainFragment.REQUEST_CODE_SETTING_LOCATION) {
            val fragment = supportFragmentManager.findFragmentByTag(GroceryMainFragment.TAG)
            fragment?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun initView() {
        if (intent.hasExtra(EXTRA_MERCHANT_INFO)) {
            val merchantInfoItem = intent.getParcelableExtra<MerchantInfoItem>(EXTRA_MERCHANT_INFO)
            displaySevenEleven(merchantInfoItem = merchantInfoItem)
        } else {
            displayGroceryMain()
        }
    }

    private fun displayGroceryMain() {
        addFragment(
            fragment = groceryNavigator.getGroceryMainFragment() as GroceryMainFragment,
            tag = GroceryMainFragment.TAG,
            addToBackStack = true
        )
    }

    private fun displaySevenEleven(merchantInfoItem: MerchantInfoItem) {
        val groceryMerchantFragment = groceryNavigator.getGroceryMerchantFragment(
            merchant = merchantInfoItem
        ) as GroceryMerchantDetailFragment
        addFragment(
            fragment = groceryMerchantFragment,
            tag = GroceryMerchantDetailFragment.TAG,
            addToBackStack = true
        )
    }
}

