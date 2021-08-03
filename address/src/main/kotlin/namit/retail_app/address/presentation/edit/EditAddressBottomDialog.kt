package namit.retail_app.address.presentation.edit

import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import namit.retail_app.address.R
import namit.retail_app.address.presentation.edit_location.EditLocationDialog
import namit.retail_app.address.presentation.set_location.SetLocationDialog
import namit.retail_app.core.data.entity.AddressListType
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.extension.afterTextChanged
import namit.retail_app.core.navigation.AddressNavigator
import namit.retail_app.core.presentation.base.BaseFullScreenDialog
import namit.retail_app.core.utils.PhoneNumberFormatter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_address_edit.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class EditAddressBottomDialog : BaseFullScreenDialog() {

    companion object {
        val TAG = EditAddressBottomDialog::class.java.simpleName

        private const val ARG_EDIT_ADDRESS_DATA = "ARG_EDIT_ADDRESS_DATA"
        private const val ARG_SAVE_TO_DELIVERY_ADDRESS = "ARG_SAVE_TO_DELIVERY_ADDRESS"

        fun newInstance(editAddressContent: AddressModel?, saveToDeliveryAddress: Boolean)
                : EditAddressBottomDialog {
            val fragment = EditAddressBottomDialog()
            val cloneAddressData = editAddressContent?.copy()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_EDIT_ADDRESS_DATA, cloneAddressData)
                putBoolean(ARG_SAVE_TO_DELIVERY_ADDRESS, saveToDeliveryAddress)
            }
            return fragment
        }
    }

    private val viewModel: EditAddressBottomDialogViewModel by viewModel(parameters = {
        parametersOf(
            arguments?.getParcelable(ARG_EDIT_ADDRESS_DATA),
            arguments?.getBoolean(ARG_SAVE_TO_DELIVERY_ADDRESS)
        )
    })

    private val addressNavigator: AddressNavigator by inject()
    var onDismissDialog: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.dialog_address_edit, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindViewModel()
    }

    override fun onStart() {
        super.onStart()
        dialog.window?.apply {
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        onDismissDialog.invoke()
    }

    private fun bindViewModel() {
        viewModel.addressContent.observe(viewLifecycleOwner, Observer { address ->
            when (address.type) {
                AddressListType.HOME -> {
                    toggleHomeOption(true)
                }
                AddressListType.WORK -> {
                    toggleWorkOption(true)
                }
                AddressListType.OTHER -> {
                    toggleOtherOption(true)
                }
            }

            address.addressDetail?.let { addressDetailsEditText.setText(it) }
            address.note?.let { noteDriverEditText.setText(it) }
            address.phone?.let { phoneNumberEditText.setText(it) }
            address.isDefault.let { setDefaultSwitchButton.isChecked = it }
            address.name.let { addressNameEditText.setText(it) }
            address.note?.let { noteDriverEditText.setText(it) }
        })

        viewModel.addressFieldLiveData.observe(viewLifecycleOwner, Observer {
            setAddressContent(it)
        })

        viewModel.dismissDialog.observe(viewLifecycleOwner, Observer {
            dismiss()
        })

        viewModel.showUpdateSavedAddressDialog.observe(viewLifecycleOwner, Observer { editAddress ->
            val editLocationDialog = addressNavigator.getEditLocationDialog(editAddress) as EditLocationDialog
            editLocationDialog.onSetLocationListener = { selectedAddress ->
                viewModel.updateLocationData(selectedAddress)
            }
            editLocationDialog.show(fragmentManager, SetLocationDialog.TAG)
        })

        viewModel.shouldUpdateLocationData.observe(viewLifecycleOwner, Observer {
            viewModel.triggerUpdateLocationData()
        })

        viewModel.showPopupMessage.observe(viewLifecycleOwner, Observer {
            showSnackBar(it, Snackbar.LENGTH_LONG)
        })

        viewModel.showLabelMissing.observe(viewLifecycleOwner, Observer {
            showSnackBar(
                resources.getString(R.string.input_address_label_missing),
                Snackbar.LENGTH_LONG
            )
        })

        viewModel.showPhoneNumberInvalid.observe(viewLifecycleOwner, Observer {
            showSnackBar(
                resources.getString(R.string.input_address_phone_invalid),
                Snackbar.LENGTH_LONG
            )
        })
    }

    private fun initView() {
        initInputEditText()
        iconCloseImageView.setOnClickListener {
            dismiss()
        }

        editAddressWrapperLayout.setOnClickListener {
            dismiss()
        }

        saveAddressButton.setOnClickListener {
            viewModel.validateInputData()
        }

        homeOptionTextView.setOnClickListener {
            viewModel.saveAddressType(AddressListType.HOME)
        }

        workOptionTextView.setOnClickListener {
            viewModel.saveAddressType(AddressListType.WORK)
        }

        otherOptionTextView.setOnClickListener {
            viewModel.saveAddressType(AddressListType.OTHER)
        }

        addressInputLayout.setOnClickListener {
            viewModel.openUpdateSavedAddressDialog()
        }

        iconMapImageView.setOnClickListener {
            viewModel.openUpdateSavedAddressDialog()
        }

        setDefaultSwitchButton.setOnCheckedChangeListener { buttonView, isChecked ->
            setDefaultSwitchButton.setBackColorRes(if(isChecked) R.color.curiousBlue else R.color.tropicalBlue)
            viewModel.saveDefault(isChecked)
        }
    }

    private fun initInputEditText() {

        phoneNumberEditText?.addTextChangedListener(
            PhoneNumberFormatter(
                editText = phoneNumberEditText,
                onTextChanged = {
                    //todo need confirm logic when integrate api
                    viewModel.savePhoneNumber(phoneNumberEditText.text.toString().trim())
                })
        )

        addressDetailsEditText.afterTextChanged {
            viewModel.saveAddressDetail(it.trim())
        }

        noteDriverEditText.afterTextChanged {
            viewModel.saveNote(it.trim())
        }

        addressNameEditText.afterTextChanged {
            viewModel.saveName(it.trim())
        }

    }


    private fun setAddressContent(address: String) {
        addressEditText.apply {
            text = address
            setTextColor(ContextCompat.getColor(context, R.color.trout))
        }
    }

    private fun toggleHomeOption(isOn: Boolean) {
        homeOptionTextView.apply {
            handleToggleTextColor(isOn, this)
            val icon: Drawable? = if (isOn) {
                ContextCompat.getDrawable(context, R.drawable.ic_home_select)
            } else {
                ContextCompat.getDrawable(context, R.drawable.ic_home_grey)
            }
            setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null)

            if (isOn) {
                toggleWorkOption(false)
                toggleOtherOption(false)
            }
        }

        addressNameEditText.apply {
            if(isOn) {
                setText(context.getString(R.string.home))
                isEnabled = false
            }
        }
    }

    private fun toggleWorkOption(isOn: Boolean) {
        workOptionTextView.apply {
            handleToggleTextColor(isOn, this)
            val icon: Drawable? = if (isOn) {
                ContextCompat.getDrawable(context, R.drawable.ic_work_location_blue)
            } else {
                ContextCompat.getDrawable(context, R.drawable.ic_work_location_grey)
            }
            setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null)

            if (isOn) {
                toggleHomeOption(false)
                toggleOtherOption(false)
            }
        }

        addressNameEditText.apply {
            if(isOn) {
                setText(context.getString(R.string.work))
                isEnabled = false
            }
        }
    }

    private fun toggleOtherOption(isOn: Boolean) {
        otherOptionTextView.apply {
            handleToggleTextColor(isOn, this)
            val icon: Drawable? = if (isOn) {
                ContextCompat.getDrawable(context, R.drawable.ic_others_location_blue)
            } else {
                ContextCompat.getDrawable(context, R.drawable.ic_others_location_grey)
            }
            setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null)

            if (isOn) {
                toggleHomeOption(false)
                toggleWorkOption(false)
            }
        }

        addressNameEditText.apply {
            if(isOn) {
                isEnabled = true
            }
        }
    }

    private fun handleToggleTextColor(isOn: Boolean, textView: TextView) {
        textView.apply {
            if (isOn) {
                setTextColor(ContextCompat.getColor(context, R.color.dodgerBlue))
            } else {
                setTextColor(ContextCompat.getColor(context, R.color.trout))
            }
        }
    }


}