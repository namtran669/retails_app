package namit.retail_app.address.presentation.edit_location

import androidx.lifecycle.MutableLiveData
import namit.retail_app.address.domain.GetUserAddressListUseCase
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.data.entity.AddressSearchModel
import namit.retail_app.core.domain.CheckLocationPermissionUseCase
import namit.retail_app.core.domain.GetUserProfileLocalUseCase
import namit.retail_app.core.enums.RequestUserPermissionResult
import namit.retail_app.core.extension.addTo
import namit.retail_app.core.presentation.base.BaseViewModel
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
import kotlinx.coroutines.launch

class EditLocationViewModel(
    private val locationProvider: LocationProvider,
    private val getPlaceDescriptionUseCase: GetPlaceDescriptionUseCase,
    private val getLatLngLocationUseCase: GetLatLngLocationUseCase,
    private val getListUserAddressUseCase: GetUserAddressListUseCase,
    private val checkLocationServiceEnableUseCase: CheckLocationServiceEnableUseCase,
    private val checkLocationPermissionUseCase: CheckLocationPermissionUseCase,
    private val searchAddressUseCase: SearchAddressUseCase,
    private val getUserProfileUseCase: GetUserProfileLocalUseCase
) : BaseViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val selectedAddress = MutableLiveData<AddressModel>() // For return result address
    val currentAddress = MutableLiveData<AddressModel>() // For render
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
    val showEditAddressDialog = SingleLiveEvent<AddressModel>()

    private lateinit var deliveryAddress: AddressModel
    private var listAddressStorage = listOf<AddressModel>()


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
            alertRequestLocationPermission.call()
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
            currentAddress.value = deliveryAddress
            enableLocationButton.value = true
            currentLocationText.value = address.address
        }
    }

    fun setDeliveryAddress() {
        selectedAddress.value = deliveryAddress
        enableLocationButton.value = true
    }

    fun getLocationSearchSelected(searchPlace: AddressSearchModel) {
        locationSearchSelected.value = searchPlace
        launch {
            val response = getLatLngLocationUseCase.execute(searchPlace.placeID ?: "")
            if (response is UseCaseResult.Success) {
                //Should add more address data
                val latLng = response.data!!
                moveMapLocation.value = latLng
                loadPlaceDescription(latitude = latLng.latitude, longitude = latLng.longitude) { address ->
                    deliveryAddress = address
                    currentAddress.value = deliveryAddress
                    enableLocationButton.value = true
                }
            }
        }
    }

    fun loadMyAddressUser() {
        launch {
            val userProfileResult = getUserProfileUseCase.execute()
            if(userProfileResult is UseCaseResult.Success) {
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
                moveMapLocation.value = LatLng(location.latitude, location.longitude)
                loadPlaceDescription(location.latitude, location.longitude) { address ->
                    deliveryAddress = address
                    currentAddress.value = deliveryAddress
                    enableLocationButton.value = true
                }
            }, {
                alertOpenUserLocationSetting.call()
            }).addTo(compositeDisposable)
    }

    private fun loadPlaceDescription(latitude: Double, longitude: Double, address: (addressModel: AddressModel) -> Unit) {
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
    fun getSelectedAddress(addressModel: AddressModel) {
        if (addressModel.lat == 0.0 && addressModel.lng == 0.0) {
            getCurrentLocation()
        } else {
            deliveryAddress = addressModel
            moveMapLocation.value = LatLng(deliveryAddress.lat, deliveryAddress.lng)
            currentAddress.value = deliveryAddress
            enableLocationButton.value = true
        }
    }
}