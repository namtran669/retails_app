package namit.retail_app.app.navigation

import androidx.fragment.app.Fragment
import namit.retail_app.core.navigation.NotificationNavigator
import namit.retail_app.notification.presentation.notification_list.NotificationListFragment

class NotificationNavigatorImpl: NotificationNavigator {

    override fun getNotificationListFragment(): Fragment {
        return NotificationListFragment.getNewInstance()
    }
}