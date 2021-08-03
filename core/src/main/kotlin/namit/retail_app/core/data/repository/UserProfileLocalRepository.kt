package namit.retail_app.core.data.repository

import namit.retail_app.core.data.entity.UserModel
import namit.retail_app.core.provider.PreferenceProvider
import com.google.gson.Gson

interface UserProfileLocalRepository {
    fun saveCurrentUser(user: UserModel)
    fun getCurrentUser(): UserModel
    fun removeCurrentUser()
    fun haveCurrentUser(): Boolean
}

class UserProfileLocalRepositoryImpl(private val preferenceProvider: PreferenceProvider) :
    UserProfileLocalRepository {

    companion object {
        private const val PREF_CURRENT_USER_INFO = "PREF_CURRENT_USER_INFO"
    }

    override fun saveCurrentUser(user: UserModel) {
        preferenceProvider.setPreference(
            key = PREF_CURRENT_USER_INFO,
            value = Gson().toJson(user)
        )
    }

    override fun getCurrentUser(): UserModel {
        val jsonString = preferenceProvider.getPreference(
            key = PREF_CURRENT_USER_INFO,
            defValue = ""
        )
        return Gson().fromJson(jsonString, UserModel::class.java)
    }

    override fun removeCurrentUser() {
        preferenceProvider.clearPreference(PREF_CURRENT_USER_INFO)
    }

    override fun haveCurrentUser(): Boolean {
        return preferenceProvider.getPreference(
            key = PREF_CURRENT_USER_INFO,
            defValue = ""
        ).isNotEmpty()
    }

}