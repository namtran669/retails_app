package namit.retail_app.story.presentation.details

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import namit.retail_app.core.data.entity.BaseStoryContent
import namit.retail_app.core.extension.loadImage
import namit.retail_app.core.presentation.base.BaseFullScreenDialog
import namit.retail_app.core.utils.getHeightScreenSize
import namit.retail_app.core.utils.getWidthScreenSize
import namit.retail_app.story.R
import kotlinx.android.synthetic.main.dialog_story_detail.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class StoryDetailDialogFragment : BaseFullScreenDialog() {

    companion object {
        const val TAG = "StoryDetailDialogFragment"
        private const val NEW_LINE_SYMBOL = "\n"
        private const val BREAK_LINE_SYMBOL = "<br/>"

        private const val ARG_CONTENT = "ARG_CONTENT"
        fun newInstance(baseStoryContent: BaseStoryContent): StoryDetailDialogFragment {
            val fragment = StoryDetailDialogFragment()
            val bundle = Bundle().apply {
                putParcelable(ARG_CONTENT, baseStoryContent)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    private val viewModel: StoryDetailDialogViewModel by viewModel(parameters = {
        parametersOf(
            arguments!!.getParcelable(ARG_CONTENT)
        )
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.dialog_story_detail, null)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewModel()
        viewModel.renderStoryDetail()
    }

    private fun initView() {
        toolbar.apply {
            setScreenTitle("")
            setLeftIconImageView(R.drawable.ic_close_black)
            onBackPressed = {
                dismiss()
            }
        }

        storyImageView.apply {
            layoutParams.width = getWidthScreenSize(context)
            layoutParams.height = (getHeightScreenSize(context) * 0.30).toInt()
        }

        val scrollViewRect = Rect()
        storyDetailScrollView.apply {
            getHitRect(scrollViewRect)
            setOnScrollChangeListener { _: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->
                if (titleTextView.getLocalVisibleRect(scrollViewRect)) {
                    viewModel.hideTitle()
                } else {
                    viewModel.showTitle()
                }
            }
        }
    }

    private fun bindViewModel() {
        viewModel.storyImage.observe(viewLifecycleOwner, Observer {
            storyImageView.loadImage(it)
        })

        viewModel.renderImageInCenter.observe(viewLifecycleOwner, Observer {
            storyImageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        })

        viewModel.title.observe(viewLifecycleOwner, Observer {
            titleTextView.text = it
        })

        viewModel.details.observe(viewLifecycleOwner, Observer {
            markdownView.isOpenUrlInBrowser = true
            markdownView.setMarkDownText(it.replace(NEW_LINE_SYMBOL, BREAK_LINE_SYMBOL))
        })

        viewModel.showTitle.observe(viewLifecycleOwner, Observer {
            toolbar.apply {
                setScreenTitle(it)
                elevation =
                    context.resources.getDimension(namit.retail_app.core.R.dimen.toolbarProductElevation)
            }
        })

        viewModel.hideTitle.observe(viewLifecycleOwner, Observer {
            toolbar.apply {
                setScreenTitle("")
                elevation =
                    context.resources.getDimension(namit.retail_app.core.R.dimen.toolbarProductElevation)
            }
        })

        viewModel.showActionButton.observe(viewLifecycleOwner, Observer {
            actionButton.visibility = View.VISIBLE
        })
    }
}