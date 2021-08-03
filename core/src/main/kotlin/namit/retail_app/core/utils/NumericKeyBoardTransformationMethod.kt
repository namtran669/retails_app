package namit.retail_app.core.utils

import android.text.method.PasswordTransformationMethod
import android.view.View


class NumericKeyBoardTransformationMethod : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View?): CharSequence {
        return source
    }
}