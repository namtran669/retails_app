package namit.retail_app.auth.widget

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import namit.retail_app.auth.R
import namit.retail_app.core.extension.collageWidth
import namit.retail_app.core.extension.expandWidth
import namit.retail_app.core.utils.KeyboardUtil
import namit.retail_app.core.utils.NumericKeyBoardTransformationMethod
import namit.retail_app.core.utils.PhoneNumberFormatter
import kotlinx.android.synthetic.main.view_phone_number.view.*

class PhoneNumberView constructor(context: Context, attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_phone_number, this, true)
        initView()
    }

    companion object {
        private const val LENGTH_OF_PHONE_NUMBER = 16
    }

    private var action: PhoneNumberAction? = null

    private fun initView() {
        val phoneNumberFormatter =
            PhoneNumberFormatter(editText = phoneNumberEditText, onTextChanged = {
                when (it) {
                    LENGTH_OF_PHONE_NUMBER -> {
                        enableConfirmButton()
                    }
                    0 -> {
                        hideConfirmButton()
                    }
                    else -> {
                        disableConfirmButton()
                    }
                }
            })

                phoneNumberEditText.transformationMethod = NumericKeyBoardTransformationMethod()
        phoneNumberEditText?.addTextChangedListener(phoneNumberFormatter)

        phoneNumberEditText?.setOnTouchListener { viewTouched, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                phoneNumberEditText?.setSelection(phoneNumberEditText.length())
                viewTouched.requestFocus()
                KeyboardUtil.show(context, phoneNumberEditText)
            }
            true
        }

        phoneNumberEditText?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && phoneNumberEditText.text.toString().isEmpty()) {
                hideConfirmButton()
            }
            phoneNumberEditText.setOnFocusChangeListener { _, _ ->
                if (hasFocus) {
                    if (validPhoneNumberSize()) {
                        enableConfirmButton()
                    } else {
                        disableConfirmButton()
                    }
                } else if (!hasFocus && phoneNumberEditText.text.toString().isEmpty()) {
                    hideConfirmButton()
                }
            }
            phoneNumberEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    when {
                        validPhoneNumberSize() -> {
                            enableConfirmButton()
                        }
                        phoneNumberEditText.text.toString().isEmpty() -> {
                            hideConfirmButton()
                            phoneNumberEditText.clearFocus()
                        }
                        else -> {
                            disableConfirmButton()
                        }
                    }

                    KeyboardUtil.hide(context, rootView?.windowToken)

                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }

        confirmPhoneButton.setOnClickListener {
            KeyboardUtil.hide(context, rootView?.windowToken)
            it.expandWidth(700, phoneNumberLayout.width)

            Handler().postDelayed({
                confirmPhoneButton?.text = resources.getString(R.string.sending_sms_otp_inform)
            }, 600)

            Handler().postDelayed({
                action?.onConfirmButtonClicked(phoneNumberEditText.text.toString())
            }, 800)

            Handler().postDelayed({
                resetConfirmState()
            }, 1500)
        }
    }

    private fun resetConfirmState() {
        confirmPhoneButton.apply {

            collageWidth(
                0,
                resources.getDimensionPixelOffset(R.dimen.confirm_button_width)
            )
            text = resources.getString(R.string.confirm)

        }
    }

    private fun enableConfirmButton() {
        confirmPhoneButton?.apply {
            alpha = 1.0f
            isEnabled = true
            visibility = View.VISIBLE
        }
    }

    private fun disableConfirmButton() {
        confirmPhoneButton?.apply {
            alpha = 0.3f
            isEnabled = false
            visibility = View.VISIBLE
        }
    }

    fun showConfirmButton() {
        confirmPhoneButton?.apply {
            visibility = View.VISIBLE
        }
    }

    private fun hideConfirmButton() {
        confirmPhoneButton?.apply {
            visibility = View.GONE
        }
    }

    private fun validPhoneNumberSize(): Boolean {
        return phoneNumberEditText.text.toString().length == LENGTH_OF_PHONE_NUMBER
    }

    fun addActionListener(action: PhoneNumberAction) {
        this.action = action
    }

    interface PhoneNumberAction {
        fun onConfirmButtonClicked(phoneNumber: String)
    }
}

