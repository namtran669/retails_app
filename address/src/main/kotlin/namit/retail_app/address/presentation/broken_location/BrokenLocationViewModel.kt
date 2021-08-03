package namit.retail_app.address.presentation.broken_location


import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.utils.SingleLiveEvent

class BrokenLocationViewModel(
    private val eventTrackingManager: EventTrackingManager
) : BaseViewModel() {

    val openFAQPage = SingleLiveEvent<Unit>()

    fun openFAQPage() {
        eventTrackingManager.trackHelpCenter()
        openFAQPage.call()
    }

}