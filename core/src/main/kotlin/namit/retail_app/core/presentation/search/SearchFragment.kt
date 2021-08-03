package namit.retail_app.core.presentation.search

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import namit.retail_app.core.R
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.extension.gone
import namit.retail_app.core.extension.visible
import namit.retail_app.core.navigation.CartNavigator
import namit.retail_app.core.navigation.CoreNavigator
import namit.retail_app.core.presentation.adapter.ProductHorizontalAdapter
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.core.presentation.product_detail.ProductDetailDialog
import namit.retail_app.core.presentation.viewmodel.CartFloatButtonViewModel
import namit.retail_app.core.presentation.widget.EndlessProductVerticalListListener
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ObsoleteCoroutinesApi
class SearchFragment : BaseFragment() {

    companion object {
        const val TAG = "SearchFragment"
        private const val ARG_MERCHANT_INFO = "ARG_MERCHANT_INFO"
        fun getNewInstance(merchantInfoItem: MerchantInfoItem): SearchFragment {
            val bundle = Bundle()
            bundle.putParcelable(ARG_MERCHANT_INFO, merchantInfoItem)
            return SearchFragment().apply {
                arguments = bundle
            }
        }
    }

    private val searchViewModel: SearchViewModel by viewModel(parameters = {
        parametersOf(arguments?.getParcelable(ARG_MERCHANT_INFO))
    })
    private val floatCartViewModel: CartFloatButtonViewModel by viewModel()

    private val coreNavigator: CoreNavigator by inject()
    private val cartNavigator: CartNavigator by inject()
    private lateinit var productAdapter: ProductHorizontalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_search, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindViewModel()

        floatCartViewModel.loadCartInfoFirstTime()
        Handler().postDelayed({ searchView?.showKeyboard(view.context) }, 100)
    }

    private fun initView() {
        (activity as BaseActivity).apply {
            setToolbarTitle(title = getString(R.string.search))
            hideToolbarIconImage()
        }

        emptyLayoutView.apply {
            setEmptyTitle(getString(R.string.no_search))
            setEmptyDetails(getString(R.string.no_search_details))
            setEmptyImage(R.drawable.img_empty_search)
            setEmptyButtonText(getString(R.string.no_search_button))
            onClickAction = {
                searchView.clearText()
                searchView.showKeyboard(context)
            }
        }

        searchView.setHintContent(getString(R.string.default_search_for_grocery))
        searchView.enableSearch(true)
        searchView.onKeyWordsChange = {
            searchViewModel.searchProduct(keywords = it)
        }

        val linearLayoutManager = LinearLayoutManager(context)
        productAdapter = ProductHorizontalAdapter().apply {
            onSelectItem = { product ->
                context?.let {
                    searchView.hideKeyboard(it)
                }
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
        searchRecyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = productAdapter
            isNestedScrollingEnabled = false
        }

        searchScrollView.setOnScrollChangeListener(object :
            EndlessProductVerticalListListener(linearLayoutManager) {
            override fun onGoToBottomList() {
                searchViewModel.searchProduct(searchView.currentKeyword())
            }
        })

        //Cart Button
        floatCartButton.setOnClickListener {
            activity?.let {
                startActivity(cartNavigator.getCartActivity(it))
            }
        }
    }

    private fun bindViewModel() {
        searchViewModel.searchResultList.observe(viewLifecycleOwner, Observer {
            productAdapter.items = it
            searchScrollView.visible()
            emptyLayoutView.gone()
        })

        searchViewModel.scrollToTop.observe(viewLifecycleOwner, Observer {
            searchScrollView.scrollTo(0, 0)
        })

        searchViewModel.showDefaultSearchStatus.observe(viewLifecycleOwner, Observer {
            emptyLayoutView.apply {
                setEmptyTitle(getString(R.string.no_search))
                setEmptyDetails(getString(R.string.no_search_details))
                setEmptyImage(R.drawable.img_empty_search)
                setEmptyButtonText(getString(R.string.no_search_button))
            }
            emptyLayoutView.visible()
        })

        searchViewModel.showEmptySearchStatus.observe(viewLifecycleOwner, Observer {
            emptyLayoutView.apply {
                setEmptyTitle(getString(R.string.no_search_try_again))
                setEmptyDetails(getString(R.string.no_search_details_try_again))
                setEmptyImage(R.drawable.img_search_no_product)
                setEmptyButtonText(getString(R.string.no_search_button_try_again))
            }
            emptyLayoutView.visible()
        })

        searchViewModel.showNoMoreProductData.observe(viewLifecycleOwner, Observer {
            if (it) {
                bottomMessageTextView.visibility = View.VISIBLE
                bottomMessageTextView.text = getString(R.string.no_more_data)
            } else {
                bottomMessageTextView.visibility = View.GONE
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
    }
}