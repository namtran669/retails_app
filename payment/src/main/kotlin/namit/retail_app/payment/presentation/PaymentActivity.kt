package namit.retail_app.payment.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import namit.retail_app.core.navigation.PaymentNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.payment.R
import namit.retail_app.payment.presentation.payment.PaymentMethodListFragment
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PaymentActivity : BaseActivity() {

    companion object {
        private const val EXTRA_PAGE_TAG = "EXTRA_PAGE_TAG"

        fun getPaymentList(context: Context): Intent {
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtra(EXTRA_PAGE_TAG, PaymentMethodListFragment.TAG)
            return intent
        }
    }

    override var containerResId: Int = R.id.paymentFragmentContainer

    private val viewModel: PaymentViewModel by viewModel {
        parametersOf(
            intent.getStringExtra(EXTRA_PAGE_TAG)
        )
    }

    private val paymentNavigator: PaymentNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        initView()
        bindViewModel()
        viewModel.renderPage()
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
        hideToolbarIconImage()
    }

    private fun bindViewModel() {
        viewModel.openPaymentList.observe(this, Observer {
            openPaymentList()
        })
    }

    private fun openPaymentList() {
        addFragment(
            fragment = paymentNavigator.getPaymentList() as BaseFragment,
            addToBackStack = true,
            tag = PaymentMethodListFragment.TAG
        )
    }
}
