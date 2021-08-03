package namit.retail_app.core.presentation.base

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import namit.retail_app.core.R
import namit.retail_app.core.enums.RequestUserPermissionResult
import namit.retail_app.core.extension.addTo
import namit.retail_app.core.presentation.dialog.alert.AlertMessageDialog
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable

open class BasePermissionFullScreenDialog: DialogFragment() {

    private lateinit var rxPermission: RxPermissions
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rxPermission = RxPermissions(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            requestFeature(Window.FEATURE_NO_TITLE)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        val displayRectangle = Rect()
        val window = activity?.window
        window?.decorView?.getWindowVisibleDisplayFrame(displayRectangle)
        dialog?.window?.apply {
            setWindowAnimations(R.style.TranslateUpDownAnimation)
            val windowLayoutParams = WindowManager.LayoutParams()
            windowLayoutParams.copyFrom(attributes)
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    protected fun requestAllowPermission(permission: String,
                                         onResult: (result: RequestUserPermissionResult) -> Unit) {
        rxPermission.requestEach(permission).subscribe { result ->
            when {
                result.granted ->
                    onResult.invoke(RequestUserPermissionResult.ALLOW)
                result.shouldShowRequestPermissionRationale ->
                    onResult.invoke(RequestUserPermissionResult.DENY)
                else ->
                    onResult.invoke(RequestUserPermissionResult.DENY_WITH_NEVER_ASK_AGAIN)
            }
        }.addTo(compositeDisposable)
    }

    protected fun alertNetworkConnectionErrorDialog() {
        activity?.supportFragmentManager?.let {
            AlertMessageDialog.newInstance(
                title = getString(R.string.hello),
                message = getString(R.string.alert_message_network_connection),
                buttonText = getString(R.string.dismiss)
            ).show(it, AlertMessageDialog.TAG)
        }
    }
}
