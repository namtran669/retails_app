package namit.retail_app.coupon.presentation.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import namit.retail_app.core.data.entity.CouponModel
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.extension.gone
import namit.retail_app.core.extension.visible
import namit.retail_app.core.presentation.base.BaseFullScreenDialog
import namit.retail_app.core.utils.getWidthScreenSize
import namit.retail_app.coupon.R
import namit.retail_app.coupon.presentation.adapter.CouponVerticalListAdapter
import namit.retail_app.coupon.presentation.coupon.CouponViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.dialog_coupon_list.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CouponMerchantListDialogFragment : BaseFullScreenDialog() {

    companion object {
        private const val ARG_LIST_MERCHANT_INFO = "ARG_LIST_MERCHANT_INFO"
        private const val ARG_LIST_CART_ID = "ARG_LIST_CART_ID"
        const val TAG = "CouponMerchantListDialogFragment"
        fun newInstance(merchantInfoItemList: List<MerchantInfoItem>): CouponMerchantListDialogFragment {
            val dialog = CouponMerchantListDialogFragment()
            dialog.arguments = Bundle().apply {
                putParcelableArrayList(
                    ARG_LIST_MERCHANT_INFO,
                    ArrayList<MerchantInfoItem>(merchantInfoItemList)
                )
            }
            return dialog
        }

        fun newInstance(cartId: Int): CouponMerchantListDialogFragment {
            val dialog = CouponMerchantListDialogFragment()
            dialog.arguments = Bundle().apply {
                putInt(
                    ARG_LIST_CART_ID,
                    cartId
                )
            }
            return dialog
        }
    }

    private val viewModel: CouponMerchantListViewModel by viewModel(parameters = {
        val merchantList = (arguments?.getParcelableArrayList<MerchantInfoItem>(ARG_LIST_MERCHANT_INFO))?.toList() ?: listOf()
        parametersOf(
            merchantList,
            (arguments?.getInt(ARG_LIST_CART_ID, -1))
        )
    })
    private val couponViewModel: CouponViewModel by viewModel()
    private lateinit var couponVerticalListAdapter: CouponVerticalListAdapter

    var onCouponSelected: (couponModel: CouponModel) -> Unit = {}
    var onUseNowCouponClicked: (couponModel: CouponModel) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.dialog_coupon_list, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewModel()
        viewModel.render()
    }

    private fun initView() {
        closeImageView.setOnClickListener {
            dismiss()
        }

        titleTextView.text = getString(R.string.available_coupons)

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLinearLayout)
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                //do nothing
            }

            override fun onStateChanged(bottomView: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetLinearLayout
                        .setBackgroundColor(Color.WHITE)
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetLinearLayout
                        .setBackgroundResource(R.drawable.shape_white_top_left_right_30_radius)
                }
            }
        })

        couponVerticalListAdapter = CouponVerticalListAdapter().apply {
            context?.let { context ->
                val itemWidthSize = (getWidthScreenSize(context = context) * 0.95).toInt()
                itemWidth = itemWidthSize
                itemHeight = (itemWidthSize * 0.45).toInt()
            }
            onCouponClick = {
                onCouponSelected.invoke(it)
            }
            onUseNowClick = {
                onUseNowCouponClicked.invoke(it)
            }
        }

        couponRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = couponVerticalListAdapter
        }
    }

    private fun bindViewModel() {
        viewModel.renderCouponWithMerchants.observe(viewLifecycleOwner, Observer {
            couponViewModel.loadCouponListByMerchantId(merchantIds = it)
        })

        viewModel.renderCouponWithCart.observe(viewLifecycleOwner, Observer {
            couponViewModel.loadCouponListByCart(cartId = it)
        })

        couponViewModel.couponList.observe(viewLifecycleOwner, Observer {
            couponRecyclerView.scrollTo(0, 0)
            couponVerticalListAdapter.items = it
        })

        couponViewModel.showEmpty.observe(viewLifecycleOwner, Observer { isShow ->
            if (isShow) {
                couponRecyclerView.gone()
                emptyLayoutView.visible()
            } else {
                couponRecyclerView.visible()
                emptyLayoutView.gone()
            }
        })
    }
}