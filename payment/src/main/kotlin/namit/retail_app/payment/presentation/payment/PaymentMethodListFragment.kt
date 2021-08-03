package namit.retail_app.payment.presentation.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.extension.visibleWhen
import namit.retail_app.core.navigation.CoreNavigator
import namit.retail_app.core.navigation.PaymentNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.core.presentation.dialog.alert.AlertMessageDialog
import namit.retail_app.core.presentation.widget.EndlessRecyclerViewScrollListener
import namit.retail_app.payment.R
import namit.retail_app.payment.presentation.adapter.PaymentMethodListAdapter
import namit.retail_app.payment.presentation.payment_type.PaymentMethodTypeListFragment
import io.sulek.ssml.SSMLLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_payment_method_list.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class PaymentMethodListFragment : BaseFragment() {

    companion object {
        const val TAG = "PaymentMethodListFragment"
        const val RESULT_CODE_SELECTED_PAYMENT_METHOD = 1001
        const val EXTRA_SELECTED_PAYMENT_METHOD = "EXTRA_SELECTED_PAYMENT_METHOD"
        fun newInstance(): PaymentMethodListFragment {
            return PaymentMethodListFragment()
        }
    }

    private val viewModel: PaymentMethodListViewModel by viewModel()
    private lateinit var paymentMethodAdapter: PaymentMethodListAdapter
    private val coreNavigator: CoreNavigator by inject()
    private val paymentNavigator: PaymentNavigator by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_payment_method_list, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewModel()
        viewModel.loadUserPaymentList()

    }

    private fun initView() {
        (activity as BaseActivity).apply {
            setToolbarTitle(title = getString(R.string.payment))
        }

        paymentMethodAdapter = PaymentMethodListAdapter().apply {
            onSelectItem = { index ->
                viewModel.selectedPaymentMethod(indexOfPaymentMethod = index)
            }
            onDeleteItem = { index ->
                activity?.supportFragmentManager?.let {
                    coreNavigator.alertQuestionDialog(
                        title = getString(R.string.are_you_sure_to_remove_your_payment_account),
                        message = getString(R.string.you_wont_be_able_to_use_it_for_wefresh_anymore),
                        positiveButtonText = getString(R.string.yes),
                        negativeButtonText = getString(R.string.no)
                    ).apply {
                        onPositionClick = {
                            viewModel.deletePaymentMethod(indexOfPaymentMethod = index)
                        }
                    }.show(it, AlertMessageDialog.TAG)
                }
            }
            onSwipeItem = { index: Int, isExpand: Boolean ->
                viewModel.updatePaymentSwiped(index, isExpand)
            }
        }

        val paymentLayoutManager = SSMLLinearLayoutManager(paymentRecyclerView.context)
        paymentRecyclerView.apply {
            adapter = paymentMethodAdapter
            layoutManager = paymentLayoutManager
        }

        addNewMethodButton.setOnClickListener {
            (activity as BaseActivity).apply {
                replaceFragment(
                    fragment = paymentNavigator.getPaymentTypeList(),
                    addToBackStack = true,
                    tag = PaymentMethodTypeListFragment.TAG
                )
            }
        }

        paymentRecyclerView.addOnScrollListener(object :
            EndlessRecyclerViewScrollListener(paymentLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                viewModel.loadUserPaymentList()
            }
        })
    }

    private fun bindViewModel() {
        viewModel.paymentList.observe(viewLifecycleOwner, Observer {
            paymentMethodAdapter.items = it
        })

        viewModel.selectedPaymentMethod.observe(viewLifecycleOwner, Observer { paymentMethod ->
            val returnIntent = Intent()
            returnIntent.putExtra(EXTRA_SELECTED_PAYMENT_METHOD, paymentMethod)
            activity?.let {
                it.setResult(Activity.RESULT_OK, returnIntent)
                it.finish()
            }
        })

        viewModel.enableAddNewPaymentButton.observe(this, Observer {
            addNewMethodButton.visibleWhen(it)
        })
    }
}