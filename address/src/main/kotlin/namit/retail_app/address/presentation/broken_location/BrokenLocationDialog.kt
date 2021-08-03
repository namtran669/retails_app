package namit.retail_app.address.presentation.broken_location

import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import namit.retail_app.address.R
import namit.retail_app.core.config.FAQ_URL
import namit.retail_app.core.presentation.base.BasePermissionFullScreenDialog
import kotlinx.android.synthetic.main.dialog_broken_location.*
import org.koin.android.viewmodel.ext.android.viewModel


class BrokenLocationDialog : BasePermissionFullScreenDialog() {

    companion object {
        val TAG = BrokenLocationDialog::class.java.simpleName

        fun newInstance(): BrokenLocationDialog {
            return BrokenLocationDialog()
        }
    }

    private val viewModel: BrokenLocationViewModel by viewModel()

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val display = activity!!.windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val height = (size.y * 1)
            setLayout(width, height)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_broken_location, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewModel()
    }

    private fun bindViewModel() {
        viewModel.openFAQPage.observe(viewLifecycleOwner, Observer {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(FAQ_URL))
            startActivity(browserIntent)
        })
    }

    private fun initView() {
        iconCloseImageView.setOnClickListener {
            dismiss()
        }

        helpCenterButton.setOnClickListener {
            viewModel.openFAQPage()
            dismiss()
        }
    }
}