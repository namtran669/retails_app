package namit.retail_app.app.presentation.splash

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import namit.retail_app.BuildConfig
import namit.retail_app.R
import namit.retail_app.address.presentation.set_location.SetLocationDialog
import namit.retail_app.app.data.entity.FirebaseRemoteConfigKey
import namit.retail_app.core.extension.intentToPlayStore
import namit.retail_app.core.navigation.*
import namit.retail_app.core.presentation.dialog.alert.AlertMessageDialog
import namit.retail_app.core.presentation.dialog.alert.QuestionDialog
import namit.retail_app.core.utils.LocaleUtils
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SplashScreenActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_OPEN_LOGIN = 1001
        private const val REQUEST_CODE_OPEN_ONBOARD = 1002
        private const val SPLASH_DELAY: Long = 4000 //4 seconds
        fun openActivity(context: Context): Intent {
            return Intent(context, SplashScreenActivity::class.java)
        }
    }

    private val splashScreenViewModel: SplashScreenViewModel by viewModel()
    private val mainTabNavigator: MainTabNavigator by inject()
    private val addressNavigator: AddressNavigator by inject()
    private val authNavigator: AuthNavigator by inject()
    private val appNavigator: AppNavigator by inject()
    private val coreNavigator: CoreNavigator by inject()

    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
    private var splashDelayHandler: Handler? = null
    private val splashDelayRunnable: Runnable = Runnable {
        if (!isFinishing) {
            splashScreenViewModel.checkUserLogin()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        initFirebaseRemoteConfig()
        bindViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_OPEN_LOGIN -> splashScreenViewModel.checkUserLogin()
            REQUEST_CODE_OPEN_ONBOARD -> splashScreenViewModel.checkUserLogin()
            SetLocationDialog.REQUEST_CODE_SETTING_LOCATION -> {
                val targetFragment = supportFragmentManager.findFragmentByTag(SetLocationDialog.TAG)
                targetFragment?.onActivityResult(requestCode, resultCode, data)
            }
            SetLocationDialog.REQUEST_CODE_PERMISSION_LOCATION -> {
                val targetFragment = supportFragmentManager.findFragmentByTag(SetLocationDialog.TAG)
                targetFragment?.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        splashDelayHandler?.removeCallbacks(splashDelayRunnable)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun bindViewModel() {
        splashScreenViewModel.deviceIsRooted.observe(this, Observer { isRooted ->
            if (isRooted) {
                supportFragmentManager.let {
                    coreNavigator.alertMessageDialog(
                        title = getString(R.string.detect_root_device_title),
                        message = getString(R.string.detect_root_device_message),
                        buttonText = getString(R.string.close)
                    ).apply {
                        isCancelable = false
                        onDismiss = {
                            finish()
                        }
                    }.show(it, QuestionDialog.TAG)
                }
            } else {
                splashScreenViewModel.setLanguageApp()
            }
        })

        splashScreenViewModel.startSplashScreen.observe(this, Observer {
            startSplashScreen()
        })

        splashScreenViewModel.presentForceUpdate.observe(this, Observer {
            val forceUpdate = it
            coreNavigator.alertMessageDialog(
                title = if (LocaleUtils.isThai()) forceUpdate.titleTH else forceUpdate.titleEN,
                message = if (LocaleUtils.isThai()) forceUpdate.messageTH else forceUpdate.messageEN,
                buttonText = getString(R.string.update)
            ).apply {
                isCancelable = false
                onDismiss = {
                    intentToPlayStore()
                    finish()
                }
            }.show(supportFragmentManager, QuestionDialog.TAG)
        })

        splashScreenViewModel.presentSoftUpdate.observe(this, Observer {
            val softUpdate = it
            coreNavigator.alertQuestionDialog(
                title = if (LocaleUtils.isThai()) softUpdate.titleTH else softUpdate.titleEN,
                message = if (LocaleUtils.isThai()) softUpdate.messageTH else softUpdate.messageEN,
                positiveButtonText = getString(R.string.update),
                negativeButtonText = getString(R.string.close)
            ).apply {
                isCancelable = false
                onPositionClick = {
                    intentToPlayStore()
                    finish()
                }
                onNegativeClick = {
                    if (!isFinishing) {
                        splashScreenViewModel.checkUserLogin()
                    }
                }
            }.show(supportFragmentManager, QuestionDialog.TAG)
        })

        splashScreenViewModel.presentStoreClosed.observe(this, Observer { storeClosedModel ->
            coreNavigator.alertMessageDialog(
                title = if (LocaleUtils.isThai()) storeClosedModel.titleTH else storeClosedModel.titleEN,
                message = if (LocaleUtils.isThai()) storeClosedModel.messageTH else storeClosedModel.messageEN,
                buttonText = getString(R.string.close)
            ).apply {
                isCancelable = false
                onDismiss = {
                    finish()
                }
            }.show(supportFragmentManager, AlertMessageDialog.TAG)
        })

        splashScreenViewModel.presentToLogin.observe(this, Observer {
            startActivityForResult(authNavigator.openLogin(context = this), REQUEST_CODE_OPEN_LOGIN)
        })

        splashScreenViewModel.presentToSelectLocation.observe(this, Observer {
            val setLocationDialog = addressNavigator.getSetLocationDialog() as SetLocationDialog
            setLocationDialog.apply {
                isCancelable = false
                onSetLocationListener = {
                    openMainTab()
                    dismiss()
                }

                afterDismissAction = {
                    finish()
                }
            }

            setLocationDialog.show(supportFragmentManager, SetLocationDialog.TAG)
        })

        splashScreenViewModel.setLanguage.observe(this, Observer {
            LocaleUtils.applyLanguage(this, it)
        })

        splashScreenViewModel.presentOnboard.observe(this, Observer {
            startActivityForResult(
                appNavigator.getOnBoardActivity(context = this),
                REQUEST_CODE_OPEN_ONBOARD
            )
        })
    }

    private fun openMainTab() {
        startActivity(mainTabNavigator.getTabActivity(context = this))
    }

    private fun startSplashScreen() {
        splashDelayHandler = Handler()
        splashDelayHandler!!.postDelayed(splashDelayRunnable, SPLASH_DELAY)
    }

    private fun initFirebaseRemoteConfig() {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(600)
            .build()

        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { taskResult ->
                if (taskResult.isSuccessful) {
                    val remoteConfigVersionCode = try {
                        firebaseRemoteConfig.getString(FirebaseRemoteConfigKey.KEY_VERSION_CODE).toInt()
                    } catch (e: NumberFormatException) {
                        BuildConfig.VERSION_CODE
                    }
                    splashScreenViewModel.handleRemoteConfig(
                        versionCode = BuildConfig.VERSION_CODE,
                        remoteVersionCode = remoteConfigVersionCode,
                        storeClosedData = firebaseRemoteConfig.getString(
                            FirebaseRemoteConfigKey.KEY_STORE_CLOSED
                        ),
                        forceUpdateData = firebaseRemoteConfig.getString(
                            FirebaseRemoteConfigKey.KEY_FORCE_UPDATE
                        )
                    )
                } else {
                    startSplashScreen()
                }
            }
    }
}
