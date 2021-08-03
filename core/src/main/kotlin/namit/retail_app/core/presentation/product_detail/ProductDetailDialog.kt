package namit.retail_app.core.presentation.product_detail

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import namit.retail_app.core.R
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.extension.formatCurrency
import namit.retail_app.core.extension.gone
import namit.retail_app.core.extension.toThaiCurrency
import namit.retail_app.core.extension.visible
import namit.retail_app.core.presentation.adapter.ImageViewPagerAdapter
import namit.retail_app.core.presentation.adapter.ProductAdapter
import namit.retail_app.core.presentation.base.BaseFullScreenDialog
import namit.retail_app.core.presentation.cart.CartViewModel
import namit.retail_app.core.presentation.food_detail.ProductOptionGroupAdapter
import namit.retail_app.core.presentation.viewmodel.CartFloatButtonViewModel
import namit.retail_app.core.presentation.widget.EndlessRecyclerViewScrollListener
import namit.retail_app.core.utils.getWidthScreenSize
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_product_detail.*
import kotlinx.android.synthetic.main.view_customize_option_product.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ObsoleteCoroutinesApi
class ProductDetailDialog : BaseFullScreenDialog() {

    companion object {
        const val TAG = "ProductDetailDialog"
        const val ARG_PRODUCT_DATA = "ARG_PRODUCT_DATA"
        const val ARG_MERCHANT_DATA = "ARG_MERCHANT_DATA"
        fun newInstance(product: ProductItem, merchantData: MerchantInfoItem): ProductDetailDialog {
            val fragment = ProductDetailDialog()
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

    private val cartViewModel: CartViewModel by viewModel()
    private val floatCartViewModel: CartFloatButtonViewModel by viewModel()
    private lateinit var relatedProductAdapter: ProductAdapter
    private lateinit var imageViewPagerAdapter: ImageViewPagerAdapter
    private val optionProductAdapter = ProductOptionGroupAdapter()
    var onAddProductToCartSuccess: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.dialog_product_detail, null)
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
        viewModel.getRelatedProductList()
        viewModel.checkMerchantType()
        viewModel.updateTotalPrice()
        viewModel.checkProductState()
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
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    productContentBottomSheetLayout.setBackgroundResource(R.drawable.shape_white_top_left_right_30_radius)
                }
            }
        })

        val scrollViewRect = Rect()
        productDetailScrollView.apply {
            getHitRect(scrollViewRect)
            setOnScrollChangeListener { _: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->
                if (productImageViewPager != null
                    && productImageViewPager.getLocalVisibleRect(scrollViewRect)
                ) {
                    titleProductTextView.visibility = View.GONE
                    titleProductCardView.elevation =
                        context.resources.getDimension(R.dimen.toolbarProductNoElevation)
                    titleProductCardView.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    viewModel.presentTitleBar()
                    titleProductCardView.elevation =
                        context.resources.getDimension(R.dimen.toolbarProductElevation)
                    titleProductCardView.setBackgroundColor(Color.WHITE)
                }
            }
        }

        closeProductImageView.setOnClickListener {
            dismiss()
        }

        imageViewPagerAdapter = ImageViewPagerAdapter(context = context!!)
        productImageViewPager.adapter = imageViewPagerAdapter
        productImageIndicator.setViewPager(productImageViewPager)
        productImageIndicator.fillColor = ContextCompat.getColor(context!!, R.color.dodgerBlue)
        productImageIndicator.pageColor = ContextCompat.getColor(context!!, R.color.ghost)
        productImageIndicator.strokeWidth = 0F

        val itemWidthScaleTypedValue = TypedValue()
        resources.getValue(R.dimen.itemProductHorizontalWidthScale, itemWidthScaleTypedValue, true)
        val itemHeightScaleTypedValue = TypedValue()
        resources.getValue(
            R.dimen.itemProductHorizontalHeightScale,
            itemHeightScaleTypedValue,
            true
        )
        relatedProductAdapter = ProductAdapter().apply {
            val itemWidthSize = (getWidthScreenSize(context = context!!) *
                    itemWidthScaleTypedValue.float).toInt()
            itemWidth = itemWidthSize
            itemHeight = (itemWidthSize * itemHeightScaleTypedValue.float).toInt()

            onSelectProduct = { product ->
                viewModel.reloadProduct(product)
            }

            addOneMore = { product, position ->
                cartViewModel.addOneProduct(product, position)
            }

            reduceOne = { product, position ->
                cartViewModel.reduceOneProduct(product, position)
            }
        }

        relatedProductRecyclerView.apply {
            adapter = relatedProductAdapter
            isNestedScrollingEnabled = false
            val manager = layoutManager as LinearLayoutManager
            addOnScrollListener(object : EndlessRecyclerViewScrollListener(manager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    viewModel.getRelatedProductList()
                }
            })
        }

        customizeLayout.setOnClickListener {
            viewModel.showCustomizeOption()
        }

        optionProductView.setOnClickListener {
            viewModel.showCustomizeOption()
        }

        optionProductView.setAdapterOption(
            optionProductAdapter
        )

        optionProductView.apply {
            optionProductAdapter.optionSelected = {
                viewModel.updateTotalPrice()
            }
            setAdapterOption(optionProductAdapter)
            setOnClickListener {
                viewModel.showCustomizeOption()
            }
            setCollapseDetailView()
        }

        productOrderView.apply {
            onAddItem = {
                viewModel.addOneItemProduct()
            }

            onReduceItem = {
                viewModel.reduceOneItemProduct()
            }

            onAddToCart = {
                if (optionProductAdapter.validateSelectedOption(context)) {
                    val product = viewModel.updateProductOption(
                        optionProductAdapter.getSelectedOption(),
                        optionProductView.getOptionNote()
                    )
                    cartViewModel.addMultiProduct(
                        productData = product,
                        quantity = it)
                }
            }
        }

        productImageViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                countOfProductImageTextView.text = "${position + 1}/${imageViewPagerAdapter.count}"
            }
        })
    }

    private fun bindViewModel() {
        viewModel.scrollToTop.observe(viewLifecycleOwner, Observer {
            productDetailScrollView.smoothScrollTo(0, 0)
        })

        viewModel.relatedProduct.observe(viewLifecycleOwner, Observer {
            relatedProductAdapter.items = it
            relatedProductTextView.visibility = View.VISIBLE
            relatedProductRecyclerView.visibility = View.VISIBLE
        })

        viewModel.showRelatedProduct.observe(viewLifecycleOwner, Observer { isShow ->
            if (isShow) {
                relatedProductTextView.visibility = View.VISIBLE
                relatedProductRecyclerView.visibility = View.VISIBLE
            } else {
                relatedProductTextView.visibility = View.GONE
                relatedProductRecyclerView.visibility = View.GONE
            }
        })

        viewModel.renderProductImage.observe(viewLifecycleOwner, Observer {
            imageViewPagerAdapter.items = it
            productImageViewPager.adapter = imageViewPagerAdapter
            productImageIndicator.setViewPager(productImageViewPager)
            countOfProductImageTextView.text = "${productImageViewPager.currentItem + 1}/${it.size}"
        })

        viewModel.productDataLiveData.observe(viewLifecycleOwner, Observer { product ->

            titleProductTextView.text = product.name
            productNameTextView.text = product.name

            product.country?.let {
                productProducerTextView.text = getString(R.string.produce_of)
                    .plus(it)
            }

            product.retailPriceWithTax?.let {
                productPriceTextView.text =
                    it.formatCurrency().toThaiCurrency()
            }

            product.packageItem?.let {
                priceNoteTextView.text = "$it ${product.packageUnit ?: ""}"
            }

            product.weight?.let {
                productSizeTextView.text = it.toString()
                    .plus(product.weightUnit ?: "")
            }

            productLifeDetailTextView.text = product.description

            if (product.optionGroup.isNullOrEmpty()) {
                optionProductView.visibility = View.GONE
            } else {
                optionProductView.visibility = View.VISIBLE
                product.optionGroup.let {
                    optionProductAdapter.items = it
                    optionProductAdapter.showWarning = { msg ->
                        showSnackBar(msg, Snackbar.LENGTH_LONG)
                        optionProductView.setExpandDetailView()
                        Handler().postDelayed({
                            productDetailScrollView?.smoothScrollTo(0, optionProductView.bottom)
                        }, 200)
                    }
                }
            }

            if (product.isAlcohol) {
                alcoholWarningATextView.visible()
            }

            optionProductView.setOptionNote(product.note)
        })

        viewModel.merchantInfo.observe(viewLifecycleOwner, Observer {
            storeOfProductTextView.text = it.title
        })

        viewModel.showTitleBar.observe(viewLifecycleOwner, Observer {
            titleProductTextView.apply {
                text = it
                visibility = View.VISIBLE
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

        viewModel.isOutOfStockState.observe(viewLifecycleOwner, Observer {
            if (it) {
                outStockTextView.visible()
                discountPercentTextView.background =
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.bg_text_discount_percent_disable
                    )
                productOrderView.gone()
            } else {
                outStockTextView.gone()
                ContextCompat.getDrawable(context!!, R.drawable.bg_text_discount_percent)
                productOrderView.visible()
            }
        })

        cartViewModel.addMultiProductSuccess.observe(viewLifecycleOwner, Observer {
            if (it.first) {
                onAddProductToCartSuccess.invoke()
                dismiss()
            }
        })

        cartViewModel.addProductSuccess.observe(viewLifecycleOwner, Observer {
            if (it.first) {
                floatCartViewModel.refreshCartInfo()
                viewModel.addOneRelatedProduct(it.second)
            }
        })

        cartViewModel.reduceOneProductSuccess.observe(viewLifecycleOwner, Observer {
            if (it.first) {
                floatCartViewModel.refreshCartInfo()
                viewModel.reduceOneRelatedProduct(it.second)
            }
        })

        cartViewModel.deleteProductInCartSuccess.observe(viewLifecycleOwner, Observer {
            if (it.first) {
                floatCartViewModel.refreshCartInfo()
                viewModel.deleteRelatedProduct(it.second)
            }
        })
    }
}