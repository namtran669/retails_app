package namit.retail_app.grocery.presentation.category_all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.navigation.CoreNavigator
import namit.retail_app.core.navigation.GroceryNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.core.presentation.widget.decoration.HorizontalSpaceItemDecoration
import namit.retail_app.grocery.R
import namit.retail_app.grocery.presentation.category_sub.GrocerySubCategoryFragment
import namit.retail_app.grocery.presentation.category_sub_detail.GrocerySubCategoryDetailFragment
import kotlinx.android.synthetic.main.fragment_grocery_all_category.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GroceryAllCategoryFragment : BaseFragment() {
    companion object {
        const val TAG = "GroceryAllCategoryFragment"

        private const val ARG_MERCHANT_INFO = "ARG_MERCHANT_INFO"

        fun getNewInstance(merchantInfoItem: MerchantInfoItem): GroceryAllCategoryFragment {
            val fragment = GroceryAllCategoryFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_MERCHANT_INFO, merchantInfoItem)
            }
            return fragment
        }

    }

    private val coreNavigator: CoreNavigator by inject()
    private val groceryNavigator: GroceryNavigator by inject()
    private val viewModel: GroceryAllCategoryViewModel by viewModel(parameters = {
        parametersOf(arguments!!.getParcelable(ARG_MERCHANT_INFO)!!)
    })
    private lateinit var featureCategoryAdapter: GroceryFeatureCategoryAdapter
    private lateinit var catalogCategoryAdapter: GroceryCatalogCategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_grocery_all_category, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindViewModel()
        viewModel.loadAllCategoryList()
    }

    private fun initView() {
        categoryToolbarView.setScreenTitle(getString(R.string.aisles))
        searchView.apply {
            setHintContent(resources.getString(R.string.search_hint_general))
            enableSearch(enable = false)
            setOnClickListener {
                context?.let {
                    startActivity(coreNavigator.openSearchActivity(
                        context = it,
                        merchantInfoItem = arguments!!.getParcelable(ARG_MERCHANT_INFO)!!
                    ))
                }
            }
        }

        categoryToolbarView.onBackPressed = {
            (activity as BaseActivity).onBackPressed()
        }

        featureCategoryAdapter = GroceryFeatureCategoryAdapter()
        val featureDecoration =
            HorizontalSpaceItemDecoration(
                startEndSpace = resources.getDimension(R.dimen.featureCategoryStartEndSpace).toInt(),
                betweenSpace = resources.getDimension(R.dimen.featureCategoryBetweenSpace).toInt()
            )
        featureCategoryRecycleView.apply {
            isNestedScrollingEnabled = false
            adapter = featureCategoryAdapter
            addItemDecoration(featureDecoration)
        }

        catalogCategoryAdapter = GroceryCatalogCategoryAdapter().apply {
            onItemSelect = { categorySelected ->
                viewModel.checkSubCategory(categorySelected)
            }
        }

        allCatalogRecycleView.apply {
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(context, 3)
            adapter = catalogCategoryAdapter
        }

    }

    private fun bindViewModel() {
        viewModel.categoryList.observe(viewLifecycleOwner, Observer {
            catalogCategoryAdapter.items = it
        })

        viewModel.openSubCategory.observe(viewLifecycleOwner, Observer {
            (activity as BaseActivity).apply {
                val groceryCategorySubFragment =
                    groceryNavigator.getGrocerySubCategoryFragment(
                        merchantInfoItem = arguments!!.getParcelable(ARG_MERCHANT_INFO)!!,
                        parentCategory = it.first,
                        childCategoryList = it.second
                    ) as GrocerySubCategoryFragment
                addFragment(
                    fragment = groceryCategorySubFragment,
                    tag = GroceryAllCategoryFragment.TAG,
                    addToBackStack = true
                )
            }
        })

        viewModel.openSubCategoryDetail.observe(viewLifecycleOwner, Observer {
            (activity as BaseActivity).apply {
                val subCategoryDetailFragment =
                    groceryNavigator.getGrocerySubCategoryDetailFragment(
                        arguments!!.getParcelable(ARG_MERCHANT_INFO)!!,
                        selectedCategory = it,
                        parentCategory = it
                    ) as GrocerySubCategoryDetailFragment
                addFragment(
                    fragment = subCategoryDetailFragment,
                    tag = GroceryAllCategoryFragment.TAG,
                    addToBackStack = true
                )
            }
        })
    }
}