package namit.retail_app.payment.presentation.card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.payment.R
import kotlinx.android.synthetic.main.fragment_card_collection.*
import org.koin.android.viewmodel.ext.android.viewModel


class PaymentCardFragment : BaseFragment() {

    companion object {
        val TAG = PaymentCardFragment::class.java.simpleName
        private const val ARGUMENT_USER_ID = "argument_user_id"
        fun newInstance(userId: String = ""): PaymentCardFragment {
            val fragment = PaymentCardFragment()
            fragment.arguments = Bundle().apply {
                putString(ARGUMENT_USER_ID, userId)
            }
            return fragment
        }
    }

    private val viewModel: PaymentCardViewModel by viewModel()
    private lateinit var paymentCardAdapter: PaymentCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentCardAdapter = PaymentCardAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_card_collection, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindViewModel()

        viewModel.loadPaymentCollection(userId = arguments?.getString(ARGUMENT_USER_ID) ?: "")
    }

    private fun initView() {
        paymentCardRecycleView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        paymentCardRecycleView.adapter = paymentCardAdapter
    }

    private fun bindViewModel() {
        viewModel.paymentCollectionList.observe(viewLifecycleOwner, Observer {
            paymentCardAdapter.items = it
        })
    }

    fun refresh(userId: String) {

    }
}