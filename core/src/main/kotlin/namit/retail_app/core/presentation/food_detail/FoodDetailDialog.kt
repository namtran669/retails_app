package namit.retail_app.core.presentation.food_detail

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import namit.retail_app.core.R
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.extension.formatCurrency
import namit.retail_app.core.extension.loadImage
import namit.retail_app.core.extension.toThaiCurrency
import namit.retail_app.core.navigation.GroceryNavigator
import namit.retail_app.core.presentation.base.BaseFullScreenDialog
import namit.retail_app.core.presentation.product_detail.ProductDetailViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_food_detail.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FoodDetailDialog : BaseFullScreenDialog() {

    companion object {
        const val TAG = "FoodDetailDialog"
        const val ARG_PRODUCT_DATA = "ARG_PRODUCT_DATA"
        const val ARG_MERCHANT_DATA = "ARG_MERCHANT_DATA"

        fun newInstance(product: ProductItem, merchantData: MerchantInfoItem): FoodDetailDialog {
            val fragment = FoodDetailDialog()
            val args = Bundle()
            args.putParcelable(ARG_PRODUCT_DATA, product)
            args.putParcelable(ARG_MERCHANT_DATA, merchantData)
            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel: ProductDetailViewModel by viewModel(parameters = {
        parametersOf(
            arguments?.get(ARG_PRODUCT_DATA) as ProductItem,
            arguments?.get(ARG_MERCHANT_DATA) as MerchantInfoItem
        )
    })

    private val optionProductAdapter = ProductOptionGroupAdapter()
    private val groceryNavigator: GroceryNavigator by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.dialog_food_detail, null)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewModel()
    }

    private fun initView() {
        val bottomSheetBehavior = BottomSheetBehavior.from(productContentBottomSheetLayout)
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                //do nothing
            }

            override fun onStateChanged(bottomView: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    productContentBottomSheetLayout.setBackgroundColor(Color.WHITE)
                    productContentWrapperCardView.radius = 0f
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    productContentBottomSheetLayout.setBackgroundResource(R.drawable.shape_white_top_left_right_30_radius)
                    productContentWrapperCardView.radius =
                        resources.getDimension(R.dimen.foodDetailCornerRadius)
                }
            }

        })

        val scrollViewRect = Rect()
        productDetailScrollView.apply {
            getHitRect(scrollViewRect)
            setOnScrollChangeListener { _: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->
                if (productImageView != null
                    && productImageView.getLocalVisibleRect(scrollViewRect)
                ) {
                    viewModel.setShowTitleProduct(false)
                } else {
                    viewModel.setShowTitleProduct(true)
                }
            }
        }

        closeProductImageView.setOnClickListener {
            dismiss()
        }

        productOrderView.setAddToCartAction {
            context?.apply {
                groceryNavigator.getGroceryWarningDialog()
                    .show(activity?.supportFragmentManager, "GroceryWarningDialog")
            }
        }

        optionProductView.setOnClickListener {
            viewModel.showCustomizeOption()
        }

        optionProductView.setAdapterOption(
            optionProductAdapter
        )

        optionProductView.apply {
            setAdapterOption(optionProductAdapter)
            setOnClickListener { viewModel.showCustomizeOption() }
            setCollapseDetailView()
        }

        productOrderView.apply {
            onAddItem = {
                viewModel.addOneItemProduct()
            }

            onReduceItem = {
                viewModel.reduceOneItemProduct()
            }
        }
    }

    private fun bindViewModel() {

        viewModel.productDataLiveData.observe(viewLifecycleOwner, Observer { product ->
            productImageView.loadImage(product.thumbnailUrl)
            titleProductTextView.text = product.name
            productNameTextView.text = product.name

            product.retailPriceWithTax?.let {
                productPriceTextView.text =
                    it.formatCurrency().toThaiCurrency()
            }

            product.optionGroup.let {
                optionProductAdapter.items = it
                optionProductAdapter.showWarning = { msg ->
                    showSnackBar(msg, Snackbar.LENGTH_LONG)
                }
            }

            product.name.let {
                productDetailTextView.text = it
            }
        })

        viewModel.shouldShowProductTitle.observe(viewLifecycleOwner, Observer {
            if (it) {
                titleProductTextView.apply {
                    text = viewModel.getTitleProduct()
                    visibility = View.VISIBLE
                }

                titleProductCardView.apply {
                    cardElevation = context.resources.getDimension(R.dimen.toolbarProductElevation)
                    setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                }
                closeProductImageView.setImageResource(R.drawable.ic_close_black)

            } else {
                titleProductTextView.visibility = View.GONE
                titleProductCardView.apply {
                    cardElevation =
                        context.resources.getDimension(R.dimen.toolbarProductNoElevation)
                    setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            android.R.color.transparent
                        )
                    )
                }
                closeProductImageView.setImageResource(R.drawable.ic_close_white)
            }
        })

        viewModel.showCustomizeOptionView.observe(viewLifecycleOwner, Observer {
            if (it) {
                optionProductView.setExpandDetailView()
            } else {
                optionProductView.setCollapseDetailView()
            }
        })

        viewModel.productQuantity.observe(viewLifecycleOwner, Observer {
            productOrderView.setQuantityNumber(it)
        })

        viewModel.toggleMinusButton.observe(viewLifecycleOwner, Observer {
            productOrderView.toggleMinusButton(it)
        })
    }
}