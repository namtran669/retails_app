package namit.retail_app.home.presentation.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import namit.retail_app.address.presentation.set_location.SetLocationViewModel
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.extension.*
import namit.retail_app.core.navigation.*
import namit.retail_app.core.presentation.base.BasePermissionFragment
import namit.retail_app.core.presentation.dialog.alert.AlertMessageDialog
import namit.retail_app.core.presentation.widget.LoadingDialog
import namit.retail_app.core.utils.getWidthScreenSize
import namit.retail_app.coupon.presentation.adapter.CouponHorizontalListAdapter
import namit.retail_app.coupon.presentation.coupon.CouponViewModel
import namit.retail_app.coupon.presentation.detail.CouponDetailDialogFragment
import namit.retail_app.home.R
import namit.retail_app.home.data.entity.WeatherModel
import namit.retail_app.home.enums.DaySessionType
import namit.retail_app.home.enums.WeatherType
import namit.retail_app.story.presentation.adapter.StoryAdapter
import namit.retail_app.story.presentation.adapter.StoryContentAdapter
import namit.retail_app.story.presentation.details.StoryDetailDialogFragment
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

@ObsoleteCoroutinesApi
class HomeFragment : BasePermissionFragment() {

    companion object {
        const val TAG = "HomeFragment"
        const val REQUEST_CODE_SETTING_LOCATION = 1001
        fun getNewInstance(): HomeFragment = HomeFragment()
    }

    private val homeViewModel: HomeViewModel by viewModel()
    private val couponViewModel: CouponViewModel by viewModel()
    private val locationViewModel: SetLocationViewModel by viewModel()

    private val couponNavigator: CouponNavigator by inject()
    private val groceryNavigator: GroceryNavigator by inject()
    private val coreNavigator: CoreNavigator by inject()
    private val foodNavigator: FoodNavigator by inject()
    private val storyNavigator: StoryNavigator by inject()

    private lateinit var storyAdapter: StoryAdapter
    private lateinit var couponHorizontalListAdapter: CouponHorizontalListAdapter
    private lateinit var annoucementAdapter: StoryContentAdapter
    private var annoucementSkeletonScreen: SkeletonScreen? = null
    private var couponsSkeletonScreen: SkeletonScreen? = null
    private var storiesSkeletonScreen: SkeletonScreen? = null

