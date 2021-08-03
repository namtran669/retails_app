package namit.retail_app.food.presentation.restaurant

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.extension.loadImage
import namit.retail_app.core.navigation.FoodNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.core.presentation.widget.decoration.HorizontalSpaceItemDecoration
import namit.retail_app.core.utils.AppBarStateChangeListener
import namit.retail_app.food.R
import namit.retail_app.food.presentation.restaurant.menu.MenuPageFragment
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_restaurant_detail.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.DecimalFormat

class RestaurantDetailFragment : BaseFragment() {

    companion object {
        val TAG = RestaurantDetailFragment::class.java.simpleName
        private const val DISTANCE_FORMAT = "0.#"
        private const val ARG_RESTAURANT_DATA = "ARG_RESTAURANT_DATA"

        fun getNewInstance(restaurantData: MerchantInfoItem): RestaurantDetailFragment {
            val fragment = RestaurantDetailFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_RESTAURANT_DATA, restaurantData)
            }
            return fragment
        }

    }

    private val foodNavigator: FoodNavigator by inject()

    private val viewModel: RestaurantDetailViewModel by viewModel(parameters = {
        parametersOf(arguments?.getParcelable(ARG_RESTAURANT_DATA))
    })

    private var appBarStateChangeListener: AppBarStateChangeListener? = null
    private lateinit var categoryAdapter: FoodRootCategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_restaurant_detail, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initView()
        bindViewModel()

        viewModel.renderRestaurantInfo()
        viewModel.loadCategoryList()
    }

    private fun initToolbar() {
        (activity as BaseActivity).apply {
            title = ""
            if (appBarStateChangeListener == null) {
                appBarStateChangeListener = object : AppBarStateChangeListener() {
                    override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                        when (state) {
                            State.COLLAPSED -> {
                                setShowProductName(isShow = true)
                            }

                            State.EXPANDED -> {
                                setShowProductName(isShow = false)
                            }
                            else -> return

                        }
                    }
                }
            } else {
                appBarLayout.removeOnOffsetChangedListener(appBarStateChangeListener)
            }

            backImageView.setOnClickListener {
                (activity as BaseActivity).onBackPressed()
            }
        }
    }

    private fun setShowProductName(isShow: Boolean) {
        if (isShow) {
            toolbar.visibility = View.VISIBLE
            backImageView.setColorFilter(
                ContextCompat.getColor(
                    activity!!,
                    R.color.trout
                )
            )
        } else {
            toolbar.visibility = View.GONE
            backImageView.setColorFilter(Color.WHITE)
        }
    }

    private fun initView() {
        couponBannerView.setTotalCoupons(totalCoupon = 10)
        couponBannerView.setOnClickListener {
            //TODO Display coupon view
        }

        //BackButton
        backImageView.setOnClickListener {
            (activity as BaseActivity).onBackPressed()
        }
        backCollapseImageView.setOnClickListener {
            (activity as BaseActivity).onBackPressed()
        }

        categoryAdapter = FoodRootCategoryAdapter().apply {
            onSelectItem = {
                viewModel.updateCategory(it)
            }
        }
        rootCategoryRecyclerView.apply {
            adapter = categoryAdapter
            addItemDecoration(
                HorizontalSpaceItemDecoration(
                    startEndSpace = resources.getDimensionPixelOffset(R.dimen.contentCardMarginBetween),
                    betweenSpace = resources.getDimensionPixelOffset(R.dimen.contentCardMarginBetween),
                    topBottomSpace = resources.getDimensionPixelOffset(R.dimen.contentCardMarginBetween)
                )
            )
        }

        restaurantStickyScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (view?.getChildAt(view.childCount - 1) != null
                && (scrollY >= view.getChildAt(view.childCount - 1).measuredHeight - view.measuredHeight)
                && scrollY > oldScrollY
            ) {
                viewModel.checkGotoBottomList()
            }
        })

    }

    @SuppressLint("SetTextI18n")
    private fun bindViewModel() {
        viewModel.categoryList.observe(viewLifecycleOwner, Observer {
            categoryDividerView.visibility = View.VISIBLE
            categoryAdapter.items = it

        })

        viewModel.selectedCategory.observe(viewLifecycleOwner, Observer {
            val menuTag = getMenuPageTag(it.second)
            val fragment = if (childFragmentManager.findFragmentByTag(menuTag) != null) {
                childFragmentManager.findFragmentByTag(menuTag) as MenuPageFragment
            } else {
                foodNavigator.getMenuPageFragment(it.first, it.second) as MenuPageFragment
            }
            replaceFragment(
                fragment = fragment,
                addToBackStack = true,
                containerId = R.id.menuFoodFragmentContainer,
                tag = menuTag
            )
        })

        viewModel.checkGoToBottom.observe(viewLifecycleOwner, Observer {
            val menuTag = getMenuPageTag(it)
            (childFragmentManager.findFragmentByTag(menuTag) as MenuPageFragment).checkLoadMoreItem()
        })

        viewModel.restaurantInfo.observe(viewLifecycleOwner, Observer {
            titleToolbar.text = it.title
            restaurantTitleTextView.text = it.title
            distanceTextView.text =
                "${DecimalFormat(DISTANCE_FORMAT).format(it.distance)} ${resources.getString(R.string.km_symbol)}"

            bannerRestaurantImageView.loadImage(imageUrl = it.cover, placeHolder = R.color.altoGray)

            it.openingPeriod?.let {
                openCloseTimeTextView.text = it
                openCloseTimeDotView.visibility = View.VISIBLE
            } ?: kotlin.run { openCloseTimeDotView.visibility = View.GONE }
        })
    }

    private fun getMenuPageTag(category: CategoryItem) = "${MenuPageFragment.TAG}_${category.id}"

    override fun onResume() {
        super.onResume()
        appBarLayout.addOnOffsetChangedListener(appBarStateChangeListener)
        backImageView.setColorFilter(Color.WHITE)
    }

    override fun onPause() {
        appBarLayout.removeOnOffsetChangedListener(appBarStateChangeListener)
        super.onPause()
    }

    private fun replaceFragment(
        fragment: BaseFragment, addToBackStack: Boolean = false,
        containerId: Int, tag: String? = fragment.TAG
    ) {
        activity?.let {
            if (!it.isFinishing) {
                val fragmentTransaction = childFragmentManager.beginTransaction()
                fragmentTransaction.replace(containerId, fragment, tag)

                if (addToBackStack) {
                    fragmentTransaction.addToBackStack(tag)
                }
                fragmentTransaction.commit()
            }
        }
    }

}