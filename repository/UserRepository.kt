package com.eui.coffeeshop.data.repository

import com.eui.coffeeshop.data.local.dao.UserDao
import com.eui.coffeeshop.data.local.entity.toDomain
import com.eui.coffeeshop.data.local.entity.toEntity
import com.eui.coffeeshop.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * UserRepository — manages local user profile data in Room.
 * Does NOT handle authentication (that stays in AuthRepository/SharedPreferences).
 */
class UserRepository(private val userDao: UserDao) {

    // ── Observe All (reactive) ────────────────
    fun getAllUsers(): Flow<List<User>> =
        userDao.getAllUsers().map { it.map { e -> e.toDomain() } }

    // ── Observe Single (reactive) ─────────────
    fun observeUser(userId: String): Flow<User?> =
        userDao.observeUser(userId).map { it?.toDomain() }

    // ── Fetch Single (one-shot) ───────────────
    suspend fun getUserById(userId: String): User? =
        userDao.getUserById(userId)?.toDomain()

    // ── Insert ────────────────────────────────
    suspend fun saveUser(user: User) =
        userDao.insertUser(user.toEntity())

    // ── Update ────────────────────────────────
    suspend fun updateUser(user: User) =
        userDao.updateUser(user.toEntity())

    // ── Delete ────────────────────────────────
    suspend fun deleteUser(userId: String) =
        userDao.deleteUserById(userId)

    // ── Clear all ─────────────────────────────
    suspend fun clearAll() = userDao.clearAll()
}
