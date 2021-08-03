package namit.retail_app.payment.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.payment.R
import namit.retail_app.payment.data.PaymentMethodTypeModel
import namit.retail_app.core.enums.PaymentType
import kotlinx.android.synthetic.main.item_payment_method.view.*
import kotlin.properties.Delegates

class PaymentMethodTypeListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<PaymentMethodTypeModel>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var onItemClick: (paymentMethodType: PaymentMethodTypeModel) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_payment_method_type, parent, false)
        return PaymentMethodTypeViewHolder(view = view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PaymentMethodTypeViewHolder).bind(paymentMethodType = items[position])
    }

    inner class PaymentMethodTypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick.invoke(items[adapterPosition])
                }
            }
        }

        fun bind(paymentMethodType: PaymentMethodTypeModel) {
            itemView.apply {
                if (paymentMethodType.type == PaymentType.CREDIT_CARD) {
                    titleTextView.text = context.getString(R.string.credit_debit_card)
                } else {
                    titleTextView.text = paymentMethodType.title
                }

                paymentImageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        when (paymentMethodType.type) {
                            PaymentType.TRUE_MONEY -> {
                                R.drawable.ic_payment_true_money
                            }
                            else -> {
                                R.drawable.ic_payment
                            }
                        }
                    )
                )
            }
        }
    }
}