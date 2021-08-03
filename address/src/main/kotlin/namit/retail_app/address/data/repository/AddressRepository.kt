package namit.retail_app.address.data.repository

import namit.retail_app.core.data.entity.AddressListType
import namit.retail_app.core.data.entity.AddressModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import hasura.CreateMyAddressMutation
import hasura.DeleteMyAddressMutation
import hasura.GetMyAddressListQuery
import hasura.UpdateMyAddressMutation

interface AddressRepository {
    suspend fun getUserAddressList(): List<AddressModel>
    suspend fun deleteAddressItem(address: AddressModel): Boolean
    suspend fun createAddressItem(address: AddressModel): AddressModel
    suspend fun updateAddressItem(address: AddressModel): Boolean
}

class AddressRepositoryImpl(private val apollo: ApolloClient) : AddressRepository {

    override suspend fun getUserAddressList(): List<AddressModel> {
        val query =
            GetMyAddressListQuery
                .builder()
                .build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()

        val result = response.data()?.addresses()?.edges()
        val listAddressModel = mutableListOf<AddressModel>()
        result?.forEach {
            val type = when (it.type_id()) {
                AddressListType.HOME.value -> AddressListType.HOME
                AddressListType.WORK.value -> AddressListType.WORK
                else -> AddressListType.OTHER
            }
            val address = AddressModel(
                id = it.id(),
                landMark = it.land_mark(),
                type = type,
                name = it.name(),
                address = it.address(),
                addressDetail = it.address_details(),
                phone = it.phone().replaceFirst("66", "0"),
                note = it.notes(),
                lat = it.lat(),
                lng = it.lng(),
                isDefault = it.is_default
            )

            listAddressModel.add(address)
        }
        return listAddressModel
    }

    override suspend fun deleteAddressItem(address: AddressModel): Boolean {
        val mutate =
            DeleteMyAddressMutation
                .builder()
                .id(address.id)
                .build()
        val deferred = apollo.mutate(mutate).toDeferred()
        val response = deferred.await()

        val result = response.data()?.deleteAddress()?.modified()
        return result == 1
    }

    override suspend fun createAddressItem(address: AddressModel): AddressModel {
        val mutate =
            CreateMyAddressMutation
                .builder()
                .name(address.name)
                .phone(convertPhoneNumberFormat(address.phone ?: ""))
                .lat(address.lat)
                .lng(address.lng)
                .land_mark(address.landMark)
                .address(address.address ?: "")
                .is_default(address.isDefault)
                .type_id(address.type.value)
                .note(address.note ?: "")
                .build()
        val deferred = apollo.mutate(mutate).toDeferred()
        val response = deferred.await()
        address.id = response.data()?.createAddress()?.id() ?: -1
        return address
    }

    override suspend fun updateAddressItem(address: AddressModel): Boolean {

        val mutate = UpdateMyAddressMutation
            .builder()
            .id(address.id)
            .name(address.name)
            .phone(convertPhoneNumberFormat(address.phone ?: ""))
            .lat(address.lat)
            .lng(address.lng)
            .land_mark(address.landMark)
            .address(address.address)
            .address_detail(address.addressDetail)
            .is_default(address.isDefault)
            .type_id(address.type.value)
            .note(address.note ?: "")
            .build()

        val deferred = apollo.mutate(mutate).toDeferred()
        val response = deferred.await()

        val result = response.data()?.updateAddress()?.modified() ?: 0
        return result > 0
    }

    private fun convertPhoneNumberFormat(phone: String): String {
        val phoneNumberObj: Phonenumber.PhoneNumber =
            PhoneNumberUtil.getInstance().parse(phone, "TH")
        return phoneNumberObj.countryCode.toString() + phoneNumberObj.nationalNumber
    }
}