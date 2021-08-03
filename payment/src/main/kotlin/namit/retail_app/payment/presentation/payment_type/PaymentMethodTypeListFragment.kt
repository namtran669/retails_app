package namit.retail_app.payment.presentation.payment_type

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import namit.retail_app.core.navigation.PaymentNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.payment.R
import namit.retail_app.payment.presentation.adapter.PaymentMethodTypeListAdapter
import namit.retail_app.payment.presentation.credit_card.AddCreditDebitCardDialogFragment
import namit.retail_app.payment.presentation.payment.PaymentMethodListFragment
import namit.retail_app.payment.presentation.truemoney.input_phone.InputPhoneNumberDialogFragment
import kotlinx.android.synthetic.main.fragment_payment_method_type_list.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class PaymentMethodTypeListFragment : BaseFragment() {

    companion object {
        const val TAG = "PaymentMethodTypeListFragment"
        fun newInstance(): PaymentMethodTypeListFragment {
            return PaymentMethodTypeListFragment()
        }
    }

    private val viewModel: PaymentMethodTypeListViewModel by viewModel()
    private lateinit var paymentMethodTypeListAdapter: PaymentMethodTypeListAdapter
    private val paymentNavigator: PaymentNavigator by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context)
            .inflate(R.layout.fragment_payment_method_type_list, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewModel()
        viewModel.renderPaymentTypeList()

    }

    private fun initView() {
        (activity as BaseActivity).apply {
            setToolbarTitle(title = getString(R.string.add_new_payment))
        }

        paymentMethodTypeListAdapter = PaymentMethodTypeListAdapter().apply {
            onItemClick = {
                viewModel.selectedPaymentMethod(type = it.type)
            }
        }

        paymentTypeRecyclerView.apply {
            adapter = paymentMethodTypeListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun bindViewModel() {
        viewModel.paymentTypeList.observe(viewLifecycleOwner, Observer {
            paymentMethodTypeListAdapter.items = it
        })

        viewModel.openAddTrueMoney.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let {
                val inputPhoneNumberDialog =
                    paymentNavigator.getInputPhoneNumberDialog("")
                inputPhoneNumberDialog.show(it, InputPhoneNumberDialogFragment.TAG)
            }
        })

        viewModel.openAddCreditDebitCard.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let {
                (paymentNavigator.getAddCreditCardDialog() as AddCreditDebitCardDialogFragment).apply {
                    onAddCardSuccess = {
                        (activity as BaseActivity).apply {
                            supportFragmentManager.popBackStack(
                                TAG,
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                            )
                            supportFragmentManager.popBackStack(
                                PaymentMethodListFragment.TAG,
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                            )
                            addFragment(
                                fragment = paymentNavigator.getPaymentList(),
                                addToBackStack = true,
                                tag = PaymentMethodListFragment.TAG
                            )
                        }
                    }
                }.show(it, AddCreditDebitCardDialogFragment.TAG)
            }
        })
    }
}