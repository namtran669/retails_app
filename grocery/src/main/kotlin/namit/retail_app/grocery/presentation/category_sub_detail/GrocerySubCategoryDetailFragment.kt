package namit.retail_app.grocery.presentation.category_sub_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.navigation.CartNavigator
import namit.retail_app.core.navigation.CoreNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.core.presentation.cart.CartViewModel
import namit.retail_app.core.presentation.product_detail.ProductDetailDialog
import namit.retail_app.core.presentation.viewmodel.CartFloatButtonViewModel
import namit.retail_app.core.presentation.widget.EndlessProductVerticalListListener
import namit.retail_app.grocery.R
import kotlinx.android.synthetic.main.fragment_grocery_sub_category_detail.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ObsoleteCoroutinesApi
class GrocerySubCategoryDetailFragment : BaseFragment() {
    companion object {
        const val TAG = "GrocerySubCategoryDetailFragment"
        private const val ARG_MERCHANT_INFO = "ARG_MERCHANT_INFO"
        private const val ARG_ROOT_CATEGORY = "ARG_ROOT_CATEGORY"
        private const val ARG_SELECTED_CATEGORY = "ARG_SELECTED_CATEGORY"

        fun getNewInstance(
            merchantInfoItem: MerchantInfoItem,
            parentCategory: CategoryItem,
            selectedCategory: CategoryItem
        ): GrocerySubCategoryDetailFragment {
            val fragment = GrocerySubCategoryDetailFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_MERCHANT_INFO, merchantInfoItem)
                putParcelable(ARG_ROOT_CATEGORY, parentCategory)
                putParcelable(ARG_SELECTED_CATEGORY, selectedCategory)
            }
            return fragment
        }
    }

    private val viewModel: GrocerySubCategoryDetailViewModel by viewModel(parameters = {
        parametersOf(
            arguments?.getParcelable(ARG_ROOT_CATEGORY),
            arguments?.getParcelable(ARG_SELECTED_CATEGORY)
        )
    })
    private val cartViewModel: CartViewModel by viewModel()
    private val floatCartViewModel: CartFloatButtonViewModel by viewModel()

    private val coreNavigator: CoreNavigator by inject()
    private val cartNavigator: CartNavigator by inject()

    private lateinit var productAdapter: SubCategoryProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context)
            .inflate(R.layout.fragment_grocery_sub_category_detail, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindViewModel()
        bindCartViewModel()
        bindFloatCartViewModel()

        viewModel.loadProductList()
        floatCartViewModel.loadCartInfoFirstTime()
    }

    private fun initView() {
        categoryToolbarView?.onBackPress = {
            (activity as? BaseActivity)?.onBackPressed()
        }

        val productLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        productAdapter = SubCategoryProductAdapter().apply {
            onSelectProduct = { product ->
                activity?.supportFragmentManager?.let { fragmentManager ->
                    coreNavigator.getProductDetailDialog(
                        product = product,
                        merchantData = arguments!!.getParcelable(ARG_MERCHANT_INFO)!!
                    ).apply {
                        onAddProductToCartSuccess = {
                            floatCartViewModel.refreshCartInfo()
                        }
                    }.show(fragmentManager, ProductDetailDialog.TAG)
                }
            }

            addOneMore = { product, position ->
                cartViewModel.addOneProduct(product, position)
            }

            reduceOne = { product, position ->
                cartViewModel.reduceOneProduct(product, position)
            }
        }
        productRecycleView?.apply {
            isNestedScrollingEnabled = false
            adapter = productAdapter
            layoutManager = productLayoutManager
        }

        productNestedScrollView.setOnScrollChangeListener(object :
            EndlessProductVerticalListListener(productLayoutManager) {
            override fun onGoToBottomList() {
                viewModel.loadProductList()
            }
        })

        floatCartButton.setOnClickListener {
            activity?.let {
                startActivity(cartNavigator.getCartActivity(it))
            }
        }
    }

    private fun bindViewModel() {
        viewModel.productList.observe(viewLifecycleOwner, Observer {
            productAdapter.items = it!!.toList()
        })

        viewModel.showHaveNoMoreProduct.observe(viewLifecycleOwner, Observer {
            if (it) {
                bottomMessageTextView.visibility = View.VISIBLE
                bottomMessageTextView.text = getString(R.string.no_more_data)
            } else {
                bottomMessageTextView.visibility = View.GONE
            }
        })

        viewModel.rootCategory.observe(viewLifecycleOwner, Observer {
            categoryToolbarView?.apply {
                setScreenTitle(it.name)
                setToolbarIcon(it.iconUrl ?: "")

                it.breadcrumbChildList?.let {
                    val renderNameList = mutableListOf<String>()
                    it.forEach { childCategory ->
                        renderNameList.add(
                            childCategory.name
                        )
                    }
                    handleBreadcrumbCategory(renderNameList)
                }
            }
        })
    }

    private fun bindFloatCartViewModel() {
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
    }

    private fun bindCartViewModel() {
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