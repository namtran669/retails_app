package namit.retail_app.app.data.entity

class FirebaseRemoteConfigKey {
    companion object {
        const val KEY_FORCE_UPDATE = "forceUpdate"
        const val KEY_STORE_CLOSED = "storeClosed"
        const val KEY_VERSION_CODE = "versionCode"
        const val FORCE_UPDATE_TYPE_NONE = "none"
        const val FORCE_UPDATE_TYPE_SOFT = "soft"
        const val FORCE_UPDATE_TYPE_FORCE = "force"
    }
}