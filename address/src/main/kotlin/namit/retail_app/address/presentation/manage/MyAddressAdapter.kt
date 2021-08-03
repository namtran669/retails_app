package namit.retail_app.address.presentation.manage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.address.R
import namit.retail_app.core.data.entity.AddressListType
import namit.retail_app.core.data.entity.AddressModel
import io.sulek.ssml.OnSwipeListener
import kotlinx.android.synthetic.main.item_my_other_address.view.*
import kotlin.properties.Delegates

class MyAddressAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                } else {
                    onSelectItem.invoke(adapterPosition)
                }
            }

            itemView.binImageView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && items[adapterPosition].isSwiped) {
                    deleteItem(items[adapterPosition])
                } else {
                    onSelectItem.invoke(adapterPosition)
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
                    }
                }
            })
        }

        private fun deleteItem(address: AddressModel) {
            onDeleteItem.invoke(address)
        }

        fun bind(data: AddressModel) {
            itemView.apply {
                when {
                    !data.name.isNullOrBlank() -> {
                        when (data.type) {
                            AddressListType.HOME -> {
                                addressTitleTextView.text =
                                    context.getString(R.string.home)
                            }
                            AddressListType.WORK -> {
                                addressTitleTextView.text =
                                    context.getString(R.string.work)
                            }
                            else -> {
                                addressTitleTextView.text = data.name
                            }
                        }
                    }
                    !data.landMark.isNullOrBlank() -> {
                        addressTitleTextView.text = data.landMark
                    }
                    !data.address.isNullOrBlank() -> {
                        addressTitleTextView.text = data.address
                    }
                    else -> {
                        addressTitleTextView.text = context.getString(R.string.selected_location)
                    }
                }

                if (!data.address.isNullOrBlank()) {
                    fullAddressTextView.text = data.address
                } else {
                    fullAddressTextView.text = context.getString(R.string.selected_location)
                }

                when (data.type) {
                    AddressListType.HOME -> iconAddressImageView.setImageResource(
                        if (data.isSelected) R.drawable.ic_home_select
                        else R.drawable.ic_home
                    )

                    AddressListType.WORK -> iconAddressImageView.setImageResource(
                        if (data.isSelected) R.drawable.ic_work_location_blue
                        else R.drawable.ic_work_location_grey
                    )

                    else -> iconAddressImageView.setImageResource(
                        if (data.isSelected) R.drawable.ic_others_location_blue
                        else R.drawable.ic_others_location_grey
                    )
                }

                defaultAddressTextView.visibility = if (data.isDefault) View.VISIBLE else View.GONE

                swipeContainer.apply(data.isSwiped)
            }

        }
    }

}