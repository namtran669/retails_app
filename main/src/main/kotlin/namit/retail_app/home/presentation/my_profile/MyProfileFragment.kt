package namit.retail_app.home.presentation.my_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.home.R

class MyProfileFragment: BaseFragment() {

    companion object {
        const val TAG = "ProfileFragment"
        fun getInstance(): MyProfileFragment = MyProfileFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {

    }

    private fun bindViewModel() {

    }
}