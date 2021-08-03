package namit.retail_app.coupon.presentation.promo_code

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import namit.retail_app.core.data.entity.RedeemCart
import namit.retail_app.core.extension.afterTextChanged
import namit.retail_app.core.navigation.CoreNavigator
import namit.retail_app.core.presentation.base.BaseFullScreenDialog
import namit.retail_app.core.presentation.widget.LoadingDialog
import namit.retail_app.coupon.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_promo_code.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ObsoleteCoroutinesApi
class PromoCodeDialog : BaseFullScreenDialog() {
    companion object {
        val TAG: String = PromoCodeDialog::class.java.simpleName
        private const val TIME_SHOW_ERROR_BUTTON = 2000L
        private const val ARG_CART_ID = "ARG_CART_ID"

        fun newInstance(cartId: Int): PromoCodeDialog {
            val fragment = PromoCodeDialog()
            fragment.arguments = Bundle().apply {
                putInt(ARG_CART_ID, cartId)
            }
            return fragment
        }
    }

    private val viewModel: PromoCodeViewModel by viewModel(parameters = {
        parametersOf(arguments?.getInt(ARG_CART_ID))
    })

    private val coreNavigator: CoreNavigator by inject()
    var onRedeemSuccess: (RedeemCart) -> Unit = {}

    private var loadingDialog = coreNavigator.getLoadingDialog(haveBlurBackground = true)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.dialog_promo_code, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindViewModel()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    private fun initView() {
        iconCloseImageView.setOnClickListener { dismiss() }

        inputOtpEditText.apply {
            filters = arrayOf(InputFilter.AllCaps())
            afterTextChanged {
                viewModel.setCurrentPromoCode(it.trim())
            }
        }
        redeemCodeButton.setOnClickListener {
            viewModel.redeemCode()
        }
        changeButtonToExpireCode(true, resources.getString(R.string.redeem))
    }


    private fun bindViewModel() {
        viewModel.redeemSuccess.observe(viewLifecycleOwner, Observer {
                onRedeemSuccess.invoke(it)
                dismiss()
        })

        viewModel.redeemFail.observe(viewLifecycleOwner, Observer {
            changeButtonToExpireCode(false, resources.getString(R.string.sorry_invalid_code))
            Handler().postDelayed({
                if (isAdded) {
                    changeButtonToExpireCode(true, resources.getString(R.string.redeem))
                }
            }, TIME_SHOW_ERROR_BUTTON)
        })

        viewModel.isRedeemProcessing.observe(viewLifecycleOwner, Observer {
            showLoadingDialog(it)
        })

        viewModel.showErrorMessage.observe(viewLifecycleOwner, Observer {
            showSnackBar(it, Snackbar.LENGTH_SHORT)
        })
    }

    private fun changeButtonToExpireCode(isValid: Boolean, message: String) {
        redeemCodeButton?.apply {
            isEnabled = isValid
            if (isValid) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    redeemCodeButton.setTextAppearance(R.style.MaterialTextButton_H1_KournikovaDodgerBlue)
                } else {
                    redeemCodeButton.setTextAppearance(
                        context,
                        R.style.MaterialTextButton_H1_KournikovaDodgerBlue
                    )
                }
                redeemCodeButton.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.kournikova))
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    redeemCodeButton.setTextAppearance(R.style.MaterialTextButton_H1_GeraldineWhite)
                } else {
                    redeemCodeButton.setTextAppearance(
                        redeemCodeButton.context,
                        R.style.MaterialTextButton_H1_GeraldineWhite
                    )
                }
                redeemCodeButton.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.geraldine))
            }
            redeemCodeButton.text = message
        }
    }

    private fun showLoadingDialog(isShow: Boolean) {
        if (isShow) {
            if (activity?.supportFragmentManager?.findFragmentByTag(LoadingDialog.TAG) == null) {
                activity?.supportFragmentManager?.let { fragmentManager ->
                    loadingDialog.show(fragmentManager, LoadingDialog.TAG)
                }
            }
        } else {
            loadingDialog.dismiss()
        }
    }
}