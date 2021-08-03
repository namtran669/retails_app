package namit.retail_app.address.presentation.set_location

import android.util.Log
import androidx.lifecycle.MutableLiveData
import namit.retail_app.address.domain.GetDeliveryAddressUseCase
import namit.retail_app.address.domain.GetMerchantListUseCase
import namit.retail_app.address.domain.GetUserAddressListUseCase
import namit.retail_app.address.domain.SaveDeliveryAddressUseCase
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.data.entity.AddressSearchModel
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.domain.CheckLocationPermissionUseCase
import namit.retail_app.core.domain.GetUserProfileLocalUseCase
import namit.retail_app.core.enums.MerchantType
import namit.retail_app.core.enums.RequestUserPermissionResult
import namit.retail_app.core.extension.addTo
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.tracking.TrackingValue
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.maps.data.model.GeoLocationResult
import namit.retail_app.maps.domain.CheckLocationServiceEnableUseCase
import namit.retail_app.maps.domain.GetLatLngLocationUseCase
import namit.retail_app.maps.domain.GetPlaceDescriptionUseCase
import namit.retail_app.maps.domain.SearchAddressUseCase
import namit.retail_app.maps.utils.LocationProvider
import com.google.android.gms.maps.model.LatLng
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SetLocationViewModel(
    private val locationProvider: LocationProvider,
    private val getPlaceDescriptionUseCase: GetPlaceDescriptionUseCase,
    private val saveDeliveryAddressUseCase: SaveDeliveryAddressUseCase,
    private val getDeliveryAddressUseCase: GetDeliveryAddressUseCase,
    private val getLatLngLocationUseCase: GetLatLngLocationUseCase,
    private val getListUserAddressUseCase: GetUserAddressListUseCase,
    private val checkLocationServiceEnableUseCase: CheckLocationServiceEnableUseCase,
    private val checkLocationPermissionUseCase: CheckLocationPermissionUseCase,
    private val searchAddressUseCase: SearchAddressUseCase,
    private val getUserProfileUseCase: GetUserProfileLocalUseCase,
    private val getMerchantListUseCase: GetMerchantListUseCase,
    private val eventTrackingManager: EventTrackingManager
) : BaseViewModel() {

    private val compositeDisposable = CompositeDisposable()
    val selectedDeliveryAddress = MutableLiveData<AddressModel>() // For return result address
    val currentDeliveryAddress = MutableLiveData<AddressModel>() // For render
    val moveMapLocation = MutableLiveData<LatLng>()
    val storageAddressList = MutableLiveData<List<AddressModel>>()
    val locationSearchSelected = MutableLiveData<AddressSearchModel>()
    val addressSearchLiveData = MutableLiveData<List<AddressSearchModel>>()
    val enableLocationButton = MutableLiveData<Boolean>()
    val currentLocationText = MutableLiveData<String>()

    //Alert Action
    val syncMap = SingleLiveEvent<Unit>()
    val alertRequestLocationPermission = SingleLiveEvent<Unit>()
    val alertOpenUserLocationSetting = SingleLiveEvent<Unit>()
    val alertOpenPermissionSetting = SingleLiveEvent<Unit>()
    val showBrokenLocationDialog = SingleLiveEvent<Unit>()
    val showEditAddressDialog = SingleLiveEvent<AddressModel>()
    val showLoading = SingleLiveEvent<Boolean>()

    private lateinit var deliveryAddress: AddressModel
    private var listAddressStorage = listOf<AddressModel>()

    companion object {
        const val DEFAULT_BANGKOK_LAT = 13.7563
        const val DEFAULT_BANGKOK_LNG = 100.5018
        const val TAG = "Set Location"
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun checkLocationPermission(permissionResult: RequestUserPermissionResult? = null) {
        val isLocationServiceEnable = checkLocationServiceEnableUseCase.execute()
        if (isLocationServiceEnable is UseCaseResult.Error) {
            alertOpenUserLocationSetting.call()
            return
        }

        val hasLocationPermission = checkLocationPermissionUseCase.execute()
        if (hasLocationPermission is UseCaseResult.Error) {
            when (permissionResult) {
                RequestUserPermissionResult.DENY_WITH_NEVER_ASK_AGAIN -> {
                    alertOpenPermissionSetting.call()
                }
                RequestUserPermissionResult.DENY -> {
                    alertRequestLocationPermission.call()
                }
                else -> {
                    alertRequestLocationPermission.call()
                }
            }
            return
        }

        if (permissionResult != null && permissionResult != RequestUserPermissionResult.ALLOW) {
            alertOpenPermissionSetting.call()
            return
        }

        syncMap.call()
    }

    fun loadLocationByLatLong(latitude: Double, longitude: Double) {
        loadPlaceDescription(latitude, longitude) { address ->
            deliveryAddress = address
            enableLocationButton.value = true
            currentDeliveryAddress.value = deliveryAddress
            currentLocationText.value = address.address?.replace("  ", " ")
        }
    }

    private fun setDeliveryAddress() {
        saveDeliveryAddress(deliveryAddress)
        selectedDeliveryAddress.value = deliveryAddress
        enableLocationButton.value = true
    }

    fun getLocationSearchSelected(searchPlace: AddressSearchModel) {
        locationSearchSelected.value = searchPlace
        launch {
            val response = getLatLngLocationUseCase.execute(searchPlace.placeID ?: "")
            if (response is UseCaseResult.Success) {
                val latLng = response.data!!
                moveCameraToPlace(latLng)
            }
        }
    }

    fun loadMyAddressUser() {
        launch {
            val userProfileResult = getUserProfileUseCase.execute()
            if (userProfileResult is UseCaseResult.Success) {
                val result = getListUserAddressUseCase.execute(userProfileResult.data!!.id)
                if (result is UseCaseResult.Success) {
                    listAddressStorage = result.data!!
                    storageAddressList.value = result.data!!
                }
            }
        }
    }

    fun moveCameraToPlace(latLng: LatLng) {
        moveMapLocation.value = latLng
    }

    fun searchAddress(query: String) {
        launch {
            val response = searchAddressUseCase.execute(query)
            if (response is UseCaseResult.Success) {
                addressSearchLiveData.value = response.data
            }
        }
    }

    private fun getCurrentLocation() {
        locationProvider.execute()
            .firstElement()
            .subscribe({ location ->
                moveCameraToPlace(LatLng(location.latitude, location.longitude))
            }, {
                moveCameraToPlace(LatLng(DEFAULT_BANGKOK_LAT, DEFAULT_BANGKOK_LNG))
                alertOpenUserLocationSetting.call()
                Log.e(TAG, "Can not get current", it)
            }).addTo(compositeDisposable)
    }

    private fun loadPlaceDescription(
        latitude: Double,
        longitude: Double,
        address: (addressModel: AddressModel) -> Unit
    ) {
        launch {
            val response = getPlaceDescriptionUseCase.execute(latitude, longitude)
            val addressResult = AddressModel()
            addressResult.lat = latitude
            addressResult.lng = longitude
            if (response is UseCaseResult.Success) {
                val location = response.data as GeoLocationResult
                addressResult.name = location.landMark
                addressResult.landMark = location.landMark
                addressResult.address = ("${location.streetNumber} " +
                        "${location.route} " +
                        "${location.supDistrict} " +
                        "${location.district} " +
                        "${location.province} " +
                        "${location.country} " + location.postalCode).trim()
            }
            address.invoke(addressResult)
        }
    }

    fun getSelectedDeliveryAddress() {
        val result = getDeliveryAddressUseCase.execute()
        if (result is UseCaseResult.Success) {
            deliveryAddress = result.data!!
            moveCameraToPlace(LatLng(deliveryAddress.lat, deliveryAddress.lng))
            currentDeliveryAddress.value = deliveryAddress
            enableLocationButton.value = true

        } else {
            getCurrentLocation()
        }
    }

    private fun saveDeliveryAddress(addressModel: AddressModel) =
        saveDeliveryAddressUseCase.execute(addressModel)

    fun checkLocationAndSaveDeliveryAddress() {
        var validLocation = false
        val deferredList =
            mutableListOf<Deferred<UseCaseResult<List<MerchantInfoItem>>>>()
        launch {
            showLoading.value = true
            MerchantType.values().forEach {
                if (it != MerchantType.UNKNOWN) {
                    val deferred = async {
                        getMerchantListUseCase.execute(deliveryAddress.lat, deliveryAddress.lng, it)
                    }
                    deferredList.add(deferred)
                }
            }

            deferredList.forEach {
                val result = it.await()
                if (result is UseCaseResult.Success) {
                    val hasMerchant = result.data?.isNotEmpty() ?: false
                    if (hasMerchant) {
                        validLocation = true
                        return@forEach
                    }
                }
            }

            showLoading.value = false
            if (validLocation) {
                eventTrackingManager.trackLocationService(available = TrackingValue.VALUE_YES)
                setDeliveryAddress()
            } else {
                eventTrackingManager.trackLocationService(available = TrackingValue.VALUE_NO)
                showBrokenLocationDialog.call()
            }
        }
    }
}