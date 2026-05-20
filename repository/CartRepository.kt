package com.eui.coffeeshop.data.repository

import com.eui.coffeeshop.data.local.dao.CartDao
import com.eui.coffeeshop.data.local.entity.toDomain
import com.eui.coffeeshop.data.local.entity.toEntity
import com.eui.coffeeshop.domain.model.CartItem
import com.eui.coffeeshop.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * CartRepository — manages the local shopping cart backed by Room.
 * The cart persists across app restarts since it lives in the database.
 */
class CartRepository(private val cartDao: CartDao) {

    // ── Observe (reactive) ────────────────────
    fun getCartItems(): Flow<List<CartItem>> =
        cartDao.getCartItems().map { it.map { e -> e.toDomain() } }

    fun getCartItemCount(): Flow<Int> = cartDao.getCartItemCount()

    // ── Fetch Single ──────────────────────────
    suspend fun getCartItemById(productId: String): CartItem? =
        cartDao.getCartItemById(productId)?.toDomain()

    // ── Insert from Product ───────────────────
    suspend fun addItem(product: Product) {
        // Check if item already in cart — if so, increment quantity
        val existing = cartDao.getCartItemById(product.id)
        if (existing != null) {
            cartDao.updateQuantity(product.id, existing.quantity + 1)
        } else {
            val cartItem = CartItem(
                productId = product.id,
                name = product.name,
                price = product.price,
                imageUrl = product.imageUrl,
                quantity = 1,
                category = product.category
            )
            cartDao.addItem(cartItem.toEntity())
        }
    }

    // ── Update ────────────────────────────────
    suspend fun updateItem(cartItem: CartItem) =
        cartDao.updateItem(cartItem.toEntity())

    suspend fun updateQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            cartDao.removeItemById(productId)
        } else {
            cartDao.updateQuantity(productId, quantity)
        }
    }

    // ── Delete ────────────────────────────────
    suspend fun removeItem(cartItem: CartItem) =
        cartDao.removeItem(cartItem.toEntity())

    suspend fun removeItemById(productId: String) =
        cartDao.removeItemById(productId)

    // ── Clear all ─────────────────────────────
    suspend fun clearCart() = cartDao.clearCart()
}
