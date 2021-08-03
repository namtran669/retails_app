package namit.retail_app.core.presentation.base

import android.Manifest
import android.os.Bundle
import namit.retail_app.core.enums.RequestUserPermissionResult
import namit.retail_app.core.extension.addTo
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable

abstract class BasePermissionFragment: BaseFragment() {

    companion object {
        const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    }

    private lateinit var rxPermission: RxPermissions
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rxPermission = RxPermissions(this)
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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}