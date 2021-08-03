package namit.retail_app.home.presentation.profile.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.home.R

class SettingProfileFragment: BaseFragment() {

    companion object {
        const val TAG = "SettingProfileFragment"
        fun getInstance(): SettingProfileFragment = SettingProfileFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting_profile, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        (activity as BaseActivity).setToolbarTitle(getString(R.string.my_profile))
    }
}