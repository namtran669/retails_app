package namit.retail_app.grocery.presentation.warning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import namit.retail_app.core.presentation.base.BaseFullScreenDialog
import namit.retail_app.grocery.R
import kotlinx.android.synthetic.main.dialog_grocery_warning.*

class GroceryWarningDialog : BaseFullScreenDialog(), View.OnClickListener {

    companion object {
        const val TAG = "GroceryWarningDialog"
        fun newInstance(): GroceryWarningDialog = GroceryWarningDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.dialog_grocery_warning, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        //todo hard code Store Name
        goToStoreButton.text = resources.getString(R.string.go_to_store_warning, "Makro")

        goToStoreClickView.setOnClickListener(this)
        showAllProductClickView.setOnClickListener(this)

        with(itemProductHorizontalView) {
            setProductName("Banana")
            setActutalPrice(32.98f)
            setDiscountPrice(27.00f)
            setDiscountPercent(-20)
            setProductImage("https://img1.exportersindia.com/product_images/bc-full/2018/12/6006660/fresh-banana-1543995016-4533906.jpeg")
        }


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.goToStoreClickView -> {
                Toast.makeText(this.context, "Clicked Go To Store", Toast.LENGTH_SHORT).show()
            }

            R.id.showAllProductClickView -> {
                Toast.makeText(this.context, "Clicked Show All Product", Toast.LENGTH_SHORT).show()
            }
            else -> return
        }
    }
}