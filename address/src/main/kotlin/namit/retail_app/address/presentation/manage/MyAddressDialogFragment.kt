package namit.retail_app.address.presentation.manage

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import namit.retail_app.address.R
import namit.retail_app.address.presentation.edit.EditAddressBottomDialog
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.navigation.AddressNavigator
import namit.retail_app.core.navigation.CoreNavigator
import namit.retail_app.core.presentation.base.BaseFullScreenDialog
import namit.retail_app.core.presentation.dialog.alert.QuestionDialog
import io.sulek.ssml.SSMLLinearLayoutManager
import kotlinx.android.synthetic.main.dialog_my_address.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MyAddressDialogFragment : BaseFullScreenDialog() {

    companion object {
        val TAG = MyAddressDialogFragment::class.java.simpleName

        fun newInstance(): MyAddressDialogFragment {
            return MyAddressDialogFragment()
        }
    }

    private val addressNavigator: AddressNavigator by inject()
    private val coreNavigator: CoreNavigator by inject()
    private val viewModel: MyAddressDialogViewModel by viewModel()

    private lateinit var favoriteAddressAdapter: MyAddressAdapter
    private lateinit var otherAddressAdapter: MyAddressAdapter

    var onDismissDialog: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.dialog_my_address, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindViewModel()
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        dialog?.apply {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            window?.setLayout(width, height)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAddressList()
    }

    private fun initView() {

        notificationSimpleToolbar.onBackPressed = {
            dismiss()
        }

        favoriteAddressAdapter = MyAddressAdapter()
        favoriteAddressRecyclerView.apply {
            adapter = favoriteAddressAdapter
            layoutManager = SSMLLinearLayoutManager(context)
        }

        favoriteAddressAdapter.apply {
            onClickEdit = {
                viewModel.presentEditAddressOption(it)
            }
            onDeleteItem = {
                showQuestionDialog(it)
            }

            onSelectItem = {
                viewModel.updateFavoriteSelection(it)
            }

            onSwipeItem = { index: Int, isExpand: Boolean ->
                viewModel.updateFavoriteSwipe(index, isExpand)
            }
        }

        otherAddressAdapter = MyAddressAdapter()
        otherAddressRecyclerView.apply {
            adapter = otherAddressAdapter
            layoutManager = SSMLLinearLayoutManager(context)
        }

        otherAddressAdapter.apply {
            onClickEdit = {
                viewModel.presentEditAddressOption(it)
            }

            onDeleteItem = {
                showQuestionDialog(it)
            }

            onSelectItem = {
                viewModel.updateOtherSelection(it)
            }

            onSwipeItem = { index: Int, isExpand: Boolean ->
                viewModel.updateOtherSwipe(index, isExpand)
            }
        }

        addNewAddressButton.setOnClickListener {
            viewModel.presentEditAddressOption()
        }

        addNowTextView.setOnClickListener {
            viewModel.presentEditAddressOption()
        }
    }

    private fun bindViewModel() {

        viewModel.favoriteListLiveData.observe(this, Observer {
            favoriteAddressAdapter.items = it
            showFavoriteAddressList()
        })

        viewModel.otherListLiveData.observe(this, Observer {
            otherAddressAdapter.items = it
            showOtherAddressList()
        })

        viewModel.openEditAddressDialog.observe(this, Observer {
            showEditAddressDialog(it)
        })

        viewModel.showNoAddressText.observe(viewLifecycleOwner, Observer {
            showNoAddressText()
        })

        viewModel.openTitleFavoriteType.observe(viewLifecycleOwner, Observer {isOpen ->
            favoriteTitleTextView.visibility = if(isOpen) View.VISIBLE else View.GONE
        })

        viewModel.openTitleOtherType.observe(viewLifecycleOwner, Observer {isOpen ->
            otherTitleTextView.visibility = if(isOpen) View.VISIBLE else View.GONE
        })

        viewModel.dismissDialog.observe(this, Observer {
            this.dismiss()
        })
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        onDismissDialog.invoke()
    }

    private fun showNoAddressText() {
        noAddressTextView.visibility = View.VISIBLE
        addNowTextView.visibility = View.VISIBLE
        favoriteTitleTextView.visibility = View.GONE
        favoriteAddressRecyclerView.visibility = View.GONE
        otherTitleTextView.visibility = View.GONE
        otherAddressRecyclerView.visibility = View.GONE
    }

    private fun showFavoriteAddressList() {
        noAddressTextView.visibility = View.GONE
        addNowTextView.visibility = View.GONE
        favoriteTitleTextView.visibility = View.VISIBLE
        favoriteAddressRecyclerView.visibility = View.VISIBLE
    }

    private fun showOtherAddressList() {
        noAddressTextView.visibility = View.GONE
        addNowTextView.visibility = View.GONE
        otherTitleTextView.visibility = View.VISIBLE
        otherAddressRecyclerView.visibility = View.VISIBLE
    }

    private fun showQuestionDialog(addressData: AddressModel) {
        activity?.supportFragmentManager?.let {
            coreNavigator.alertQuestionDialog(
                title = getString(R.string.are_you_sure_you_want_to_delete_this_address),
                message = getString(R.string.this_address_will_be_permanently_deleted_from_your_saved_address_list),
                positiveButtonText = getString(R.string.yes),
                negativeButtonText = getString(R.string.cancel)
            ).apply {
                onPositionClick = {
                    viewModel.removeAddressOption(addressData)
                }
            }.show(it, QuestionDialog.TAG)
        }
    }

    private fun showEditAddressDialog(address: AddressModel?) {
        activity?.supportFragmentManager?.let {
            (addressNavigator.getEditAddressDialog(address) as EditAddressBottomDialog).apply {
                onDismissDialog = {
                    viewModel.loadAddressList()
                }
            }.show(it, EditAddressBottomDialog.TAG)
        }
    }

}