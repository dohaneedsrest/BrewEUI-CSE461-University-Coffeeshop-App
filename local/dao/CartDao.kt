package com.eui.coffeeshop.data.local.dao

import androidx.room.*
import com.eui.coffeeshop.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    // ── Fetch All (reactive) ─────────────────
    @Query("SELECT * FROM cart ORDER BY name")
    fun getCartItems(): Flow<List<CartItemEntity>>

    // ── Fetch Single by ID ───────────────────
    @Query("SELECT * FROM cart WHERE productId = :productId")
    suspend fun getCartItemById(productId: String): CartItemEntity?

    // ── Item Count (reactive) ────────────────
    @Query("SELECT COUNT(*) FROM cart")
    fun getCartItemCount(): Flow<Int>

    // ── Insert / Replace ─────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItem(item: CartItemEntity)

    // ── Update ───────────────────────────────
    @Update
    suspend fun updateItem(item: CartItemEntity)

    // ── Update Quantity ──────────────────────
    @Query("UPDATE cart SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateQuantity(productId: String, quantity: Int)

    // ── Delete single ────────────────────────
    @Delete
    suspend fun removeItem(item: CartItemEntity)

    // ── Delete by ID ─────────────────────────
    @Query("DELETE FROM cart WHERE productId = :productId")
    suspend fun removeItemById(productId: String)

    // ── Clear all ────────────────────────────
    @Query("DELETE FROM cart")
    suspend fun clearCart()
}
