package namit.retail_app.grocery.presentation.category_detail

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.navigation.CartNavigator
import namit.retail_app.core.navigation.CoreNavigator
import namit.retail_app.core.presentation.adapter.ProductAdapter
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.core.presentation.cart.CartViewModel
import namit.retail_app.core.presentation.product_detail.ProductDetailDialog
import namit.retail_app.core.presentation.viewmodel.CartFloatButtonViewModel
import namit.retail_app.core.presentation.widget.EndlessProductVerticalListListener
import namit.retail_app.core.utils.getWidthScreenSize
import namit.retail_app.grocery.R
import kotlinx.android.synthetic.main.fragment_grocery_category_detail.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ObsoleteCoroutinesApi
class GroceryCategoryDetailFragment : BaseFragment() {
    companion object {
        const val TAG = "GroceryCategoryDetailFragment"

        private const val ARG_SELECTED_CATEGORY = "ARG_SELECTED_CATEGORY"
        private const val ARG_MERCHANT = "ARG_MERCHANT"

        fun getNewInstance(
            selectedCategory: CategoryItem,
            merchantData: MerchantInfoItem
        ): GroceryCategoryDetailFragment {
            val fragment = GroceryCategoryDetailFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_SELECTED_CATEGORY, selectedCategory)
                putParcelable(ARG_MERCHANT, merchantData)
            }
            return fragment
        }
    }

    private val viewModel: GroceryCategoryDetailViewModel by viewModel(parameters = {
        parametersOf(
            arguments?.getParcelable(ARG_SELECTED_CATEGORY),
            arguments?.getParcelable(ARG_MERCHANT)
        )
    })
    private val floatCartViewModel: CartFloatButtonViewModel by viewModel()
    private val cartViewModel: CartViewModel by viewModel()

    private val cartNavigator: CartNavigator by inject()
    private val coreNavigator: CoreNavigator by inject()
    private lateinit var productsAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_grocery_category_detail, null)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindViewModel()

        viewModel.loadProductList()
        floatCartViewModel.loadCartInfoFirstTime()
    }

    private fun initView() {
        searchView.apply {
            setHintContent(resources.getString(R.string.search_hint_general))
            enableSearch(enable = false)
            setOnClickListener {
                context?.let {
                    startActivity(
                        coreNavigator.openSearchActivity(
                            context = it,
                            merchantInfoItem = arguments!!.getParcelable(ARG_MERCHANT)!!
                        )
                    )
                }
            }
        }

        initListProductView()

        //Cart Button
        floatCartButton.setOnClickListener {
            activity?.run {
                val intent = cartNavigator.getCartActivity(this)
                startActivity(intent)
            }
        }

        categoryToolbarView.onBackPress = {
            (activity as BaseActivity).onBackPressed()
        }
    }

    private fun initListProductView() {
        productsAdapter = ProductAdapter().apply {
            val itemWidthScaleTypedValue = TypedValue()
            resources.getValue(
                R.dimen.itemProductVerticalWidthScale,
                itemWidthScaleTypedValue,
                true
            )
            val itemHeightScaleTypedValue = TypedValue()
            resources.getValue(
                R.dimen.itemProductVerticalHeightScale,
                itemHeightScaleTypedValue,
                true
            )

            val itemWidthSize =
                (getWidthScreenSize(context = context!!) * itemWidthScaleTypedValue.float).toInt()
            itemWidth = itemWidthSize
            itemHeight = (itemWidthSize * itemHeightScaleTypedValue.float).toInt()

            onSelectProduct = {
                viewModel.openProductDetailDialog(it)
            }

            addOneMore = { product, position ->
                cartViewModel.addOneProduct(product, position)
            }

            reduceOne = { product, position ->
                cartViewModel.reduceOneProduct(product, position)
            }
        }

        val productLayoutManager = GridLayoutManager(context, 2)
        productsRecycleView.apply {
            adapter = productsAdapter
            isNestedScrollingEnabled = false
            layoutManager = productLayoutManager
        }

        groceryCategoryDetailScrollView.setOnScrollChangeListener(object :
            EndlessProductVerticalListListener(productLayoutManager) {
            override fun onGoToBottomList() {
                viewModel.loadProductList()
            }
        })
    }

    private fun bindViewModel() {
        viewModel.productList.observe(viewLifecycleOwner, Observer {
            productsAdapter.items = it!!.toMutableList()
        })

        viewModel.showHaveNoMoreProduct.observe(viewLifecycleOwner, Observer {
            if (it) {
                bottomMessageTextView.visibility = View.VISIBLE
                bottomMessageTextView.text = resources.getString(R.string.no_more_data)
            } else {
                bottomMessageTextView.visibility = View.GONE
            }
        })

        viewModel.currentCategory.observe(viewLifecycleOwner, Observer { category ->
            categoryToolbarView.setScreenTitle(
                category.name
            )

            category.iconUrl?.let { categoryToolbarView.setToolbarIcon(it) }
        })

        viewModel.openProductDetail.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let { fragmentManager ->
                coreNavigator.getProductDetailDialog(product = it.first, merchantData = it.second)
                    .apply {
                        onAddProductToCartSuccess = {
                            floatCartViewModel.refreshCartInfo()
                        }
                    }.show(fragmentManager, ProductDetailDialog.TAG)
            }
        })

        //Cart Handling
        floatCartViewModel.cartInfo.observe(viewLifecycleOwner, Observer {
            floatCartButton.setCartValue(it.totalPrice, it.amount)
        })

        floatCartViewModel.isCartLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                floatCartButton.showSkeleton()
            } else {
                floatCartButton.hideSkeleton()
            }
        })

        cartViewModel.addProductSuccess.observe(viewLifecycleOwner, Observer {
            if (it.first) {
                viewModel.addProduct(it.second)
                floatCartViewModel.refreshCartInfo()
            }
        })

        cartViewModel.reduceOneProductSuccess.observe(viewLifecycleOwner, Observer {
            if (it.first) {
                viewModel.reduceProductInCart(it.second)
                floatCartViewModel.refreshCartInfo()
            }
        })

        cartViewModel.deleteProductInCartSuccess.observe(viewLifecycleOwner, Observer {
            if (it.first) {
                viewModel.removeProductInCart(it.second)
                floatCartViewModel.refreshCartInfo()
            }
        })
    }
}