package com.eui.coffeeshop.data.repository

import android.content.Context
import android.content.SharedPreferences

/**
 * AuthRepository — mock authentication using SharedPreferences.
 * Replaces Firebase Auth for this build. To connect Firebase:
 * replace login() and register() bodies with FirebaseAuth calls.
 */
class AuthRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false)

    fun getCurrentUserId(): String = prefs.getString(KEY_USER_ID, "guest_user") ?: "guest_user"

    fun getCurrentUserName(): String = prefs.getString(KEY_USER_NAME, "Guest") ?: "Guest"

    /**
     * Mock login — accepts any valid email/password format.
     * Returns true on success, false if credentials are "wrong" (demo: any input works).
     */
    suspend fun login(email: String, password: String): Result<String> {
        return try {
            // In a real build: FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
            val userId = "user_${email.hashCode()}"
            prefs.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_USER_EMAIL, email)
                .apply()
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, fullName: String): Result<String> {
        return try {
            val userId = "user_${email.hashCode()}"
            prefs.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_USER_NAME, fullName)
                .apply()
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        prefs.edit()
            .putBoolean(KEY_LOGGED_IN, false)
            .remove(KEY_USER_ID)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_NAME)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "eui_coffee_auth"
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
    }
}
