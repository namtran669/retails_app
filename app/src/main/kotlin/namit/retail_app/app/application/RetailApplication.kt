package namit.retail_app.app.application

import androidx.multidex.MultiDexApplication
import namit.retail_app.BuildConfig
import namit.retail_app.app.di.koinModuleList
import com.google.android.gms.ads.MobileAds
import io.branch.referral.Branch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RetailApplication: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
        initGoogleAds()
        initBranch()
    }

    private fun initKoin() {
        startKoin {
            if (BuildConfig.DEBUG) {
                printLogger()
            }
            androidContext(this@RetailApplication)
            modules(koinModuleList)
        }
    }

    // Setup GoogleAds for Branch.io
    private fun initGoogleAds() = MobileAds.initialize(this)

    private fun initBranch() {
        if (BuildConfig.DEBUG) {
            // Branch logging for debugging
            Branch.enableDebugMode()
        }
        // Branch object initialization
        Branch.getAutoInstance(this)
    }
}