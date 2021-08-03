package namit.retail_app.core.provider

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

interface AdvertisingIdProvider {
    fun execute(): String?
}

class AdvertisingIdProviderImpl(private val context: Context): AdvertisingIdProvider, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.Main

    override fun execute(): String? {
        var idInfo: AdvertisingIdClient.Info? = null
        try {
            idInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var advertId: String? = null
        try {
            advertId = idInfo!!.id
            Log.d("DeviceID", advertId)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return advertId
    }

    fun cleanUp() {
        job.cancel()
    }
}
