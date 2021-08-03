package namit.retail_app.coupon.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import namit.retail_app.core.data.entity.CouponModel
import namit.retail_app.core.enums.MerchantType
import namit.retail_app.core.extension.gone
import namit.retail_app.core.extension.loadImage
import namit.retail_app.core.extension.toGrayScale
import namit.retail_app.core.presentation.base.BaseFullScreenDialog
import namit.retail_app.core.utils.getWidthScreenSize
import namit.retail_app.coupon.R
import kotlinx.android.synthetic.main.dialog_coupon_detail.*
import kotlinx.android.synthetic.main.item_vertical_coupon_x_point.*
import kotlinx.android.synthetic.main.item_vertical_coupon_x_point.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CouponDetailDialogFragment : BaseFullScreenDialog() {

    companion object {
        private const val ARG_COUPON = "ARG_COUPON"
        const val TAG = "CouponDetailDialogFragment"
        fun newInstance(couponModel: CouponModel): CouponDetailDialogFragment {
            val dialog = CouponDetailDialogFragment()
            dialog.arguments = Bundle().apply {
                putParcelable(ARG_COUPON, couponModel)
            }
            return dialog
        }
    }

    private val viewModel: CouponDetailViewModel by viewModel(
        parameters = {
            parametersOf(
                arguments!!.getParcelable(ARG_COUPON)
            )
        }
    )

    private var couponView: View? = null
    var onUseNowClick: (couponModel: CouponModel) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.dialog_coupon_detail, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewModel()
        viewModel.renderCouponType()
    }

    private fun initView() {
        backImageView.setOnClickListener {
            dismiss()
        }

        useNowButton.setOnClickListener {
            onUseNowClick.invoke(arguments!!.getParcelable(ARG_COUPON)!!)
            dismiss()
        }
    }

    private fun bindViewModel() {
        viewModel.renderMerchantLogo.observe(viewLifecycleOwner, Observer {
            couponView?.apply {
                couponIconImageView.visibility = View.GONE
                couponMerchantIconImageView.visibility = View.VISIBLE
                couponMerchantIconImageView.loadImage(it)
                couponConstraintLayout.background =
                    ContextCompat.getDrawable(context, R.drawable.img_coupon_merchant_bg)
                couponDesc1TextView.setTextColor(ContextCompat.getColor(context, R.color.trout))
                couponDesc2TextView.setTextColor(ContextCompat.getColor(context, R.color.trout))
                couponValueTextView.setTextColor(ContextCompat.getColor(context, R.color.trout))
                lineImageView.setImageResource(R.drawable.ic_coupon_line_black)
            }
        })

        viewModel.renderCouponMerchantType.observe(viewLifecycleOwner, Observer { merchantType ->
            context?.let { context ->
                when (merchantType) {
                    MerchantType.GROCERY -> {
                        couponView?.couponIconImageView?.setImageResource(R.drawable.ic_coupon_grocery)
                        couponView?.couponConstraintLayout?.background =
                            ContextCompat.getDrawable(context, R.drawable.img_coupon_grocery_bg)
                    }
                    MerchantType.RESTAURANT -> {
                        couponView?.couponIconImageView?.setImageResource(R.drawable.ic_coupon_food)
                        couponView?.couponConstraintLayout?.background =
                            ContextCompat.getDrawable(context, R.drawable.img_coupon_food_bg)
                    }
                    MerchantType.CONVENIENCE -> {
                        couponView?.couponIconImageView?.setImageResource(R.drawable.ic_coupon_convenience)
                        couponView?.couponConstraintLayout?.background =
                            ContextCompat.getDrawable(context, R.drawable.img_coupon_convenience_bg)
                    }
                    else -> {
                        couponView?.couponIconImageView?.setImageResource(R.drawable.ic_coupon_cafe)
                        couponView?.couponConstraintLayout?.background =
                            ContextCompat.getDrawable(context, R.drawable.img_coupon_cafe_bg)
                    }
                }
            }
        })

        viewModel.renderCouponName.observe(viewLifecycleOwner, Observer {
            promotionTextView.text = it
        })

        viewModel.renderCouponDescription.observe(viewLifecycleOwner, Observer {
            detailsTextView.text = it
        })

        viewModel.renderCouponExpireDate.observe(viewLifecycleOwner, Observer {
            expireDateTextView.text = it
        })

        viewModel.renderCouponDuration.observe(viewLifecycleOwner, Observer {
            durationTextView.text = getString(R.string.until).plus(" $it")
        })

        viewModel.renderCouponBuyXGetX.observe(viewLifecycleOwner, Observer {
            renderCoupon(R.layout.item_vertical_coupon_freedie)
        })

        viewModel.renderCouponDiscountAmount.observe(viewLifecycleOwner, Observer {
            renderCoupon(R.layout.item_vertical_coupon_discount_amout)
        })

        viewModel.renderCouponDiscountPercentage.observe(viewLifecycleOwner, Observer {
            renderCoupon(R.layout.item_vertical_coupon_discount_percentage)
        })

        viewModel.renderCouponFreeBie.observe(viewLifecycleOwner, Observer {
            renderCoupon(R.layout.item_vertical_coupon_free_delivery)
        })

        viewModel.renderCouponXPoint.observe(viewLifecycleOwner, Observer {
            renderCoupon(R.layout.item_vertical_coupon_x_point)
        })

        viewModel.renderFlashDeal.observe(viewLifecycleOwner, Observer {
            couponView?.apply {
                countDownImageView.visibility = View.VISIBLE
                countDownTextView.visibility = View.VISIBLE
                countDownTextView.text = it
            }
        })

        viewModel.renderRanOut.observe(viewLifecycleOwner, Observer {
            couponView?.apply {
                couponConstraintLayout.toGrayScale()
                ranOutImageView.visibility = View.VISIBLE
            }
        })

        viewModel.renderValue.observe(viewLifecycleOwner, Observer {
            couponView?.couponValueTextView?.text = it
        })
    }

    private fun renderCoupon(couponLayoutId: Int) {
        context?.let {
            val couponWidth = (getWidthScreenSize(context = it) * 0.90).toInt()
            val couponHeight = (couponWidth * 0.45).toInt()
            couponView = LayoutInflater.from(context).inflate(couponLayoutId, null)
            couponView!!.couponConstraintLayout.apply {
                layoutParams.width = couponWidth
                layoutParams.height = couponHeight
            }
            couponContainer.addView(couponView)
            couponView?.collectNowButton?.gone()
            couponView?.collectNowButton?.setOnClickListener {
                onUseNowClick.invoke(arguments!!.getParcelable(ARG_COUPON)!!)
                dismiss()
            }

            viewModel.renderCouponDescription()
        }
    }
}