package namit.retail_app.grocery.presentation.category_sub

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.navigation.CoreNavigator
import namit.retail_app.core.navigation.GroceryNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.core.presentation.widget.decoration.VerticalSpaceItemDecoration
import namit.retail_app.grocery.R
import namit.retail_app.grocery.presentation.category_all.GroceryAllCategoryFragment
import namit.retail_app.grocery.presentation.category_sub_detail.GrocerySubCategoryDetailFragment
import kotlinx.android.synthetic.main.fragment_grocery_sub_category.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GrocerySubCategoryFragment : BaseFragment() {
    companion object {
        const val TAG = "GrocerySubCategoryFragment"
        private const val ARG_MERCHANT_INFO = "ARG_MERCHANT_INFO"
        private const val ARG_ROOT_CATEGORY = "ARG_ROOT_CATEGORY"
        private const val ARG_CHILD_CATEGORY_LIST = "ARG_CHILD_CATEGORY_LIST"

        fun getNewInstance(
            merchantInfoItem: MerchantInfoItem,
            rootCategory: CategoryItem,
            childCategoryList: List<CategoryItem>
        ): GrocerySubCategoryFragment {
            val fragment = GrocerySubCategoryFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_MERCHANT_INFO, merchantInfoItem)
                putParcelable(ARG_ROOT_CATEGORY, rootCategory)
                putParcelableArrayList(
                    ARG_CHILD_CATEGORY_LIST,
                    childCategoryList as ArrayList<out Parcelable>?
                )
            }
            return fragment
        }

    }

    private val groceryNavigator: GroceryNavigator by inject()
    private val coreNavigator: CoreNavigator by inject()
    private val viewModel: GrocerySubCategoryViewModel by viewModel(parameters = {
        parametersOf(
            arguments?.getParcelable(ARG_ROOT_CATEGORY),
            arguments?.getParcelableArrayList<CategoryItem>(ARG_CHILD_CATEGORY_LIST)
        )
    })
    lateinit var subCategoryAdapter: GrocerySubCategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_grocery_sub_category, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindViewModel()
        viewModel.renderSubcategoryList()
    }

    private fun initView() {
        searchView.apply {
            setHintContent(resources.getString(R.string.search_hint_category))
            enableSearch(enable = false)
            setOnClickListener {
                context?.let {
                    startActivity(
                        coreNavigator.openSearchActivity(
                            context = it,
                            merchantInfoItem = arguments!!.getParcelable(ARG_MERCHANT_INFO)!!
                        ))
                }
            }
        }
        categoryToolbarView.onBackPress = {
            (activity as? BaseActivity)?.onBackPressed()
        }

        subCategoryAdapter = GrocerySubCategoryAdapter()
        subCategoryRecycleView.apply {
            adapter = subCategoryAdapter
            addItemDecoration(
                VerticalSpaceItemDecoration(
                    startEndSpace = resources.getDimensionPixelOffset(R.dimen.subCategoryStartEndSpace),
                    betweenSpace = resources.getDimensionPixelOffset(R.dimen.subCategoryBetweenSpace),
                    topSpace = resources.getDimensionPixelSize(R.dimen.subCategoryTopSpace),
                    bottomSpace = resources.getDimensionPixelOffset(R.dimen.subCategoryBottomSpace)
                )
            )
        }
        subCategoryAdapter.onItemSelect = { subCategory ->
            viewModel.checkSubCategory(subCategory)
        }
    }

    private fun bindViewModel() {
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

        viewModel.childCategoryList.observe(viewLifecycleOwner, Observer {
            subCategoryAdapter.items = it
        })

        viewModel.openOtherSubCategory.observe(viewLifecycleOwner, Observer {
            (activity as? BaseActivity)?.apply {
                val groceryCategorySubFragment =
                    groceryNavigator.getGrocerySubCategoryFragment(
                        merchantInfoItem = arguments!!.getParcelable(ARG_MERCHANT_INFO)!!,
                        parentCategory = it.first,
                        childCategoryList = it.second
                    ) as GrocerySubCategoryFragment
                addFragment(
                    fragment = groceryCategorySubFragment,
                    tag = GrocerySubCategoryFragment.TAG,
                    addToBackStack = true
                )
            }
        })

        viewModel.openSubCategoryDetail.observe(viewLifecycleOwner, Observer {
            (activity as? BaseActivity)?.apply {
                val subCategoryDetailFragment =
                    groceryNavigator.getGrocerySubCategoryDetailFragment(
                        merchantInfoItem = arguments!!.getParcelable(ARG_MERCHANT_INFO)!!,
                        selectedCategory = it.first,
                        parentCategory = it.second
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