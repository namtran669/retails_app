package namit.retail_app.payment.presentation.truemoney.otp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import namit.retail_app.core.extension.afterTextChanged
import namit.retail_app.core.presentation.base.BaseFullScreenDialog
import namit.retail_app.core.presentation.widget.IconWithTitleToolbar
import namit.retail_app.payment.R
import kotlinx.android.synthetic.main.dialog_validate_otp_for_payment.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class VerifyOtpForPaymentDialogFragment: BaseFullScreenDialog() {

    companion object {
        const val TAG = "VerifyOtpForPaymentDialogFragment"
        private const val ARG_PHONE_NUMBER = "ARG_PHONE_NUMBER"
        fun newInstance(phoneNumber: String): VerifyOtpForPaymentDialogFragment {
            val fragment = VerifyOtpForPaymentDialogFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_PHONE_NUMBER, phoneNumber)
            }
            return fragment
        }
    }

    private val viewModel: VerifyOtpForPaymentViewModel by viewModel{
        parametersOf(
            arguments?.getString(ARG_PHONE_NUMBER) ?: ""
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.dialog_validate_otp_for_payment, null)
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

        otpCodeEditText.afterTextChanged {
            viewModel.validateOtp(otpCode = it)
        }

        confirmButton.setOnClickListener {
            dismiss()
        }
    }

    private fun bindViewModel() {
        viewModel.userPhoneNumber.observe(viewLifecycleOwner, Observer {
            phoneNumberTextView.text = it
        })

        viewModel.changeOtpTextSize.observe(viewLifecycleOwner, Observer { isNotEmpty ->
            if (isNotEmpty) {
                otpCodeEditText.textSize = 27F
                otpCodeEditText.letterSpacing = 0.3F
            } else {
                otpCodeEditText.textSize = 16F
                otpCodeEditText.letterSpacing = 0F
            }
        })

        viewModel.enableConfirmButton.observe(viewLifecycleOwner, Observer {
            confirmButton.isEnabled = it
        })
    }

}