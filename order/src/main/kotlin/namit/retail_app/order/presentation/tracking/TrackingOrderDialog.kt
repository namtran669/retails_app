package namit.retail_app.order.presentation.tracking

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import namit.retail_app.core.config.FAQ_URL
import namit.retail_app.core.data.entity.OrderModel
import namit.retail_app.core.enums.OrderStatus
import namit.retail_app.core.navigation.CoreNavigator
import namit.retail_app.core.presentation.base.BasePermissionFullScreenDialog
import namit.retail_app.core.presentation.dialog.alert.AlertMessageDialog
import namit.retail_app.core.presentation.dialog.alert.ConfirmDialog
import namit.retail_app.core.presentation.dialog.alert.QuestionDialog
import namit.retail_app.order.R
import namit.retail_app.order.presentation.widget.OrderTrackingStatusView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.dialog_tracking_order.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ObsoleteCoroutinesApi
class TrackingOrderDialog : BasePermissionFullScreenDialog() {

    companion object {
        const val TAG = "TrackingOrderDialog"
        private const val ARG_ORDER_DETAIL = "ARG_ORDER_DETAIL"

        fun newInstance(orderData: OrderModel): TrackingOrderDialog {
            val fragment = TrackingOrderDialog()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_ORDER_DETAIL, orderData)
            }
            return fragment
        }
    }

    private val viewModel: TrackingOrderViewModel by viewModel(parameters = {
        parametersOf(arguments?.getParcelable(ARG_ORDER_DETAIL))
    })

    private val coreNavigator: CoreNavigator by inject()

    var onDismissDialog: () -> Unit = {}
    var onCancelOrderSuccess: () -> Unit = {}
    var onOrderCompleted: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return LayoutInflater.from(context).inflate(R.layout.dialog_tracking_order, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewModel()
        viewModel.checkingOrderStatus()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissDialog.invoke()
    }

    private fun initView() {
        closeTrackingImageView.setOnClickListener {
            dismiss()
        }

        //todo need update action base on order status
        cancelButton.setOnClickListener {
            showCancelOrderDialog()
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(orderDetailWrapper)
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                //do nothing
            }

            override fun onStateChanged(bottomView: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    orderDetailWrapper.setBackgroundResource(R.drawable.bg_order_tracking)
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    orderDetailWrapper.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        })

        helpTextView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(FAQ_URL))
            startActivity(browserIntent)
        }
    }

    private fun bindViewModel() {
        viewModel.orderDetail.observe(viewLifecycleOwner, Observer {
            it.orderStoreInfo?.storeName?.let { storeName ->
                setNameOrderFrom(storeName)
            }
            orderDetailView.setOrderDetailData(it)
        })

        viewModel.orderStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                OrderStatus.CONFIRMED -> changeOrderStatusToConfirm()
                OrderStatus.READY_TO_SHIP -> changeOrderStatusToPrepare()
                OrderStatus.COMPLETED -> {
                    changeOrderStatusToSuccess()
                    onOrderCompleted.invoke()
                }

                else -> changeOrderStatusToDelivery()
            }
        })

        viewModel.showErrorMessage.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let { fragmentManager ->
                coreNavigator.alertMessageDialog(
                    getString(R.string.cancel_order_error_title),
                    it,
                    getString(R.string.ok)
                ).show(fragmentManager, AlertMessageDialog.TAG)
            }
        })

        viewModel.cancelOrderSuccess.observe(viewLifecycleOwner, Observer {
            onCancelOrderSuccess.invoke()
            dismiss()
        })
    }

    private fun setNameOrderFrom(name: String) {
        trackingArriveTimeInTextView.text = name
    }

    //TODO need function handle PENDING UI and show CancelOrderDialog

    private fun changeOrderStatusToConfirm() {
        trackingStateContentInTextView?.text = getString(R.string.order_confirmed)

        trackingOrderStatusView?.apply {
            toggleConfirmStatus(OrderTrackingStatusView.State.CURRENT)
            togglePrepareStatus(OrderTrackingStatusView.State.INACTIVE)
            toggleDeliveryStatus(OrderTrackingStatusView.State.INACTIVE)
            toggleSuccessStatus(OrderTrackingStatusView.State.INACTIVE)
        }

        cancelButton?.setOnClickListener {
            showCancelOrderDialog()
        }
    }

    private fun changeOrderStatusToPrepare() {
        trackingStateContentInTextView?.text = getString(R.string.food_is_prepared)

        trackingOrderStatusView?.apply {
            toggleConfirmStatus(OrderTrackingStatusView.State.ACTIVE)
            togglePrepareStatus(OrderTrackingStatusView.State.CURRENT)
            toggleDeliveryStatus(OrderTrackingStatusView.State.INACTIVE)
            toggleSuccessStatus(OrderTrackingStatusView.State.INACTIVE)
        }

        cancelButton?.setOnClickListener {
            showCanNotCancelOrderDialog()
        }
    }

    private fun changeOrderStatusToDelivery() {
        trackingStateContentInTextView?.text = getString(R.string.order_is_being_deliverd)

        trackingOrderStatusView?.apply {
            toggleConfirmStatus(OrderTrackingStatusView.State.ACTIVE)
            togglePrepareStatus(OrderTrackingStatusView.State.ACTIVE)
            toggleDeliveryStatus(OrderTrackingStatusView.State.CURRENT)
            toggleSuccessStatus(OrderTrackingStatusView.State.INACTIVE)
        }

        cancelButton?.setOnClickListener {
            showCanNotCancelOrderDialog()
        }
    }

    private fun changeOrderStatusToSuccess() {
        trackingStateContentInTextView?.text = getString(R.string.order_is_completed)
        trackingArriveTitleInTextView?.text = getString(R.string.your_order_has)
        trackingOrderStatusView?.apply {
            toggleConfirmStatus(OrderTrackingStatusView.State.ACTIVE)
            togglePrepareStatus(OrderTrackingStatusView.State.ACTIVE)
            toggleDeliveryStatus(OrderTrackingStatusView.State.ACTIVE)
            toggleSuccessStatus(OrderTrackingStatusView.State.CURRENT)
        }
        iconTrackingVideoView?.pauseAnimation()

        cancelButton?.setOnClickListener {
            showCanNotCancelOrderDialog()
        }
    }

    private fun showCanNotCancelOrderDialog() {
        activity?.supportFragmentManager?.let {
            coreNavigator.alertQuestionDialog(
                getString(R.string.can_not_cancel_order_dialog_title),
                getString(R.string.cancel_order_reason_already_prepare),
                getString(R.string.back),
                getString(R.string.contact)
            ).apply {
                onPositionClick = {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(FAQ_URL))
                    startActivity(browserIntent)
                }
            }.show(it, QuestionDialog.TAG)
        }
    }

    private fun showCancelOrderDialog() {
        activity?.supportFragmentManager?.let {
            coreNavigator.alertConfirmDialog(
                getString(R.string.cancel_order_dialog_tile),
                getString(R.string.cancel_order_confirm_content),
                getString(R.string.stay),
                getString(R.string.yes_cancel)
            ).apply {
                onPositionClick = {
                    viewModel.cancelOrder()
                }
            }.show(it, ConfirmDialog.TAG)
        }
    }

}
