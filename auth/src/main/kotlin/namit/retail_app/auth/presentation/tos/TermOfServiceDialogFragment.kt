package namit.retail_app.auth.presentation.tos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import namit.retail_app.auth.R
import namit.retail_app.core.presentation.base.BaseFullScreenDialog
import kotlinx.android.synthetic.main.dialog_term_of_service.*
import org.koin.android.viewmodel.ext.android.viewModel

class TermOfServiceDialogFragment : BaseFullScreenDialog() {

    companion object {
        const val TAG = "TermOfServiceDialogFragment"
        private const val NEW_LINE_SYMBOL = "\n"
        private const val BREAK_LINE_SYMBOL = "<br/>"

        fun newInstance(): TermOfServiceDialogFragment =
            TermOfServiceDialogFragment()
    }

    private val viewModel: TermOfServiceViewModel by viewModel()

    var onAccept: () -> Unit = {}
    var onDontAccept: () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.dialog_term_of_service, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewModel()
        viewModel.loadTermOfService()
    }

    private fun initView() {
        agreeButton.setOnClickListener {
            dismiss()
            onAccept.invoke()
        }

        dontAgreeButton.setOnClickListener {
            dismiss()
            onDontAccept.invoke()
        }
    }

    private fun bindViewModel() {
        viewModel.termOfService.observe(viewLifecycleOwner, Observer {
            termOfServiceTextView.setMarkDownText(it.replace(NEW_LINE_SYMBOL, BREAK_LINE_SYMBOL))
        })
    }
}