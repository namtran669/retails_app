package namit.retail_app.app.presentation.onboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class OnboardPagerAdapter(manger: FragmentManager) :
    FragmentPagerAdapter(manger, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val listFragment = mutableListOf<Fragment>()

    override fun getItem(position: Int): Fragment {
        return listFragment[position]
    }

    override fun getCount(): Int {
        return listFragment.size
    }

    fun addFrag(fragment: Fragment) {
        listFragment.add(fragment)
    }

}