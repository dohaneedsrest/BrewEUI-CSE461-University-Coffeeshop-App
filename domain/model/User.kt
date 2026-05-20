package com.eui.coffeeshop.domain.model

/**
 * User — domain model for user profile data stored in Room.
 * Passwords are never stored here; authentication state lives in SharedPreferences.
 */
data class User(
    val userId: String,
    val email: String,
    val fullName: String,
    val createdAt: Long = System.currentTimeMillis()
)
