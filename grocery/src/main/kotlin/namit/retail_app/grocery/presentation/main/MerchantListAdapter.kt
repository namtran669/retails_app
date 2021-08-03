package namit.retail_app.grocery.presentation.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.extension.loadImage
import namit.retail_app.grocery.R
import kotlinx.android.synthetic.main.item_merchant.view.*
import kotlin.properties.Delegates

class MerchantListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<MerchantInfoItem>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    private var actionListener: OnActionListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_merchant, parent, false)
        return MerchantNameViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MerchantNameViewHolder).bind(data = items[position])
    }

    fun setActionListener(action: OnActionListener) {
        this.actionListener = action
    }

    inner class MerchantNameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    actionListener?.onItemSelect(items[adapterPosition])
                }
            }
        }

        fun bind(data: MerchantInfoItem) {
            data.imageUrl?.let {
                itemView.merchantNameImageView.loadImage(imageUrl = it)
            }
        }
    }

    //todo change to base listener
    interface OnActionListener {
        fun onItemSelect(merchant: MerchantInfoItem)
    }

}