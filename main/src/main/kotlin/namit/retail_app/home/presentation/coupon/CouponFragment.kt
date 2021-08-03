package namit.retail_app.home.presentation.coupon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import namit.retail_app.core.data.entity.CouponModel
import namit.retail_app.core.extension.gone
import namit.retail_app.core.extension.visible
import namit.retail_app.core.navigation.CouponNavigator
import namit.retail_app.core.navigation.GroceryNavigator
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.core.utils.getWidthScreenSize
import namit.retail_app.coupon.presentation.adapter.CouponFilterAdapter
import namit.retail_app.coupon.presentation.adapter.CouponVerticalListAdapter
import namit.retail_app.coupon.presentation.coupon.CouponViewModel
import namit.retail_app.coupon.presentation.detail.CouponDetailDialogFragment
import namit.retail_app.home.R
import namit.retail_app.home.presentation.home.HomeFragment
import namit.retail_app.home.presentation.tab.TabActivity
import kotlinx.android.synthetic.main.fragment_coupon_list.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class CouponFragment: BaseFragment() {

    companion object {
        const val TAG = "CouponFragment"
        fun getNewInstance(): CouponFragment =
            CouponFragment()
    }

    private val couponNavigator: CouponNavigator by inject()
    private val viewModel: CouponViewModel by viewModel()
    private val groceryNavigator: GroceryNavigator by inject()
    private lateinit var couponVerticalListAdapter: CouponVerticalListAdapter
    private lateinit var couponFilterAdapter: CouponFilterAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_coupon_list, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewMode()
        viewModel.loadCouponTypeList()
        viewModel.loadAllCoupon()
    }

    private fun initView() {
        emptyLayoutView.apply {
            setEmptyTitle(getString(R.string.no_coupons))
            setEmptyDetails(getString(R.string.no_coupons_details))
            setEmptyImage(R.drawable.img_empty_coupon)
            onClickAction = {
                (activity as TabActivity).apply {
                    setCurrentTab(R.id.actionHome)
                    renderFragment(HomeFragment.TAG)
                }
            }
        }

        couponFilterAdapter = CouponFilterAdapter().apply {
            onFilterClick = {
                viewModel.changeSelectedFilter(filterIndex = it)
            }
        }
        couponTypeRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = couponFilterAdapter
        }

        couponVerticalListAdapter = CouponVerticalListAdapter().apply {
            context?.let { context ->
                val itemWidthSize = (getWidthScreenSize(context = context) * 0.95).toInt()
                itemWidth = itemWidthSize
                itemHeight = (itemWidthSize * 0.45).toInt()
            }
            onCouponClick = {
                openCouponDetails(couponModel = it)
            }
            onUseNowClick = {
                viewModel.saveSelectedCoupon(couponModel = it)
            }
        }
        couponRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = couponVerticalListAdapter
        }
    }

    private fun bindViewMode() {
        viewModel.couponFilterList.observe(viewLifecycleOwner, Observer {
            couponFilterAdapter.items = it
        })

        viewModel.couponList.observe(viewLifecycleOwner, Observer {
            couponRecyclerView.scrollTo(0, 0)
            couponVerticalListAdapter.items = it
        })

        viewModel.showEmpty.observe(viewLifecycleOwner, Observer { isShow ->
            if (isShow) {
                couponListTitleTextView.gone()
                couponRecyclerView.gone()
                emptyLayoutView.visible()
            } else {
                couponListTitleTextView.visible()
                couponRecyclerView.visible()
                emptyLayoutView.gone()
            }
        })

        viewModel.isCouponListLoading.observe(viewLifecycleOwner, Observer { isShow ->
            if (isShow) {
                couponTitleLoadingView.visibility = View.VISIBLE
            } else {
                couponTitleLoadingView.visibility = View.GONE
            }
        })

        viewModel.openMerchantDetails.observe(viewLifecycleOwner, Observer { merchantInfo ->
            context?.let {
                startActivity(
                    groceryNavigator.getGroceryActivityWithMerchantInfo(
                        context = it, merchant = merchantInfo
                    )
                )
            }
        })

        viewModel.openMainGrocery.observe(viewLifecycleOwner, Observer {
            context?.apply {
                startActivity(groceryNavigator.getGroceryWrapperActivity(this))
            }
        })
    }

    private fun openCouponDetails(couponModel: CouponModel) {
        activity?.supportFragmentManager?.let { supportFragmentManager ->
            val couponDialog =
                couponNavigator.openCouponDetail(couponModel = couponModel) as CouponDetailDialogFragment
            couponDialog.onUseNowClick = { useNowCouponModel ->
                viewModel.saveSelectedCoupon(couponModel = useNowCouponModel)
            }
            couponDialog.show(supportFragmentManager, CouponDetailDialogFragment.TAG)
        }
    }
}