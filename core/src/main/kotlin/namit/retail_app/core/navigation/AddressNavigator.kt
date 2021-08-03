package namit.retail_app.core.navigation

import androidx.fragment.app.DialogFragment
import namit.retail_app.core.data.entity.AddressModel

interface AddressNavigator {
    fun getEditAddressDialog(editAddressContent: AddressModel? = null, saveToDeliveryAddress: Boolean = false) : DialogFragment

    fun getSetLocationDialog(editAddressContent: AddressModel? = null): DialogFragment

    fun getEditLocationDialog(editAddressContent: AddressModel): DialogFragment

    fun getSearchLocationDialog(): DialogFragment

    fun getMyAddressDialog() : DialogFragment

}