    private var loadingDialog: LoadingDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_home, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindViewModel()
        bindLocationViewModel()
        homeViewModel.loadAnnoucementList()
        homeViewModel.loadPaymentCollection()
        homeViewModel.loadStoryList()
        couponViewModel.loadAllCoupon()
        locationViewModel.checkLocationPermission()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SETTING_LOCATION) {
            requestUserLocation()
        }
    }

    private fun initView() {
        sevenCategoryImageView.setOnClickListener {
            context?.apply {
                //Todo get merchant id from Firebase
                homeViewModel.loadMerchantByStoreId("L945275")
            }
        }

        couponHorizontalListAdapter = CouponHorizontalListAdapter().apply {
            context?.let { context ->
                val itemWidthSize = (getWidthScreenSize(context = context) * 0.55).toInt()
                itemWidth = itemWidthSize
                itemHeight = (itemWidthSize * 0.58).toInt()
            }
            onCouponClick = {
                activity?.supportFragmentManager?.let { supportFragmentManager ->
                    val couponDialog =
                        couponNavigator.openCouponDetail(couponModel = it) as CouponDetailDialogFragment
                    couponDialog.onUseNowClick = { couponModel ->
                        couponViewModel.saveSelectedCoupon(couponModel = couponModel)
                    }
                    couponDialog.show(supportFragmentManager, CouponDetailDialogFragment.TAG)
                }
            }
        }
        couponRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = couponHorizontalListAdapter
            isNestedScrollingEnabled = false
        }

        groceryCategoryImageView.setOnClickListener {
            openMainGrocery()
        }

        foodCategoryImageView.setOnClickListener {
            context?.apply {
                startActivity(foodNavigator.getFoodActivity(this))
            }
        }

        cafeCategoryImageView.setOnClickListener {
            context?.apply {
                //Todo get merchant id from Firebase
                homeViewModel.loadMerchantByStoreId("L947211")
            }
        }

        annoucementAdapter = StoryContentAdapter(onItemClick = { annoucement ->
            activity?.supportFragmentManager?.let { fragmentManager ->
                val storyDetailDialog =
                    storyNavigator.getStoryDetail(baseStoryContent = annoucement)
                storyDetailDialog.show(fragmentManager, StoryDetailDialogFragment.TAG)
            }
        })
        annoucementRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = annoucementAdapter
            isNestedScrollingEnabled = false
        }

        storyAdapter = StoryAdapter()
        storyAdapter.onItemClick = { story ->
            activity?.supportFragmentManager?.let { fragmentManager ->
                val storyDetailDialog = storyNavigator.getStoryDetail(baseStoryContent = story)
                storyDetailDialog.show(fragmentManager, StoryDetailDialogFragment.TAG)
            }
        }
        storyRecyclerView.apply {
            adapter = storyAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun bindViewModel() {
        homeViewModel.userId.observe(viewLifecycleOwner, Observer {
        })

        homeViewModel.storyShelfList.observe(viewLifecycleOwner, Observer {
            storyAdapter.items = it
        })

        homeViewModel.annoucementList.observe(viewLifecycleOwner, Observer {
            annoucementAdapter.items = it
        })

        homeViewModel.isAnnoucementListLoading.observe(viewLifecycleOwner, Observer { isShow ->
            if (isShow) {
                if (annoucementSkeletonScreen != null) {
                    annoucementSkeletonScreen?.show()
                } else {
                    annoucementSkeletonScreen = Skeleton.bind(annoucementRecyclerView)
                        .adapter(annoucementAdapter)
                        .shimmer(false)
                        .frozen(false)
                        .count(1)
                        .load(R.layout.item_shelf_annoucement_skeleton)
                        .show()
                }
                annoucementRecyclerView.setPadding(10.dpToPx(), 0, 0, 0)
            } else {
                annoucementSkeletonScreen?.hide()
                annoucementRecyclerView.setPadding(10.dpToPx(), 0, 10.dpToPx(), 0)
            }
        })

        homeViewModel.isStoryListLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                if (storiesSkeletonScreen != null) {
                    storiesSkeletonScreen?.show()
                } else {
                    storiesSkeletonScreen = Skeleton.bind(storyRecyclerView)
                        .adapter(storyAdapter)
                        .shimmer(false)
                        .frozen(false)
                        .count(1)
                        .load(R.layout.item_shelf_story_skeleton)
                        .show()
                }
            } else {
                storiesSkeletonScreen?.hide()
            }
        })

        homeViewModel.openMerchantDetailByStoreId.observe(
            viewLifecycleOwner,
            Observer { merchantInfoItem ->
                openMerchantDetails(merchantInfoItem)
            })

        homeViewModel.showLoading.observe(viewLifecycleOwner, Observer { isShow ->
            fragmentManager?.let { fragmentManager ->
                if (isShow) {
                    loadingDialog?.let {
                        it.show(fragmentManager, LoadingDialog.TAG)
                    } ?: kotlin.run {
                        loadingDialog = coreNavigator.getLoadingDialog(haveBlurBackground = true)
                        loadingDialog!!.show(fragmentManager, LoadingDialog.TAG)
                    }
                } else {
                    loadingDialog?.dismiss()
                }
            }
        })

        couponViewModel.isCouponListLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                couponHeaderTextView.gone()
                if (couponsSkeletonScreen != null) {
                    couponsSkeletonScreen?.show()
                } else {
                    couponsSkeletonScreen = Skeleton.bind(couponRecyclerView)
                        .adapter(couponHorizontalListAdapter)
                        .shimmer(false)
                        .frozen(false)
                        .count(1)
                        .load(R.layout.item_shelf_coupon_no_see_all_skeleton)
                        .show()
                }
            } else {
                couponsSkeletonScreen?.hide()
                couponHeaderTextView.visible()
            }
        })

        couponViewModel.couponList.observe(viewLifecycleOwner, Observer {
            couponHorizontalListAdapter.items = it
        })

        couponViewModel.showEmpty.observe(viewLifecycleOwner, Observer { isShow ->
            if (isShow) {
                couponHeaderTextView.visibility = View.GONE
                couponRecyclerView.visibility = View.GONE
            } else {
                couponHeaderTextView.visibility = View.VISIBLE
                couponRecyclerView.visibility = View.VISIBLE
            }
        })

        couponViewModel.openMerchantDetails.observe(viewLifecycleOwner, Observer { merchantInfo ->
            openMerchantDetails(merchantInfo)
        })

        couponViewModel.openMainGrocery.observe(viewLifecycleOwner, Observer {
            openMainGrocery()
        })

        homeViewModel.currentWeather.observe(viewLifecycleOwner, Observer {
            handleCurrentWeather(it)
        })

        homeViewModel.hideWeather.observe(viewLifecycleOwner, Observer {
            getCurrentWeatherLocal()
        })
    }

    private fun bindLocationViewModel() {
        locationViewModel.currentDeliveryAddress.observe(viewLifecycleOwner, Observer {
            homeViewModel.loadCurrentWeather(it.lat, it.lng)
        })

        locationViewModel.alertOpenPermissionSetting.observe(viewLifecycleOwner, Observer {
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
        })

        locationViewModel.alertOpenUserLocationSetting.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let {
                coreNavigator.alertQuestionDialog(
                    title = getString(R.string.hello),
                    message = getString(R.string.alert_message_current_location),
                    positiveButtonText = getString(R.string.setting),
                    negativeButtonText = getString(R.string.dismiss)
                ).apply {
                    onPositionClick = {
                        activity?.intentToSettingLocation(requestCode = REQUEST_CODE_SETTING_LOCATION)
                    }
                }.show(it, AlertMessageDialog.TAG)
            }
        })

        locationViewModel.alertRequestLocationPermission.observe(viewLifecycleOwner, Observer {
            requestUserLocation()
        })

        locationViewModel.syncMap.observe(viewLifecycleOwner, Observer {
            locationViewModel.getSelectedDeliveryAddress()
        })
    }

    private fun openMerchantDetails(merchantInfoItem: MerchantInfoItem) {
        context?.let {
            startActivity(
                groceryNavigator.getGroceryActivityWithMerchantInfo(
                    context = it, merchant = merchantInfoItem
                )
            )
        }
    }

    private fun openMainGrocery() {
        context?.apply {
            startActivity(groceryNavigator.getGroceryWrapperActivity(this))
        }
    }

    private fun autoChangeOrderStatus() {
        orderStatusFloatingView.apply {
            changeToConfirmState()
            Handler().postDelayed({
                changeToPrepareState()
                setArriveTime(20)
            }, 3000)
            Handler().postDelayed({
                changeToDeliveryState()
                setArriveTime(10)
            }, 6000)
            Handler().postDelayed({
                changeToSuccessState()
            }, 9000)
        }
    }

    private fun requestUserLocation() {
        requestAllowPermission(ACCESS_FINE_LOCATION, onResult = {
            locationViewModel.checkLocationPermission(permissionResult = it)
        })
    }

    private fun handleCurrentWeather(data: WeatherModel) {
        weatherImageView.invisible()
        titleHomeTextView.invisible()
        if (data.type == WeatherType.UNKNOWN) {
            getCurrentWeatherLocal()
            return
        }

        when (data.session) {
            DaySessionType.MORNING -> {
                titleHomeTextView.text = getString(R.string.good_morning)
                weatherBackground.setBackgroundResource(R.drawable.bg_gradient_viking_white)
            }
            DaySessionType.AFTERNOON -> {
                titleHomeTextView.text = getString(R.string.good_afternoon)
                weatherBackground.setBackgroundResource(R.drawable.bg_gradient_diserria_white)
            }
            DaySessionType.EVENING -> {
                titleHomeTextView.text = getString(R.string.good_evening)
                weatherBackground.setBackgroundResource(R.drawable.bg_gradient_waikawagray_white)
            }
            DaySessionType.NIGHT -> {
                titleHomeTextView.text = getString(R.string.good_morning)
                weatherBackground.setBackgroundResource(R.drawable.bg_gradient_waikawagray_white)
                weatherImageView.setImageResource(R.drawable.ic_moon)
            }
        }

        when (data.type) {
            WeatherType.CLEAR_SUN -> {
                R.drawable.ic_sunny
            }
            WeatherType.CLEAR_MOON -> {
                R.drawable.ic_moon
            }
            WeatherType.PARTLY_CLOUDY_SUN -> {
                R.drawable.ic_partly_sunny
            }
            WeatherType.PARTLY_CLOUDY_MOON -> {
                R.drawable.ic_partly_moon
            }
            WeatherType.CLOUDY -> {
                if (data.session == DaySessionType.EVENING) {
                    weatherBackground.setBackgroundResource(R.drawable.bg_gradient_waikawagray_white)
                } else {
                    weatherBackground.setBackgroundResource(R.drawable.bg_gradient_glacier_white)
                }
                R.drawable.ic_cloudy
            }
            WeatherType.RAIN -> {
                weatherBackground.setBackgroundResource(R.drawable.bg_gradient_waikawagray_white)
                R.drawable.ic_rain
            }
            WeatherType.STORM -> {
                weatherBackground.setBackgroundResource(R.drawable.bg_gradient_waikawagray_white)
                R.drawable.ic_storm
            }
            else -> {
                null
            }
        }?.let {
            weatherImageView.setImageResource(it)
            weatherImageView.visible()
            titleHomeTextView.visible()
        }

    }

    fun getCurrentLocation() {
        locationViewModel.getSelectedDeliveryAddress()
    }

    private fun getCurrentWeatherLocal() {

        when (getDaySession()) {
            DaySessionType.MORNING -> {
                titleHomeTextView.text = getString(R.string.good_morning)
                weatherBackground.setBackgroundResource(R.drawable.bg_gradient_viking_white)
                weatherImageView.setImageResource(R.drawable.ic_partly_sunny)
            }
            DaySessionType.AFTERNOON -> {
                titleHomeTextView.text = getString(R.string.good_afternoon)
                weatherBackground.setBackgroundResource(R.drawable.bg_gradient_diserria_white)
                weatherImageView.setImageResource(R.drawable.ic_sunny)
            }
            DaySessionType.EVENING -> {
                titleHomeTextView.text = getString(R.string.good_evening)
                weatherBackground.setBackgroundResource(R.drawable.bg_gradient_waikawagray_white)
                weatherImageView.setImageResource(R.drawable.ic_moon)
            }
            DaySessionType.NIGHT -> {
                titleHomeTextView.text = getString(R.string.good_morning)
                weatherBackground.setBackgroundResource(R.drawable.bg_gradient_waikawagray_white)
                weatherImageView.setImageResource(R.drawable.ic_moon)
            }
        }

        weatherImageView.visible()
        titleHomeTextView.visible()

    }

    private fun getDaySession(): DaySessionType {
        val calendar = Calendar.getInstance()
        val timeOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        if (timeOfDay in 6..11) {
            return DaySessionType.MORNING
        } else if (timeOfDay in 12..17) {
            return DaySessionType.AFTERNOON
        } else if (timeOfDay in 18..23) {
            return DaySessionType.EVENING
        } else {
            return DaySessionType.NIGHT
        }
    }

}
