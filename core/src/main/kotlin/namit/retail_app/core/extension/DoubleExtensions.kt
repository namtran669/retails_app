package namit.retail_app.core.extension

import android.util.Log
import java.text.DecimalFormat

fun Double.formatCurrency(): String {
    var currency = ""
    try {
        val formatter = DecimalFormat("###,###.00")
        formatter.minimumIntegerDigits = 1
        currency = formatter.format(this)
        if(currency.contains(".00")){
            currency = currency.replace(".00","")
        }
    } catch (e: Exception) {
        Log.e("Double Ext", "can not calculate currency " + e.localizedMessage)
    }

    return currency
}

fun Double.convertSatangToBaht(): Double {
    return this.div(100)
}