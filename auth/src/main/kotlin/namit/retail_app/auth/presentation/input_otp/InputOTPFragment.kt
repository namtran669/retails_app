package namit.retail_app.auth.presentation.input_otp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import namit.retail_app.auth.R
import namit.retail_app.auth.presentation.tos.TermOfServiceDialogFragment
import namit.retail_app.core.extension.collageWidth
import namit.retail_app.core.extension.expandWidth
import namit.retail_app.core.navigation.AuthNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.core.utils.KeyboardUtil
import kotlinx.android.synthetic.main.fragment_input_otp.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit

class InputOTPFragment : BaseFragment() {

    companion object {
        const val TAG: String = "InputOTPFragment"
        private const val ARG_PHONE_NUMBER = "ARG_PHONE_NUMBER"
        private const val ARG_REGION = "ARG_REGION"
        private const val DISABLE_RESEND_TIMER_MS = 30000L

        fun newInstance(phoneNumber: String, region: String): InputOTPFragment {
            val fragment = InputOTPFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_PHONE_NUMBER, phoneNumber)
                putString(ARG_REGION, region)
            }
            return fragment
        }

    }

    private val viewModel: InputOTPViewModel by viewModel(parameters = {
        parametersOf(
            arguments!!.getString(ARG_PHONE_NUMBER),
            arguments!!.getString(ARG_REGION)
        )
    })

    private val authNavigator: AuthNavigator by inject()
    private var resendCountDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_input_otp, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindViewModel()

        viewModel.sendOtp()
    }

    override fun onDestroy() {
        resendCountDownTimer?.cancel()
        super.onDestroy()
    }

    private fun initView() {
        (activity as BaseActivity).apply {
            showToolbarBackImageView()
            setBackButtonColor(Color.WHITE)
        }

        //Input OTP
        inputOtpEditText.setOnTouchListener { viewTouched, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                inputOtpEditText?.setSelection(inputOtpEditText.length())
                viewTouched.requestFocus()
                KeyboardUtil.show(activity, inputOtpEditText)
            }
            true
        }

        inputOtpEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = inputOtpEditText.text.toString().length
                otpConfirmButton?.apply {
                    if (length == 6) {
                        alpha = 1.0f
                        isEnabled = true
                    } else {
                        alpha = 0.3f
                        isEnabled = false
                    }
                }

                if (length > 0) {
                    otpConfirmButton.visibility = View.VISIBLE
                    inputOtpEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32f)
                    inputOtpEditText.letterSpacing = 0.3f
                } else {
                    otpConfirmButton.visibility = View.INVISIBLE
                    inputOtpEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
                    inputOtpEditText.letterSpacing = 0.0f
                }
            }

        })

        //Confirm button
        otpConfirmButton.setOnClickListener {
            viewModel.verifyOtp(inputOtpEditText.text.toString().trim())
            //Disable until got error response from api
            it.isEnabled = false
            inputOtpEditText.isEnabled = false

            KeyboardUtil.hide(activity, otpConfirmButton?.windowToken)
        }

        //Resend button
        toggleResendButton(false)
        resendOtpTextView.setOnClickListener {
            viewModel.sendOtp()
            toggleResendButton(false)
        }

        termAndConditionTextView.setOnClickListener {
            openTermAndConditionDialog(presentToSelectLocation = false)
        }
    }

    private fun bindViewModel() {
        viewModel.phoneNumberFormatted.observe(viewLifecycleOwner, Observer {
            phoneNumOtpTextView.text = it
        })

        viewModel.sendOtpResult.observe(viewLifecycleOwner, Observer {
            countDownResendButton()
        })

        viewModel.presentNext.observe(viewLifecycleOwner, Observer { data ->
            activity?.apply {
                setResult(Activity.RESULT_OK, Intent())
                finish()
            }
        })

        viewModel.presentTermOfService.observe(viewLifecycleOwner, Observer {
            openTermAndConditionDialog(presentToSelectLocation = true)
        })

        viewModel.presentBackToLogin.observe(viewLifecycleOwner, Observer {
            activity?.onBackPressed()
        })

        viewModel.verifyOtpResult.observe(viewLifecycleOwner, Observer { isSuccess ->
            otpConfirmButton?.apply {
                expandWidth(700, inputOtpWrapper.width)

                Handler().postDelayed({
                    otpConfirmButton.text =
                        if (isSuccess) {
                            getString(R.string.thank_you_title)
                        } else {
                            getString(R.string.please_try_again)
                        }
                }, 400)

                if (isSuccess) {
                    Handler().postDelayed({
                        viewModel.presentTermOfService()
                    }, 800)
                } else {
                    Handler().postDelayed({
                        collageWidth(
                            700,
                            resources.getDimensionPixelOffset(R.dimen.confirm_button_width)
                        )
                    }, 2000)

                    Handler().postDelayed({
                        otpConfirmButton?.apply {
                            text = resources.getString(R.string.confirm)
                            isEnabled = true
                        }
                        inputOtpEditText?.isEnabled = true
                    }, 2400)
                }
            }
        })
    }

    private fun toggleResendButton(isEnable: Boolean) {
        resendOtpTextView?.apply {
            text = getString(R.string.resend_again)
            isEnabled = isEnable
            alpha = if (isEnable) {
                1.0f
            } else {
                0.5f
            }
        }
    }

    private fun countDownResendButton() {
        toggleResendButton(false)
        resendCountDownTimer = object : CountDownTimer(DISABLE_RESEND_TIMER_MS, 1000) {
            override fun onFinish() {
                toggleResendButton(true)
            }

            override fun onTick(millisUntilFinished: Long) {
                //do nothing
                resendOtpTextView?.text =
                    (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                            % TimeUnit.MINUTES.toSeconds(1)).toString()
                        .plus(" ")
                        .plus(getString(R.string.second))
            }
        }.start()
    }

    private fun openTermAndConditionDialog(presentToSelectLocation: Boolean) {
        val termOfServiceDialogFragment =
            authNavigator.getTermOfServiceDialog() as TermOfServiceDialogFragment
        termOfServiceDialogFragment.apply {
            onAccept = {
                if (presentToSelectLocation) {
                    viewModel.presentSelectUserLocation()
                }
            }

            onDontAccept = {
                if (presentToSelectLocation) {
                    viewModel.userDontAgreeWithTermOfService()
                }
            }
        }
        termOfServiceDialogFragment.show(fragmentManager, TermOfServiceDialogFragment.TAG)
    }
}