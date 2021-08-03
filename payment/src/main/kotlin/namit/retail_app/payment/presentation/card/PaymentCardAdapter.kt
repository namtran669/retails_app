package namit.retail_app.payment.presentation.card

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.payment.R
import namit.retail_app.payment.data.entity.PaymentCard
import namit.retail_app.payment.enums.PaymentCardType
import kotlin.properties.Delegates

class PaymentCardAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<PaymentCard>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            PaymentCardType.REGISTER.value -> {
                val view = layoutInflater.inflate(R.layout.item_card_register, parent, false)
                RegisterCardViewHolder(view = view)
            }
            PaymentCardType.CREDIT.value -> {
                val view = layoutInflater.inflate(R.layout.item_card_credit, parent, false)
                CreditCardViewHolder(view = view)
            }
            else -> {
                val view = layoutInflater.inflate(R.layout.item_card_point, parent, false)
                PointCardViewHolder(view = view)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = items[position].type.value

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = items[position]
        when {
            data.type == PaymentCardType.REGISTER ->
                (holder as RegisterCardViewHolder).bind(paymentCard = data)
            data.type == PaymentCardType.CREDIT ->
                (holder as CreditCardViewHolder).bind(paymentCard = data)
            data.type == PaymentCardType.POINT ->
                (holder as PointCardViewHolder).bind(paymentCard = data)
            else ->
                throw IllegalArgumentException("Unknown ViewHolder: $holder")
        }
    }

    inner class RegisterCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            //TODO SETUP OnClick HERE
        }

        fun bind(paymentCard: PaymentCard) {
            //TODO BIND ALL VIEW HERE
        }
    }

    inner class CreditCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            //TODO SETUP OnClick HERE
        }

        fun bind(paymentCard: PaymentCard) {
            //TODO BIND ALL VIEW HERE
        }
    }

    inner class PointCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            //TODO SETUP OnClick HERE
        }

        fun bind(paymentCard: PaymentCard) {
            //TODO BIND ALL VIEW HERE
        }
    }
}