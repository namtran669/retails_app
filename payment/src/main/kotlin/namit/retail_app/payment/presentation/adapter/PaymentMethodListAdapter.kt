package namit.retail_app.payment.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.extension.gone
import namit.retail_app.core.extension.visible
import namit.retail_app.payment.R
import namit.retail_app.payment.data.PaymentMethodModel
import namit.retail_app.payment.enums.CardType
import namit.retail_app.core.enums.PaymentType
import io.sulek.ssml.OnSwipeListener
import kotlinx.android.synthetic.main.item_payment_method.view.*
import kotlin.properties.Delegates

class PaymentMethodListAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<PaymentMethodModel>()) { _, _, _ ->
        notifyDataSetChanged()
    }
    var onSelectItem: (index: Int) -> Unit = {}
    var onDeleteItem: (index: Int) -> Unit = {}
    var onSwipeItem: (index: Int, isExpanded: Boolean) -> Unit = { _: Int, _: Boolean -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_payment_method, parent, false)
        return PaymentMethodViewHolder(view = view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PaymentMethodViewHolder).bind(paymentMethod = items[position])
    }

    inner class PaymentMethodViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val paymentMethod = items[adapterPosition]
                    if (paymentMethod.isSwiped) {
                        paymentMethod.isSwiped = false
                        notifyDataSetChanged()
                        return@setOnClickListener
                    }
                    onSelectItem.invoke(adapterPosition)
                }
            }

            itemView.binImageView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && items[adapterPosition].isSwiped) {
                    deleteItem(adapterPosition)
                } else {
                    onSelectItem.invoke(adapterPosition)
                }
            }

            itemView.backgroundContainer.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && items[adapterPosition].isSwiped) {
                    deleteItem(adapterPosition)
                } else {
                    onSelectItem.invoke(adapterPosition)
                }
            }

            itemView.swipeContainer.setOnSwipeListener(object : OnSwipeListener {
                override fun onSwipe(isExpanded: Boolean) {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        onSwipeItem(adapterPosition, isExpanded)
                        itemView.backgroundContainer.apply {
                            isClickable = isExpanded
                            isFocusable = isExpanded
                            isFocusableInTouchMode = isExpanded
                        }
                    }
                }
            })
        }

        private fun deleteItem(position: Int) {
            onDeleteItem.invoke(position)
        }

        fun bind(paymentMethod: PaymentMethodModel) {
            itemView.apply {
                //Disable swipe of CASH and TMN blank
                backgroundContainer.layoutParams.width =
                    if (paymentMethod.id < 0) {
                        0
                    } else {
                        resources.getDimensionPixelSize(R.dimen.removePaymentButtonWidth)
                    }

                primaryTextView.visibility =
                    if (paymentMethod.isPrimary) View.VISIBLE else View.GONE

                if (paymentMethod.type == PaymentType.CASH) {
                    titleTextView.text = context.getString(R.string.cash_on_delivery)
                } else {
                    titleTextView.text = paymentMethod.title
                }

                descTextView.apply {
                    if (paymentMethod.description.isNullOrBlank().not()) {
                        descTextView.visible()
                        descTextView.text = paymentMethod.description
                    } else {
                        descTextView.gone()
                    }
                }
                checkImageView.visibility =
                    if (paymentMethod.isSelected) View.VISIBLE else View.INVISIBLE
                when (paymentMethod.type) {
                    PaymentType.TRUE_MONEY -> {
                        R.drawable.ic_payment_true_money
                    }
                    PaymentType.CASH -> {
                        R.drawable.ic_payment_cash
                    }
                    else -> {
                        when (paymentMethod.cardType) {
                            CardType.VISA -> {
                                R.drawable.ic_visa_credit_card
                            }
                            CardType.MASTER_CARD -> {
                                R.drawable.ic_master_credit_card
                            }
                            CardType.JCB -> {
                                R.drawable.ic_jcb_credit_card
                            }
                            else -> {
                                null
                            }
                        }
                    }
                }?.let {
                    paymentImageView.setImageDrawable(
                        ContextCompat.getDrawable(context, it)
                    )
                }


                swipeContainer.apply(paymentMethod.isSwiped)
                backgroundContainer.apply {
                    isClickable = paymentMethod.isSwiped
                    isFocusable = paymentMethod.isSwiped
                    isFocusableInTouchMode = paymentMethod.isSwiped
                }
            }
        }
    }
}