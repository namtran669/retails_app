package namit.retail_app.address.presentation.edit_location

import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import namit.retail_app.address.R
import namit.retail_app.address.presentation.adapter.SearchAddressAdapter
import namit.retail_app.address.presentation.adapter.StorageAddressAdapter
import namit.retail_app.address.presentation.edit.EditAddressBottomDialog
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.extension.AfterTextChangedWatcher
import namit.retail_app.core.extension.intentToSettingAppPermission
import namit.retail_app.core.extension.intentToSettingLocation
import namit.retail_app.core.navigation.AddressNavigator
import namit.retail_app.core.navigation.CoreNavigator
import namit.retail_app.core.presentation.base.BasePermissionFragment
import namit.retail_app.core.presentation.base.BasePermissionFullScreenDialog
import namit.retail_app.core.presentation.dialog.alert.AlertMessageDialog
import namit.retail_app.core.presentation.widget.decoration.HorizontalSpaceItemDecoration
import namit.retail_app.core.utils.KeyboardUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.dialog_set_delivery_location.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class EditLocationDialog : BasePermissionFullScreenDialog(),
    OnMapReadyCallback,
    GoogleMap.OnCameraIdleListener,
    GoogleMap.OnCameraMoveStartedListener {

    companion object {
        val TAG = EditLocationDialog::class.java.simpleName
        private const val MAPS_ZOOM = 17.0F
        const val UPDATE_ADDRESS_MODEL = "UPDATE_ADDRESS_MODEL"
        const val REQUEST_CODE_SETTING_LOCATION = 5001
        const val REQUEST_CODE_PERMISSION_LOCATION = 5002

        fun newInstance(addressUpdate: AddressModel): EditLocationDialog {
            val fragment = EditLocationDialog()
            fragment.arguments = Bundle().apply {
                putParcelable(UPDATE_ADDRESS_MODEL, addressUpdate)
            }
            return fragment
        }
    }

    private var maps: GoogleMap? = null
    private lateinit var addressAdapter: StorageAddressAdapter
    private val addressSearchAdapter = SearchAddressAdapter()
    private lateinit var searchListener: AfterTextChangedWatcher

    val viewModel: EditLocationViewModel by viewModel()

    private val coreNavigator: CoreNavigator by inject()
    private val addressNavigator: AddressNavigator by inject()

    var onSetLocationListener: (AddressModel) -> Unit = {}

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val display = activity!!.windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val height = (size.y * 1)
            setLayout(width, height)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_set_delivery_location, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewModel()
        viewModel.loadMyAddressUser()
        viewModel.checkLocationPermission()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SETTING_LOCATION) {
            viewModel.checkLocationPermission()
        } else if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
            viewModel.checkLocationPermission()
        }
    }

    private fun bindViewModel() {
        viewModel.selectedAddress.observe(viewLifecycleOwner, Observer { selectedAddress ->
            onSetLocationListener.invoke(selectedAddress)
            dismiss()
        })

        viewModel.enableLocationButton.observe(viewLifecycleOwner, Observer {
            setLocationButton.isEnabled = it
        })

        viewModel.alertOpenUserLocationSetting.observe(viewLifecycleOwner, Observer {
            if (isAdded) {
                activity?.supportFragmentManager?.let {
                    coreNavigator.alertQuestionDialog(
                        title = getString(R.string.hello),
                        message = getString(R.string.alert_message_current_location),
                        positiveButtonText = getString(R.string.setting),
                        negativeButtonText = getString(R.string.dismiss)
                    ).apply {
                        onPositionClick = {
                            activity?.intentToSettingLocation(REQUEST_CODE_SETTING_LOCATION)
                        }
                    }.show(it, AlertMessageDialog.TAG)
                }
            }
        })

        viewModel.alertOpenPermissionSetting.observe(viewLifecycleOwner, Observer {
            if (isAdded) {
                activity?.supportFragmentManager?.let {
                    coreNavigator.alertQuestionDialog(
                        title = getString(R.string.hello),
                        message = getString(R.string.alert_message_open_permission_location),
                        positiveButtonText = getString(R.string.setting),
                        negativeButtonText = getString(R.string.dismiss)
                    ).apply {
                        onPositionClick = {
                            context?.intentToSettingAppPermission()
                        }
                    }.show(it, AlertMessageDialog.TAG)
                }
            }
        })

        viewModel.moveMapLocation.observe(viewLifecycleOwner, Observer { location ->
            maps?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, MAPS_ZOOM))
        })

        viewModel.showEditAddressDialog.observe(viewLifecycleOwner, Observer { addressModel ->
            if (isAdded) {
                activity?.supportFragmentManager?.let {
                    val saveAddressDialog =
                        addressNavigator.getEditAddressDialog(addressModel) as EditAddressBottomDialog
                    saveAddressDialog.show(it, EditAddressBottomDialog.TAG)
                }
            }
        })

        viewModel.storageAddressList.observe(viewLifecycleOwner, Observer {
            addressAdapter.items = it
            storageAddressRecyclerView.visibility = View.VISIBLE
        })

        viewModel.locationSearchSelected.observe(viewLifecycleOwner, Observer {
            searchLocationEditText?.apply {
                removeTextChangedListener(searchListener)
                setText(it.addressName)
                addTextChangedListener(searchListener)
                clearFocus()
            }
        })

        viewModel.addressSearchLiveData.observe(viewLifecycleOwner, Observer {
            searchResultRecyclerView.visibility = View.VISIBLE
            addressSearchAdapter.items = it
        })

        viewModel.alertRequestLocationPermission.observe(viewLifecycleOwner, Observer {
            requestUserLocation()
        })

        viewModel.syncMap.observe(viewLifecycleOwner, Observer {
            val mapsFragment = activity?.supportFragmentManager
                ?.findFragmentById(R.id.searchLocationMap) as SupportMapFragment
            mapsFragment.getMapAsync(this)
        })

        viewModel.currentLocationText.observe(viewLifecycleOwner, Observer {
            searchLocationEditText.hint = it
        })
    }

    private fun initView() {
        setLocationButton.setOnClickListener {
            viewModel.setDeliveryAddress()
        }

        toolbar.onBackPressed = {
            dismiss()
        }

        addressAdapter = StorageAddressAdapter()
        addressAdapter.apply {
            onItemSelected = {
                viewModel.moveCameraToPlace(LatLng(it.lat, it.lng))
                viewModel.loadLocationByLatLong(it.lat, it.lng)
            }
        }

        storageAddressRecyclerView.apply {
            val decoration = HorizontalSpaceItemDecoration(
                resources.getDimension(R.dimen.addressStorageSpaceStartEnd).toInt(),
                resources.getDimension(R.dimen.addressStorageSpaceBetween).toInt()
            )
            addItemDecoration(decoration)
            isNestedScrollingEnabled = false
            adapter = addressAdapter
        }

        searchResultRecyclerView.apply {
            addressSearchAdapter.onItemSelected = {
                viewModel.getLocationSearchSelected(it)
                searchResultRecyclerView.visibility = View.GONE
                KeyboardUtil.hide(context, view?.windowToken)
            }
            adapter = addressSearchAdapter
        }

        searchListener = object : AfterTextChangedWatcher() {
            override fun afterTextChangedListener(s: String) {
                viewModel.searchAddress(s)
            }
        }
        searchLocationEditText.addTextChangedListener(searchListener)
    }

    private fun requestUserLocation() {
        requestAllowPermission(BasePermissionFragment.ACCESS_FINE_LOCATION, onResult = {
            viewModel.checkLocationPermission(permissionResult = it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentManager?.beginTransaction()?.remove(searchLocationMap)?.commit()
    }

    override fun onMapReady(map: GoogleMap?) {
        maps = map
        maps?.apply {
            uiSettings?.isMyLocationButtonEnabled = true
            uiSettings?.isScrollGesturesEnabled = true
            uiSettings?.isZoomGesturesEnabled = true
            setOnCameraIdleListener(this@EditLocationDialog)
            setOnCameraMoveStartedListener(this@EditLocationDialog)
        }

        val editAddressModel = arguments?.getParcelable(UPDATE_ADDRESS_MODEL) as AddressModel
        viewModel.getSelectedAddress(editAddressModel)
    }

    override fun onCameraIdle() {
        //do nothing
        val newLocation = maps?.cameraPosition?.target
        val newLatitude = newLocation?.latitude ?: 0.0
        val newLongitude = newLocation?.longitude ?: 0.0
        viewModel.loadLocationByLatLong(newLatitude, newLongitude)
    }

    override fun onCameraMoveStarted(reason: Int) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            searchLocationEditText.apply {
                removeTextChangedListener(searchListener)
                setText("")
                addTextChangedListener(searchListener)
            }
        }
    }
}