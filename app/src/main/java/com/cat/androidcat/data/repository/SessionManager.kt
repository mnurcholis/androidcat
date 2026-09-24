package com.cat.androidcat.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.cat.androidcat.data.model.UserDto

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "cat_app_session"
        private const val KEY_TOKEN = "access_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ROLE = "user_role"
    }

    fun saveAuthSession(token: String, user: UserDto) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_ID, user.id)
            .putString(KEY_USER_EMAIL, user.email)
            .putString(KEY_USER_NAME, user.name ?: "")
            .putString(KEY_USER_ROLE, user.role)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUser(): UserDto? {
        val id = prefs.getString(KEY_USER_ID, null) ?: return null
        val email = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        val name = prefs.getString(KEY_USER_NAME, null)
        val role = prefs.getString(KEY_USER_ROLE, "USER") ?: "USER"
        return UserDto(id = id, email = email, name = name, role = role)
    }

    val isLoggedIn: Boolean get() = !getToken().isNullOrEmpty()

    val isAdmin: Boolean get() = getUser()?.isAdmin == true

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
