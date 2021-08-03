package namit.retail_app.grocery.presentation.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.presentation.adapter.ProductAdapter
import namit.retail_app.grocery.R
import kotlinx.android.synthetic.main.item_shelf_product.view.*
import kotlin.properties.Delegates

class GroceryProductAdapter : RecyclerView.Adapter<GroceryProductAdapter.GroceryViewHolder>() {

    var items by Delegates.observable(listOf<CategoryItem>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var productItemWidth = 0
    var productItemHeight = 0
    var onClickSeeAll: (merchantId: String) -> Unit = {}
    var onSelectChildProduct: (product: ProductItem) -> Unit = {}

    var addProductToCart: ((Int, ProductItem, Int) -> Unit) = { _, _, _ -> }
    var addOneMoreProduct: ((Int, ProductItem, Int) -> Unit) = { _, _, _ -> }
    var reduceOneProduct: ((Int, ProductItem, Int) -> Unit) = { _, _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_shelf_product, parent, false)
        return GroceryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class GroceryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val productsAdapter = ProductAdapter().apply {
            this.itemWidth = productItemWidth
            this.itemHeight = productItemHeight
            onSelectProduct = {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onSelectChildProduct.invoke(it)
                }
            }

            addOneMore = { productItem, productPosition ->
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    run {
                        addProductToCart.invoke(adapterPosition, productItem, productPosition)
                    }
                }
            }

            reduceOne = { productItem, productPosition ->
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    run {
                        reduceOneProduct.invoke(adapterPosition, productItem, productPosition)
                    }
                }
            }
        }

        init {
            view.productsRecyclerView.adapter = productsAdapter
            view.seeAllTextView.setOnClickListener {
                onClickSeeAll.invoke(items[adapterPosition].merchantId)
            }
        }

        fun bind(grocery: CategoryItem) {
            setNameGrocery(grocery)
            grocery.productList?.let { setListProduct(it) }
            setDescription(itemView.resources.getString(R.string.weekly_selection_of_best_products) +" "+ grocery.merchantName)
        }

        @SuppressLint("SetTextI18n")
        private fun setNameGrocery(data: CategoryItem) {
            view.groceryNameTextView.text = data.name
        }

        private fun setDescription(des: String) {
            view.groceryDateTextView.text = des
        }

        private fun setListProduct(list: List<ProductItem>) {
            productsAdapter.items = list
        }
    }
}