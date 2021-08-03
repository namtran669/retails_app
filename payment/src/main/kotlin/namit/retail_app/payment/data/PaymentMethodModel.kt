package namit.retail_app.payment.data

import android.os.Parcelable
import namit.retail_app.payment.enums.CardType
import namit.retail_app.core.enums.PaymentType
import kotlinx.android.parcel.Parcelize

@Parcelize
class PaymentMethodModel(
    var id: Int,
    var title: String,
    var description: String? = null,
    var type: PaymentType,
    var cardType: CardType? = null,
    var paymentMethodId: Int = 1,
    var isSelected: Boolean = false,
    var isSwiped: Boolean = false,
    var isPrimary: Boolean = false
) : Parcelable