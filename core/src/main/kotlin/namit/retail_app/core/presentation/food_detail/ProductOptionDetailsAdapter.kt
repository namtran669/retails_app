package namit.retail_app.core.presentation.food_detail

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.R
import namit.retail_app.core.data.entity.OptionPick
import namit.retail_app.core.data.entity.OptionSelection
import namit.retail_app.core.data.entity.OptionType
import namit.retail_app.core.extension.formatCurrency
import kotlinx.android.synthetic.main.item_option_detail.view.*
import kotlin.properties.Delegates

class ProductOptionDetailsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<OptionPick>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var groupId = 0
    var optionType = OptionType.OPTIONAL
    var selectType = OptionSelection.SINGLE
    var sizeSelection = 1
    var showSnackbarWarning: (String) -> Unit = {}

    var onOptionSelected : (Double) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_option_detail, parent, false)
        return OptionExtraViewHolder(view = view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as OptionExtraViewHolder).bind(option = items[position])

    inner class OptionExtraViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val pick = items[adapterPosition]
                    if (!pick.isSelected) {
                        when (selectType) {
                            OptionSelection.SINGLE -> {
                                for (item in items) {
                                    if (item.isSelected) item.isSelected = false
                                }
                            }

                            OptionSelection.MULTIPLE -> {
                                var countSelected = 0
                                for (item in items) {
                                    if (item.isSelected) ++countSelected
                                    if (countSelected == sizeSelection) {
                                        showSnackbarWarning.invoke(
                                            itemView.resources.getString(
                                                R.string.option_max_select_msg,
                                                sizeSelection
                                            )
                                        )
                                        return@setOnClickListener
                                    }
                                }
                            }
                        }
                    }

                    pick.isSelected = !pick.isSelected
                    onOptionSelected.invoke(calculateOptionPrice())
                    notifyDataSetChanged()
                }
            }
        }

        fun calculateOptionPrice() : Double {
            var result = 0.0
            items.forEach {
                if(it.isSelected) {
                    result += it.price ?: 0.0
                }
            }

            return result
        }

        fun bind(option: OptionPick) {
            itemView.apply {
                optionContentTextView.text = option.name

                option.price?.let {
                    if(it != 0.0) {
                        optionPriceTextView.text = "+".plus(it.formatCurrency())
                    }
                }

                when (selectType) {
                    OptionSelection.SINGLE -> {
                        if (option.isSelected) {
                            optionSelectImageView.setImageResource(R.drawable.ic_checkbox_checked)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                optionContentTextView.setTextAppearance(R.style.P2_Trout_Medium)
                                optionPriceTextView.setTextAppearance(R.style.P2_DodgerBlue)
                            } else {
                                optionContentTextView.setTextAppearance(
                                    itemView.context,
                                    R.style.P2_Trout_Medium
                                )
                                optionPriceTextView.setTextAppearance(
                                    itemView.context,
                                    R.style.P2_DodgerBlue
                                )
                            }
                            setBackgroundResource(R.drawable.bg_item_food_option_selected)
                        } else {
                            optionSelectImageView.setImageResource(R.drawable.ic_checkbox_tick_available)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                optionContentTextView.setTextAppearance(R.style.P2_Trout)
                                optionPriceTextView.setTextAppearance(R.style.P2_Trout60)
                            } else {
                                optionContentTextView.setTextAppearance(
                                    itemView.context,
                                    R.style.P2_Trout
                                )
                                optionPriceTextView.setTextAppearance(
                                    itemView.context,
                                    R.style.P2_Trout60
                                )
                            }
                            setBackgroundResource(R.drawable.bg_item_food_option_unselected)
                        }
                    }

                    OptionSelection.MULTIPLE -> {
                        if (option.isSelected) {
                            optionSelectImageView.setImageResource(R.drawable.ic_checkbox_tick_checked)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                optionContentTextView.setTextAppearance(R.style.P2_Trout_Medium)
                                optionPriceTextView.setTextAppearance(R.style.P2_DodgerBlue)
                            } else {
                                @Suppress("DEPRECATION")
                                optionContentTextView.setTextAppearance(
                                    itemView.context,
                                    R.style.P2_Trout_Medium
                                )
                                @Suppress("DEPRECATION")
                                optionPriceTextView.setTextAppearance(
                                    itemView.context,
                                    R.style.P2_DodgerBlue
                                )
                            }
                            setBackgroundResource(R.drawable.bg_item_food_option_selected)
                        } else {
                            optionSelectImageView.setImageResource(R.drawable.ic_checkbox_tick_available)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                optionContentTextView.setTextAppearance(R.style.P2_Trout)
                                optionPriceTextView.setTextAppearance(R.style.P2_Trout60)
                            } else {
                                optionContentTextView.setTextAppearance(
                                    itemView.context,
                                    R.style.P2_Trout
                                )
                                optionPriceTextView.setTextAppearance(
                                    itemView.context,
                                    R.style.P2_Trout60
                                )
                            }
                            setBackgroundResource(R.drawable.bg_item_food_option_unselected)
                        }
                    }
                }
            }
        }
    }
}