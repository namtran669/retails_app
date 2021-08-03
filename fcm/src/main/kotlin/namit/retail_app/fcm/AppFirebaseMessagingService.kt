package namit.retail_app.fcm


import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.PendingIntent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import namit.retail_app.core.config.GRAPHQL_HASURA_API_URL
import namit.retail_app.core.data.api.BaseOkHttpClientBuilder
import namit.retail_app.core.data.api.BaseUrl
import namit.retail_app.core.data.graphql.ApolloBuilder
import namit.retail_app.core.data.interceptor.HasuraInterceptor
import namit.retail_app.core.navigation.AppNavigator
import namit.retail_app.core.navigation.MainTabNavigator
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import hasura.CreateNotificationTokenMutation
import org.koin.android.ext.android.inject


class AppFirebaseMessagingService : FirebaseMessagingService() {

    private val appNavigator: AppNavigator by inject()
    private val mainNavigator: MainTabNavigator by inject()

    companion object {
        const val TAG = "FCM Service"
        const val RETAIL_APP_CHANNEL = "Retailapp notification channel"
        const val RETAIL_CHANNEL_DESCRIPTION = "Retailapp notification channel to show notification"
        const val RETAIL_CHANNEL_ID = "WF_2020"
        const val ID = "id"
        const val CONTENT = "content"
        const val UPDATE_FCM_TOKEN_SUCCESS = "UPDATE_FCM_TOKEN_SUCCESS"
        const val UPDATE_FCM_TOKEN_FAIL = "UPDATE_FCM_TOKEN_FAIL"
    }

    override fun onNewToken(fcmToken: String) {
        super.onNewToken(fcmToken)
        try {
            val builder = ApolloBuilder(
                BaseOkHttpClientBuilder(HasuraInterceptor()).init(),
                BaseUrl(GRAPHQL_HASURA_API_URL)
            )

            val apollo: ApolloClient = builder.build()
            val mutate = CreateNotificationTokenMutation.builder()
                .uuid(getUUID())
                .token(fcmToken).build()

            apollo.mutate(mutate).enqueue(object :
                ApolloCall.Callback<CreateNotificationTokenMutation.Data>() {
                override fun onFailure(ex: ApolloException) {
                    Log.e(TAG, "Error", ex)
                }

                override fun onResponse(response: Response<CreateNotificationTokenMutation.Data>) {
                    val result = response.data()?.createNotificationToken()
                    if (result?.token()?.isNotEmpty() == true) {
                        Log.d(TAG, UPDATE_FCM_TOKEN_SUCCESS)
                    }
                }
            })

        } catch (ex: Exception) {
            Log.e(TAG, UPDATE_FCM_TOKEN_FAIL, ex)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val data = remoteMessage.data
        //Todo: will improve validate later to show notification content
        val id = data[ID]?.toInt() ?: 0
        val content = data[CONTENT] ?: ""
        showNotification(id, content)
    }

    private fun showNotification(id: Int, content: String) {
        val intent = if (isAppOpen()) {
            mainNavigator.getTabActivity(this)
        } else {
            appNavigator.getSplashScreenActivity(this)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val builder = NotificationCompat.Builder(this, RETAIL_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_notification_dev)
            .setContentTitle(getText(R.string.app_name))
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(id, builder.build())
        }
    }

    private fun getUUID(): String {
        var idInfo: AdvertisingIdClient.Info? = null
        try {
            idInfo = AdvertisingIdClient.getAdvertisingIdInfo(this)

        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var advertId = ""
        try {
            advertId = idInfo!!.id
            Log.d("DeviceID", advertId)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return advertId
    }

    private fun isAppOpen(): Boolean {
        val appPackageName = application?.packageName
        val activityManager = getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses: List<RunningAppProcessInfo>? = activityManager.runningAppProcesses

        appProcesses?.forEach {
            if (it.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                && it.processName == appPackageName
            ) {
                return true
            }
        }

        return false
    }
}