package com.example.karflex

import android.content.Context
import androidx.core.content.edit

/**
 * Wraps the "KarFlexSession" SharedPreferences.
 *
 * LoginActivity stores the pending OTP data (otp_code / otp_username / otp_phone);
 * once the OTP step passes, [login] stores the signed-in user for the Home page.
 */
class SessionManager(context: Context) {

    private val preferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    val pendingOtp: String?
        get() = preferences.getString(KEY_OTP_CODE, null)

    val pendingUsername: String?
        get() = preferences.getString(KEY_OTP_USERNAME, null)

    val pendingPhone: String?
        get() = preferences.getString(KEY_OTP_PHONE, null)

    val isLoggedIn: Boolean
        get() = userId != -1

    val userId: Int
        get() = preferences.getInt(KEY_USER_ID, -1)

    val username: String?
        get() = preferences.getString(KEY_USERNAME, null)

    val phone: String?
        get() = preferences.getString(KEY_PHONE, null)

    fun login(userId: Int, username: String, phone: String) {
        preferences.edit {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putString(KEY_PHONE, phone)
            remove(KEY_OTP_CODE)
        }
    }

    fun logout() {
        preferences.edit { clear() }
    }

    companion object {
        const val PREF_NAME = "KarFlexSession"

        // Written by LoginActivity
        const val KEY_OTP_CODE = "otp_code"
        const val KEY_OTP_USERNAME = "otp_username"
        const val KEY_OTP_PHONE = "otp_phone"

        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_PHONE = "phone"
    }
}
