package namit.retail_app.app.navigation

import androidx.fragment.app.DialogFragment
import namit.retail_app.address.presentation.edit.EditAddressBottomDialog
import namit.retail_app.address.presentation.edit_location.EditLocationDialog
import namit.retail_app.address.presentation.manage.MyAddressDialogFragment
import namit.retail_app.address.presentation.search.SearchAddressDialog
import namit.retail_app.address.presentation.set_location.SetLocationDialog
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.navigation.AddressNavigator

class AddressNavigatorImpl : AddressNavigator {

    override fun getSetLocationDialog(editAddressContent: AddressModel?): DialogFragment {
        return SetLocationDialog.newInstance(editAddressContent)
    }

    override fun getEditAddressDialog(
        editAddressContent: AddressModel?,
        saveToDeliveryAddress: Boolean
    ): DialogFragment {
        return EditAddressBottomDialog.newInstance(editAddressContent, saveToDeliveryAddress)
    }

    override fun getEditLocationDialog(editAddressContent: AddressModel): DialogFragment {
        return EditLocationDialog.newInstance(editAddressContent)
    }

    override fun getMyAddressDialog(): DialogFragment {
        return MyAddressDialogFragment.newInstance()
    }

    override fun getSearchLocationDialog(): DialogFragment {
        return SearchAddressDialog.newInstance()
    }

}