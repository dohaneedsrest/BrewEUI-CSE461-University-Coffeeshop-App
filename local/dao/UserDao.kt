package com.eui.coffeeshop.data.local.dao

import androidx.room.*
import com.eui.coffeeshop.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * UserDao — full CRUD for local user profile storage.
 * Auth tokens / passwords are never stored here (that's AuthRepository / SharedPreferences).
 */
@Dao
interface UserDao {

    // ── Fetch All ────────────────────────────
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    // ── Fetch Single by ID ───────────────────
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    // ── Fetch Single by ID as Flow ───────────
    @Query("SELECT * FROM users WHERE userId = :userId")
    fun observeUser(userId: String): Flow<UserEntity?>

    // ── Insert ───────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // ── Update ───────────────────────────────
    @Update
    suspend fun updateUser(user: UserEntity)

    // ── Delete single ────────────────────────
    @Delete
    suspend fun deleteUser(user: UserEntity)

    // ── Delete by ID ─────────────────────────
    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteUserById(userId: String)

    // ── Clear all ────────────────────────────
    @Query("DELETE FROM users")
    suspend fun clearAll()


}
