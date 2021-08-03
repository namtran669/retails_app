package namit.retail_app.core.presentation.food_detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.R
import namit.retail_app.core.data.entity.OptionGroup
import namit.retail_app.core.data.entity.OptionPick
import namit.retail_app.core.data.entity.OptionSelection
import namit.retail_app.core.data.entity.OptionType
import kotlinx.android.synthetic.main.item_option_header.view.*
import kotlin.properties.Delegates


class ProductOptionGroupAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<OptionGroup>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var showWarning: (String) -> Unit = {}
    var optionSelected: (Double) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_option_header, parent, false)
        return OptionListHolder(view = view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as OptionListHolder).bind(group = items[position])


    inner class OptionListHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val optionAdapter = ProductOptionDetailsAdapter()

        @Suppress("DEPRECATION")
        fun bind(group: OptionGroup) {
            itemView.apply {
                groupTitleTextView.text = group.name
                var pickTitle = ""

                when (group.type) {
                    OptionType.OPTIONAL -> if (group.maxLimit != null) {
                        pickTitle =
                            context.getString(R.string.pick).plus(group.maxLimit?.toString())
                                .plus(resources.getString(R.string.optional_title))
                    } else {
                        pickTitle = resources.getString(R.string.optional)
                    }

                    OptionType.REQUIRED -> if (group.maxLimit != null) {
                        pickTitle =
                            context.getString(R.string.pick).plus(group.maxLimit?.toString())
                    }
                }
                pickTitleTextView.text = pickTitle

                optionAdapter.apply {
                    items = group.options
                    optionType = group.type ?: OptionType.OPTIONAL
                    selectType = group.selection ?: OptionSelection.MULTIPLE
                    sizeSelection = group.maxLimit ?: 1
                    showSnackbarWarning = showWarning
                    onOptionSelected = optionSelected
                }

                listOptionRecyclerView.apply {
                    adapter = optionAdapter
                    isNestedScrollingEnabled = false
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(context)
                }


            }
        }
    }

    fun getSelectedOption(): List<OptionGroup> {
        val selected = mutableListOf<OptionGroup>()
        items.forEach {
            val group = it.copy()
            val listPick = mutableListOf<OptionPick>()
            it.options.forEach {
                if (it.isSelected) {
                    listPick.add(it)
                }
            }

            if (listPick.isNotEmpty()) {
                group.options = listPick
                selected.add(group)
            }
        }

        return selected
    }

    fun validateSelectedOption(context: Context): Boolean {
        for (group in items) {
            val pickNumber = countSelectOption(group.options)
            if (pickNumber == 0 && group.type == OptionType.REQUIRED) {
                showWarning.invoke(
                    context.resources.getString(
                        R.string.option_require_select_msg,
                        group.name
                    )
                )
                return false
            }
            group.minLimit?.let {
                if (pickNumber < it && pickNumber != 0) {
                    showWarning.invoke(
                        context.resources.getString(
                            R.string.option_min_select_has_name_msg,
                            it, group.name
                        )
                    )
                    return false
                }
            }
            group.maxLimit?.let {
                if (pickNumber > it) {
                    showWarning.invoke(
                        context.resources.getString(
                            R.string.option_max_select_has_name_msg,
                            it, group.name
                        )
                    )
                    return false
                }
            }
        }
        return true
    }

    private fun countSelectOption(listOption: List<OptionPick>): Int {
        var pickNumber = 0
        listOption.forEach { option ->
            if (option.isSelected) {
                pickNumber++
            }
        }
        return pickNumber
    }
}