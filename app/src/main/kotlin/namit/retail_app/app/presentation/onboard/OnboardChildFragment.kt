package namit.retail_app.app.presentation.onboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import namit.retail_app.R
import namit.retail_app.app.enums.OnboardType
import namit.retail_app.core.extension.visible
import namit.retail_app.core.presentation.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_onboard_child.*

class OnboardChildFragment : BaseFragment() {

    companion object {
        val TAG: String = OnboardChildFragment::class.java.simpleName
        private val ARG_ONBOARD_TYPE = "ARG_ONBOARD_TYPE"

        fun newInstance(type: String): OnboardChildFragment {
            val fragment =
                OnboardChildFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_ONBOARD_TYPE, type)
            }
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboard_child, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val iconRes: Int
        val titleRes: Int
        var subtitleRes: Int? = null
        when (OnboardType.valueOf(arguments!!.getString(ARG_ONBOARD_TYPE)!!)) {
            OnboardType.STORE -> {
                iconRes = R.drawable.img_onboard_store
                titleRes = R.string.welcome_to_retail
                subtitleRes = R.string.lets_buy_7_eleven_groceries_and_cafe
            }
            OnboardType.COUPON -> {
                iconRes = R.drawable.img_onboard_coupon
                titleRes = R.string.enjoy_our_collectable_coupons
            }
            OnboardType.PAY -> {
                iconRes = R.drawable.img_onboard_pay
                titleRes = R.string.pay_online
                subtitleRes = R.string.with_true_money_and_credit_card
            }
            OnboardType.UPDATE -> {
                iconRes = R.drawable.img_onboard_update
                titleRes = R.string.stay_updated
                subtitleRes =
                    R.string.more_features_coming_soon_shopping_list_recipes_subscriptions_etc
            }
        }

        iconRes.let { onboardImageView.setImageResource(it) }
        titleRes.let { onboardTileTextView.text = getString(it) }
        subtitleRes?.let {
            onboardSubtitleTextView.text = getString(it)
            onboardSubtitleTextView.visible()
        }
    }

}