package namit.retail_app.coupon.presentation.detail

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.CouponModel
import namit.retail_app.core.enums.CouponType
import namit.retail_app.core.enums.MerchantType
import namit.retail_app.core.extension.toDate
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.tracking.EventTrackingManager
import java.util.concurrent.TimeUnit

class CouponDetailViewModel(
    private val couponModel: CouponModel,
    private val eventTrackingManager: EventTrackingManager
) : BaseViewModel() {

    companion object {
        const val COUPON_DURATION_DATE_FORMAT = "dd MMM yyyy"
    }

    val renderCouponBuyXGetX = MutableLiveData<Unit>()
    val renderCouponDiscountAmount = MutableLiveData<Unit>()
    val renderCouponDiscountPercentage = MutableLiveData<Unit>()
    val renderCouponFreeBie = MutableLiveData<Unit>()
    val renderCouponXPoint = MutableLiveData<Unit>()
    val renderMerchantLogo = MutableLiveData<String>()
    val renderCouponMerchantType = MutableLiveData<MerchantType>()
    val renderCouponName = MutableLiveData<String>()
    val renderCouponDescription = MutableLiveData<String>()
    val renderCouponExpireDate = MutableLiveData<String>()
    val renderCouponDuration = MutableLiveData<String>()
    val renderRanOut = MutableLiveData<Unit>()
    val renderFlashDeal = MutableLiveData<String>()
    val renderValue = MutableLiveData<String>()
    var couponFlashDealTimer: CountDownTimer? = null

    fun renderCouponType() {
        eventTrackingManager.trackCouponView(couponType = couponModel.couponType)
        when (couponModel.couponType) {
            //TODO Implement later
//            CouponType.FREEBIE -> {
//                renderCouponBuyXGetX.value = Unit
//            }
            CouponType.FIXED -> {
                renderCouponDiscountAmount.value = Unit
            }
            CouponType.PERCENTAGE -> {
                renderCouponDiscountPercentage.value = Unit
            }
            CouponType.DELIVERY_FEE -> {
                renderCouponFreeBie.value = Unit
            }
            else -> {
                renderCouponXPoint.value = Unit
            }
        }
    }

    fun renderCouponDescription() {
        if (couponModel.couponType == CouponType.FIXED || couponModel.couponType == CouponType.PERCENTAGE) {
            renderValue.value = couponModel.couponValue
        }

        renderCouponName.value = couponModel.name
        renderCouponDescription.value = couponModel.description
        renderCouponExpireDate.value = couponModel.endDate?.toDate(dateFormat = COUPON_DURATION_DATE_FORMAT)
        renderCouponDuration.value = couponModel.endDate?.toDate(dateFormat = COUPON_DURATION_DATE_FORMAT)

        if (couponModel.couponMerchantType == MerchantType.MERCHANT) {
            renderMerchantLogo.value = couponModel.merchantInfoItem?.imageUrl ?: ""
        } else {
            renderCouponMerchantType.value = couponModel.couponMerchantType
        }

        if (couponModel.isRanOut) {
            renderRanOut.value = Unit
        }

        if (couponModel.isFlashDeals && couponModel.endDate ?: 0 - System.currentTimeMillis() > 0) {
            couponFlashDealTimer = object : CountDownTimer(couponModel.endDate ?: 0, 500) {
                override fun onFinish() {
                    renderCouponDescription()
                }

                override fun onTick(millisUntilFinished: Long) {
                    val timeDiff = millisUntilFinished - System.currentTimeMillis()
                    if (timeDiff > 0) {
                        renderFlashDeal.value = String.format(
                            "%02d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(timeDiff),
                            TimeUnit.MILLISECONDS.toMinutes(timeDiff) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(timeDiff) % TimeUnit.MINUTES.toSeconds(1)
                        )
                    }
                }
            }.start()
        }
    }
}