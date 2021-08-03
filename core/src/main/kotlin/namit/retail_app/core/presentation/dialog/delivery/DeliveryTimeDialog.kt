package namit.retail_app.core.presentation.dialog.delivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import namit.retail_app.core.R
import namit.retail_app.core.data.entity.TimeSlot
import namit.retail_app.core.extension.dpToPx
import namit.retail_app.core.extension.invisible
import namit.retail_app.core.extension.visible
import namit.retail_app.core.presentation.base.BaseFullScreenDialog
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import kotlinx.android.synthetic.main.dialog_delivery_time.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DeliveryTimeDialog : BaseFullScreenDialog() {

    companion object {
        const val TAG = "DeliveryTimeDialog"
        const val ARG_MERCHANT_ID = "ARG_MERCHANT_ID"
        fun newInstance(): DeliveryTimeDialog {
            return DeliveryTimeDialog()
        }

        fun newInstance(merchantId: String): DeliveryTimeDialog {
            val fragment = DeliveryTimeDialog()
            val args = Bundle()
            args.putString(ARG_MERCHANT_ID, merchantId)
            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel: DeliveryTimeViewModel by viewModel(parameters = {
        parametersOf(arguments?.getString(ARG_MERCHANT_ID))
    })

    private var deliveryDaySkeletonScreen: SkeletonScreen? = null
    private var deliveryTimeSkeletonScreen: SkeletonScreen? = null

    private lateinit var deliveryDayAdapter: DeliveryDayAdapter
    private lateinit var deliveryTimeAdapter: DeliveryTimeAdapter
    var onConfirm: (timeSlot: TimeSlot) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.dialog_delivery_time, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deliveryDayAdapter = DeliveryDayAdapter().apply {
            onSelectDay = {
                viewModel.updateDaySlotSection(it)
            }
        }

        deliveryTimeAdapter = DeliveryTimeAdapter()
        initView()
        bindViewModel()
        viewModel.loadDeliveryData()
    }

    private fun initView() {
        closeImageView.setOnClickListener {
            dismiss()
        }

        deliveryDayRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = deliveryDayAdapter
        }

        deliveryTimeRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = deliveryTimeAdapter
        }

        confirmButton.setOnClickListener {
            viewModel.saveSelectedTimeSlot()
        }
    }

    private fun bindViewModel() {
        viewModel.deliveryDaySlotList.observe(viewLifecycleOwner, Observer {
            deliveryDayAdapter.items = it
        })

        viewModel.deliveryTimeSlotList.observe(viewLifecycleOwner, Observer {
            toggleDeliveryNowView(false)
            deliveryTimeAdapter.items = it
        })

        viewModel.deliveryDaySelectedIndex.observe(viewLifecycleOwner, Observer {
            deliveryDayRecyclerView.scrollToPosition(it)
        })

        viewModel.deliveryTimeSlotSelectedIndex.observe(viewLifecycleOwner, Observer {
            deliveryTimeRecyclerView.scrollToPosition(it)
        })

        viewModel.deliveryNowLiveData.observe(viewLifecycleOwner, Observer {
            toggleDeliveryNowView(true)
            if (it.isFull) {
                deliveryNowInfoTextView.text = resources.getString(R.string.delivery_now_off_msg)
                deliveryNowImageView.setImageResource(R.drawable.ic_box_not_now_time_slot)
            } else {
                deliveryNowInfoTextView.text = resources.getString(R.string.delivery_now_on_msg)
                deliveryNowImageView.setImageResource(R.drawable.ic_box_time_slot)
            }
        })

        viewModel.confirmButtonStatus.observe(viewLifecycleOwner, Observer {
            confirmButton.isEnabled = it
        })

        viewModel.selectedTimeSlot.observe(viewLifecycleOwner, Observer {
            onConfirm.invoke(it)
            this.dismiss()
        })

        viewModel.showLoadingView.observe(viewLifecycleOwner, Observer { isShow ->
            if (isShow) {
                deliveryIconViewSkeleton.visible()
                timeSlotTitleViewSkeleton.visible()
                timeSlotDetailViewSkeleton1.visible()
                timeSlotDetailViewSkeleton2.visible()
                deliveryIconImageView.invisible()
                timeSlotTitleTextView.invisible()
                timeSlotDetailTextView.invisible()
                confirmButton.text = ""
                if (deliveryDaySkeletonScreen != null) {
                    deliveryDaySkeletonScreen?.show()
                } else {
                    deliveryDaySkeletonScreen = Skeleton.bind(deliveryDayRecyclerView)
                        .adapter(deliveryDayAdapter)
                        .shimmer(false)
                        .frozen(false)
                        .count(1)
                        .load(R.layout.view_delivery_day_skeleton)
                        .show()
                }
                deliveryDayRecyclerView.setPadding(0.dpToPx(), 0, 0.dpToPx(), 0)

                if (deliveryTimeSkeletonScreen != null) {
                    deliveryTimeSkeletonScreen?.show()
                } else {
                    deliveryTimeSkeletonScreen = Skeleton.bind(deliveryTimeRecyclerView)
                        .adapter(deliveryTimeAdapter)
                        .shimmer(false)
                        .frozen(false)
                        .count(1)
                        .load(R.layout.view_delivery_time_skeleton)
                        .show()
                }
            } else {
                deliveryIconViewSkeleton.invisible()
                timeSlotTitleViewSkeleton.invisible()
                timeSlotDetailViewSkeleton1.invisible()
                timeSlotDetailViewSkeleton2.invisible()
                deliveryIconImageView.visible()
                timeSlotTitleTextView.visible()
                timeSlotDetailTextView.visible()
                confirmButton.text = getString(R.string.confirm)
                deliveryDaySkeletonScreen?.hide()
                deliveryDayRecyclerView.setPadding(15.dpToPx(), 0, 15.dpToPx(), 0)
                deliveryTimeSkeletonScreen?.hide()
            }
        })
    }

    private fun toggleDeliveryNowView(isOn: Boolean) {
        if (isOn) {
            deliveryTimeRecyclerView.visibility = View.GONE
            deliveryNowLayout.visibility = View.VISIBLE
        } else {
            deliveryNowLayout.visibility = View.GONE
            deliveryTimeRecyclerView.visibility = View.VISIBLE
        }
    }
}