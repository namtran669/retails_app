package namit.retail_app.payment.data

import android.os.Parcelable
import namit.retail_app.payment.enums.CardType
import kotlinx.android.parcel.Parcelize

@Parcelize
class CardModel(
    var name: String,
    var number: String,
    var expireDate: String,
    var cvvCode: String,
    var type: CardType = CardType.UNKNOWNS
) : Parcelable