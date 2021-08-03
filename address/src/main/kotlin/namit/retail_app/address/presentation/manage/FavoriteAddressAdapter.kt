package namit.retail_app.address.presentation.manage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.address.R
import namit.retail_app.core.data.entity.AddressListType
import namit.retail_app.core.data.entity.AddressModel
import io.sulek.ssml.OnSwipeListener
import kotlinx.android.synthetic.main.item_my_other_address.view.*
import kotlin.properties.Delegates

class HomeWorkAddressAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<AddressModel>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var onSelectItem: (index: Int) -> Unit = {}
    var onClickEdit: (address: AddressModel) -> Unit = {}
    var onDeleteItem: (address: AddressModel) -> Unit = {}
    var onSwipeItem: (index: Int, isExpanded: Boolean) -> Unit = { _: Int, _: Boolean -> }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_my_other_address, parent, false)
        return OtherAddressViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as OtherAddressViewHolder).bind(items[position])
    }

    fun clearSelectItem() {
        for ((index, value) in items.withIndex()) {
            if (value.isSelected) {
                value.isSelected = false
                notifyItemChanged(index)
                break
            }
        }
    }

    fun clearSwipeItem() {
        for ((index, value) in items.withIndex()) {
            if (value.isSwiped) {
                value.isSwiped = false
                notifyItemChanged(index)
                break
            }
        }
    }

    inner class OtherAddressViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onSelectItem(adapterPosition)
                }
            }

            itemView.backgroundContainer.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && items[adapterPosition].isSwiped) {
                    deleteItem(items[adapterPosition])
                }
            }

            itemView.binImageView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && items[adapterPosition].isSwiped) {
                    deleteItem(items[adapterPosition])
                }
            }

            itemView.iconEditImageView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onClickEdit(items[adapterPosition])
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

        private fun deleteItem(address: AddressModel) {
            onDeleteItem.invoke(address)
        }

        fun bind(data: AddressModel) {
            itemView.apply {
                //Enable for real item from api
                itemView.isEnabled = data.id > 0

                when {
                    !data.landMark.isNullOrBlank() -> {
                        fullAddressTextView.text = data.landMark
                    }
                    !data.address.isNullOrBlank() -> {
                        fullAddressTextView.text = data.address
                    }
                    else -> fullAddressTextView.text = ""
                }

                when (data.type) {
                    AddressListType.HOME -> {
                        addressTitleTextView.text = resources.getString(R.string.home)
                        toggleHomeOption(data.isSelected, iconAddressImageView)
                    }
                    AddressListType.WORK -> {
                        addressTitleTextView.text = resources.getString(R.string.work)
                        toggleWorkOption(data.isSelected, iconAddressImageView)
                    }
                    else -> {
                    }
                }

                swipeContainer.apply(data.isSwiped)
                backgroundContainer.apply {
                    isClickable = data.isSwiped
                    isFocusable = data.isSwiped
                    isFocusableInTouchMode = data.isSwiped
                }
            }

        }

        private fun toggleHomeOption(isOn: Boolean, iconImageView: ImageView) {
            val iconResId: Int = if (isOn) {
                R.drawable.ic_home_select
            } else {
                R.drawable.ic_home_grey
            }

            iconImageView.setImageResource(iconResId)
        }

        private fun toggleWorkOption(isOn: Boolean, iconImageView: ImageView) {
            val iconResId: Int = if (isOn) {
                R.drawable.ic_work_location_blue
            } else {
                R.drawable.ic_work_location_grey
            }

            iconImageView.setImageResource(iconResId)
        }

    }

}