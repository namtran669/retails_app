package namit.retail_app.core.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import namit.retail_app.core.R
import namit.retail_app.core.extension.loadImage
import kotlinx.android.synthetic.main.item_product_image.view.*
import kotlin.properties.Delegates

class ImageViewPagerAdapter(val context: Context) : PagerAdapter() {

    var items by Delegates.observable(listOf<String>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.item_product_image, null)
        view.productImageView.loadImage(items[position])
        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        val vp = container as ViewPager
        val view: View = `object` as View
        vp.removeView(view)
    }
}