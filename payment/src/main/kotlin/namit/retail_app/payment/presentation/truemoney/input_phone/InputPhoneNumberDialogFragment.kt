package namit.retail_app.payment.presentation.truemoney.input_phone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import namit.retail_app.core.navigation.PaymentNavigator
import namit.retail_app.core.presentation.base.BaseFullScreenDialog
import namit.retail_app.core.presentation.widget.IconWithTitleToolbar
import namit.retail_app.core.utils.PhoneNumberFormatter
import namit.retail_app.payment.R
import namit.retail_app.payment.presentation.truemoney.otp.VerifyOtpForPaymentDialogFragment
import kotlinx.android.synthetic.main.dialog_input_phone_number.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class InputPhoneNumberDialogFragment : BaseFullScreenDialog() {

    companion object {
        const val TAG = "InputPhoneNumberDialogFragment"
        private const val ARG_PHONE_NUMBER = "ARG_PHONE_NUMBER"
        fun newInstance(phoneNumber: String): InputPhoneNumberDialogFragment {
            val fragment = InputPhoneNumberDialogFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_PHONE_NUMBER, phoneNumber)
            }
            return fragment
        }
    }

    private val viewModel: InputPhoneNumberViewModel by viewModel {
        parametersOf(
            arguments?.getString(ARG_PHONE_NUMBER) ?: ""
        )
    }

    private val paymentNavigator: PaymentNavigator by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.dialog_input_phone_number, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewModel()
        viewModel.renderPhoneNumber()
    }

    private fun initView() {
        iconWithTitleToolbar.setToolbarBackImage(R.drawable.ic_close_black)
        iconWithTitleToolbar.setToolbarIcon(R.drawable.img_truemoney)
        iconWithTitleToolbar.setIconSize(
            width = resources.getDimension(R.dimen.trueMoneyWidth).toInt(),
            height = resources.getDimension(R.dimen.trueMoneyHeight).toInt()
        )
        iconWithTitleToolbar.setActionListener(onAction = object: IconWithTitleToolbar.OnActionListener{
            override fun onBackPress() {
                dismiss()
            }
        })

        confirmButton.setOnClickListener {
            viewModel.presentValidateOtp()
        }

        phoneNumberEditText.addTextChangedListener(
            PhoneNumberFormatter(
                editText = phoneNumberEditText,
                onTextChanged = {
                    viewModel.validatePhoneNumber(newPhoneNumber = phoneNumberEditText.text.toString())
                })
        )
    }

    private fun bindViewModel() {
        viewModel.userPhoneNumber.observe(viewLifecycleOwner, Observer {
            phoneNumberEditText.setText(it)
            viewModel.validatePhoneNumber(newPhoneNumber = phoneNumberEditText.text.toString())
        })

        viewModel.openValidateOtpDialog.observe(viewLifecycleOwner, Observer { phoneNumber ->
            activity?.supportFragmentManager?.let { supportFragmentManager ->
                val validateOtpDialog = paymentNavigator.getValidateOtpForPayment(
                    phoneNumber = phoneNumber
                ) as VerifyOtpForPaymentDialogFragment
                validateOtpDialog.show(supportFragmentManager, VerifyOtpForPaymentDialogFragment.TAG)
            }
        })

        viewModel.enableConfirmButton.observe(viewLifecycleOwner, Observer {
            confirmButton.isEnabled = it
        })
    }
}