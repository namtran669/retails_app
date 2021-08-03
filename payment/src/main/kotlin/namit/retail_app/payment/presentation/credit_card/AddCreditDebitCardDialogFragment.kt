package namit.retail_app.payment.presentation.credit_card

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import namit.retail_app.core.extension.afterTextChanged
import namit.retail_app.core.navigation.CoreNavigator
import namit.retail_app.core.presentation.base.BaseFullScreenDialog
import namit.retail_app.core.presentation.dialog.alert.AlertMessageDialog
import namit.retail_app.core.presentation.widget.IconWithTitleToolbar
import namit.retail_app.core.presentation.widget.LoadingDialog
import namit.retail_app.core.utils.*
import namit.retail_app.payment.R
import kotlinx.android.synthetic.main.dialog_add_credit_debit_card.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class AddCreditDebitCardDialogFragment : BaseFullScreenDialog() {

    companion object {
        const val TAG = "AddCreditDebitCardDialogFragment"
        fun newInstance(): AddCreditDebitCardDialogFragment {
            return AddCreditDebitCardDialogFragment()
        }
    }

    private val viewModel: AddCardDebitViewModel by viewModel()
    private val coreNavigator:CoreNavigator by inject()

    var onAddCardSuccess: () -> Unit = {}

    private val loadingDialog = coreNavigator.getLoadingDialog(haveBlurBackground = true)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.dialog_add_credit_debit_card, null)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val screenHeight = getHeightScreenSize(context)
            val height = (screenHeight * 0.96).toInt()
            setLayout(width, height)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewModel()
    }

    private fun initView() {
        iconWithTitleToolbar.setToolbarBackImage(R.drawable.ic_close_black)
        iconWithTitleToolbar.setScreenTitle(getString(R.string.add_card))
        iconWithTitleToolbar.hideIcon()
        iconWithTitleToolbar.setActionListener(
            onAction = object : IconWithTitleToolbar.OnActionListener {
                override fun onBackPress() {
                    dismiss()
                }
            })

        cardNameEditText.afterTextChanged {
            viewModel.setCardName(cardName = it)
        }

        cardNumberEditText.afterTextChanged {
            viewModel.setCardNumber(cardNumber = it)
        }

        cardNumberEditText.apply {
            transformationMethod = NumericKeyBoardTransformationMethod()
            addTextChangedListener(
                CreditCardNumberFormatter(
                    editText = cardNumberEditText,
                    onTextChanged = {
                        viewModel.setCardNumber(cardNumber = cardNumberEditText.text.toString())
                    })
            )

            setOnTouchListener { viewTouched, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    cardNumberEditText?.setSelection(cardNumberEditText.length())
                    viewTouched.requestFocus()
                    KeyboardUtil.show(context, cardNumberEditText)
                }
                true
            }
        }

        cardExpireDateEditText.apply {
            transformationMethod = NumericKeyBoardTransformationMethod()
            addTextChangedListener(
                CreditCardExpireDateFormatter(
                    editText = cardExpireDateEditText,
                    onTextChanged = {
                        viewModel.setCardExpireDate(cardExpireDate = cardExpireDateEditText.text.toString())
                    }
                )
            )

            setOnTouchListener { viewTouched, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    cardExpireDateEditText?.setSelection(cardExpireDateEditText.length())
                    viewTouched.requestFocus()
                    KeyboardUtil.show(context, cardExpireDateEditText)
                }
                true
            }
        }

        cardCvvEditText.afterTextChanged {
            viewModel.setCardCvvCode(cardCvvCode = it)
        }

        addButton.setOnClickListener {
            viewModel.validateInputData()
        }

        setDefaultSwitchButton.setOnCheckedChangeListener { _, isChecked ->
            setDefaultSwitchButton.setBackColorRes(if (isChecked) R.color.curiousBlue else R.color.tropicalBlue)
            viewModel.setDefault(isChecked)
        }
    }

    private fun bindViewModel() {
        viewModel.hideCardIcon.observe(viewLifecycleOwner, Observer {
            currentCardIconImageView.visibility = View.INVISIBLE
        })

        viewModel.cardIsJCB.observe(viewLifecycleOwner, Observer {
            currentCardIconImageView.visibility = View.VISIBLE
            currentCardIconImageView.setImageResource(R.drawable.ic_jcb_credit_card_white)
        })

        viewModel.cardIsVisa.observe(viewLifecycleOwner, Observer {
            currentCardIconImageView.visibility = View.VISIBLE
            currentCardIconImageView.setImageResource(R.drawable.ic_visa_credit_card_white)
        })

        viewModel.cardIsMasterCard.observe(viewLifecycleOwner, Observer {
            currentCardIconImageView.visibility = View.VISIBLE
            currentCardIconImageView.setImageResource(R.drawable.ic_master_credit_card_white)
        })

        viewModel.cardName.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                currentCardNameTextView.text = it
            } else {
                currentCardNameTextView.text = getString(R.string.card_name_hint)
            }
        })

        viewModel.cardNumberHide.observe(viewLifecycleOwner, Observer {
            currentCardNumberHideTextView.text = it
        })

        viewModel.cardNumber.observe(viewLifecycleOwner, Observer {
            currentCardNumberTextView.text = it
        })

        viewModel.enableAddCard.observe(viewLifecycleOwner, Observer {
            addButton.isEnabled = it
        })

        viewModel.showInvalidCardName.observe(viewLifecycleOwner, Observer { isShow ->
            if (isShow) {
                invalidNameTextView.visibility = View.VISIBLE
                cardNameViewLine.setBackgroundResource(R.color.sunsetOrange60)
            } else {
                invalidNameTextView.visibility = View.INVISIBLE
                cardNameViewLine.setBackgroundResource(R.color.botticelli60)
            }
        })

        viewModel.showInvalidCardNumber.observe(viewLifecycleOwner, Observer { isShow ->
            if (isShow) {
                invalidCardNumberTextView.visibility = View.VISIBLE
                cardNumberViewLine.setBackgroundResource(R.color.sunsetOrange60)
            } else {
                invalidCardNumberTextView.visibility = View.INVISIBLE
                cardNumberViewLine.setBackgroundResource(R.color.botticelli60)
            }
        })

        viewModel.showInvalidCardExpireDate.observe(viewLifecycleOwner, Observer { isShow ->
            if (isShow) {
                invalidExpireDateTextView.visibility = View.VISIBLE
                cardExpireDateViewLine.setBackgroundResource(R.color.sunsetOrange60)
            } else {
                invalidExpireDateTextView.visibility = View.INVISIBLE
                cardExpireDateViewLine.setBackgroundResource(R.color.botticelli60)
            }
        })

        viewModel.showInvalidCardCvvCode.observe(viewLifecycleOwner, Observer { isShow ->
            if (isShow) {
                invalidCvvTextView.visibility = View.VISIBLE
                cardCvvViewLine.setBackgroundResource(R.color.sunsetOrange60)
            } else {
                invalidCvvTextView.visibility = View.INVISIBLE
                cardCvvViewLine.setBackgroundResource(R.color.botticelli60)
            }
        })

        viewModel.dismissDialog.observe(viewLifecycleOwner, Observer {
            dismiss()
        })

        viewModel.showOtherErrorMessage.observe(this, Observer {
            activity?.supportFragmentManager?.let { fragmentManager ->
                coreNavigator.alertMessageDialog(
                    title = getString(R.string.add_new_credit_card_error_title),
                    message = it,
                    buttonText = getString(R.string.ok)
                ).show(fragmentManager, AlertMessageDialog.TAG)
            }
        })

        viewModel.isAddCardProcessing.observe(viewLifecycleOwner, Observer {
            if (it) {
                if (activity?.supportFragmentManager?.findFragmentByTag(LoadingDialog.TAG) == null) {
                    activity?.supportFragmentManager?.let { fragmentManager ->
                        loadingDialog.show(fragmentManager, LoadingDialog.TAG)
                    }
                }
            } else {
                loadingDialog.dismiss()
            }
        })

        viewModel.addNewCardSuccess.observe(this, Observer {
            dismiss()
            onAddCardSuccess.invoke()
        })
    }
}