package namit.retail_app.notification.presentation.notification_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import namit.retail_app.core.navigation.CartNavigator
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.notification.R
import namit.retail_app.notification.data.entity.NotificationContent
import namit.retail_app.notification.data.entity.NotificationState
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_notification_list.*
import org.koin.android.ext.android.inject


class NotificationListFragment : BaseFragment() {

    companion object {
        const val TAG = "NotificationListFragment"
        fun getNewInstance(): NotificationListFragment {
            return NotificationListFragment()
        }
    }

    lateinit var notificationAdapter: SectionedRecyclerViewAdapter
    private val cartNavigator: CartNavigator by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_notification_list, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        //todo test UI data
        dummyNotification().forEach { (key, list) ->
            notificationAdapter.addSection(NotificationSection(key, list).apply {
                onReorderClicked = {
                    activity?.let { startActivity(cartNavigator.getCartActivity(it)) }
                }
            })
        }
        notificationAdapter.notifyDataSetChanged()

    }

    private fun initView() {
        notificationAdapter = SectionedRecyclerViewAdapter()

        notificationListRecyclerView.apply {
            adapter = notificationAdapter
            val dividerItemDecoration = DividerItemDecoration(
                this.context,
                LinearLayoutManager.VERTICAL
            )
            ContextCompat.getDrawable(
                this.context,
                R.drawable.divider_vertical_mercury
            )?.apply { dividerItemDecoration.setDrawable(this) }
            addItemDecoration(dividerItemDecoration)
        }
    }

    private fun dummyNotification(): MutableMap<String, MutableList<NotificationContent>> {
        val notificationAllData: MutableMap<String, MutableList<NotificationContent>> =
            mutableMapOf()

        val notificationList = mutableListOf<NotificationContent>()
        notificationList.add(
            NotificationContent(
                title = "You’ve got 56.25฿ cash back!",
                content = "Apply promo code ‘DEC25’ for 25% off. Promo valid: 24-26 Dec ‘19  Promo valid: 24-26 Dec ‘19",
                createdAt = "2 hours ago",
                type = NotificationState.NORMAL,
                isUnSeen = true
            )
        )

        notificationList.add(
            NotificationContent(
                title = "You’ve got 56.25฿ cash back!",
                content = "Apply promo code ‘DEC25’ for 25% off. Promo valid: 24-26 Dec ‘19 Promo valid: 24-26 Dec ‘19",
                createdAt = "2 hours ago",
                type = NotificationState.NORMAL
            )
        )

        notificationList.add(
            NotificationContent(
                title = "Change of opening hours",
                content = "Some of the shops you ordered from have changed their hours Promo valid: 24-26 Dec ‘19",
                createdAt = "2 hours ago",
                type = NotificationState.NORMAL
            )
        )

        notificationAllData["Today"] = notificationList
        notificationAllData["Last 7 Days"] = notificationList

        val notificationOrderList = mutableListOf<NotificationContent>()
        notificationOrderList.add(
            NotificationContent(
                title = "Dressed - Wisdom 101",
                content = "3 items . 22/9 Sukhumvit Rd.",
                createdAt = "10 Dec 2019 . 22:35",
                orderStatus = "Order successful",
                type = NotificationState.ORDER,
                isUnSeen = true
            )
        )

        notificationOrderList.add(
            NotificationContent(
                title = "Dressed - Wisdom 101",
                content = "3 items . 22/10 Sukhumvit Road test ",
                createdAt = "10 Dec 2019 . 22:35",
                orderStatus = "Your order has been confirmed",
                type = NotificationState.ORDER
            )
        )

        notificationAllData["Last month"] = notificationOrderList

        return notificationAllData
    }
}