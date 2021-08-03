package namit.retail_app.food.presentation.restaurant.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.navigation.CoreNavigator
import namit.retail_app.core.navigation.FoodNavigator
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.core.presentation.food_detail.FoodDetailDialog
import namit.retail_app.core.presentation.widget.decoration.VerticalSpaceItemDecoration
import namit.retail_app.food.R
import kotlinx.android.synthetic.main.fragment_menu_page.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MenuPageFragment : BaseFragment() {

    companion object {
        val TAG = MenuPageFragment::class.java.simpleName

        private const val ARG_RESTAURANT_DATA = "ARG_RESTAURANT_DATA"
        private const val ARG_CATEGORY_DATA = "ARG_CATEGORY_DATA"


        fun getNewInstance(
            restaurantData: MerchantInfoItem,
            categoryData: CategoryItem
        ): MenuPageFragment {
            val fragment = MenuPageFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_RESTAURANT_DATA, restaurantData)
                putParcelable(ARG_CATEGORY_DATA, categoryData)
            }
            return fragment
        }
    }

    private val viewModel: MenuPageViewModel by viewModel(parameters = {
        parametersOf(
            arguments?.getParcelable(ARG_RESTAURANT_DATA),
            arguments?.getParcelable(ARG_CATEGORY_DATA)
        )
    })
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var foodLayoutManager: LinearLayoutManager
    private val coreNavigator: CoreNavigator by inject()
    private val foodNavigator: FoodNavigator by inject()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_menu_page, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindViewModel()

        viewModel.loadFoodListFirstTime()
    }

    private fun initView() {
        foodAdapter = FoodAdapter()
            .apply {
                onSelectItem = {
                    viewModel.presentFoodDetailDialog(it)
                }
            }
        foodLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val decoration =
            VerticalSpaceItemDecoration(
                startEndSpace = resources.getDimension(R.dimen.productCategorySpaceStartEnd).toInt(),
                betweenSpace = resources.getDimension(R.dimen.productCategorySpaceBetween).toInt(),
                topSpace = resources.getDimension(R.dimen.productCategorySpaceTopBottom).toInt(),
                bottomSpace = resources.getDimension(R.dimen.productCategorySpaceTopBottom).toInt()
            )
        foodRecyclerView.apply {
            adapter = foodAdapter
            layoutManager = foodLayoutManager
            addItemDecoration(decoration)
            isNestedScrollingEnabled = false

        }
    }

    private fun bindViewModel() {
        viewModel.foodList.observe(viewLifecycleOwner, Observer {
            foodAdapter.items = it

        })

        viewModel.showHaveNoMoreProduct.observe(viewLifecycleOwner, Observer {
            if (it) {
                bottomMessageTextView.visibility = View.VISIBLE
                bottomMessageTextView.text = getString(R.string.no_more_data)
            } else {
                bottomMessageTextView.visibility = View.GONE
            }
        })

        viewModel.openFoodDetailDialog.observe(viewLifecycleOwner, Observer {
            activity?.supportFragmentManager?.let { fragmentManager ->
                coreNavigator.getFoodDetailDialog(product = it.second, merchantData = it.first)
                    .show(fragmentManager, FoodDetailDialog.TAG)
            }
        })
    }

    fun checkLoadMoreItem() {
        val firstItemIndex = foodLayoutManager.findFirstVisibleItemPosition()
        val visibleItemCount = foodLayoutManager.childCount
        val totalItemCount = foodLayoutManager.itemCount
        if (visibleItemCount + firstItemIndex >= totalItemCount) {
            viewModel.loadFoodList()
        }
    }
}