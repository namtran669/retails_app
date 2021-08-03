package namit.retail_app.payment.data

import android.os.Parcelable
import namit.retail_app.core.enums.PaymentType
import kotlinx.android.parcel.Parcelize

@Parcelize
class PaymentMethodTypeModel(
    var title: String,
    var type: PaymentType
): Parcelable