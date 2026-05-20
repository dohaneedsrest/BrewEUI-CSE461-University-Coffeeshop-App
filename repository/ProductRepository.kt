package com.eui.coffeeshop.data.repository

import com.eui.coffeeshop.data.local.dao.ProductDao
import com.eui.coffeeshop.data.local.entity.toDomain
import com.eui.coffeeshop.data.local.entity.toEntity
import com.eui.coffeeshop.data.mock.MockDataSource
import com.eui.coffeeshop.domain.model.Product
import com.eui.coffeeshop.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * ProductRepository — single source of truth for product data.
 *
 * Offline-first strategy:
 *   1. UI always observes Room via Flow (reactive, automatic updates)
 *   2. Mock data is seeded into Room on first launch
 *   3. If a remote source (e.g., Firestore) were connected, updates would
 *      be pushed into Room here, and the UI would update automatically
 *      since it observes Room's Flow — no UI-layer changes needed.
 */
class ProductRepository(private val productDao: ProductDao) {

    // ── Observe All (reactive) ────────────────────────────────────────────
    fun getAllProducts(): Flow<List<Product>> =
        productDao.getAllProducts().map { it.map { entity -> entity.toDomain() } }

    fun getProductsByCategory(category: String): Flow<List<Product>> =
        productDao.getProductsByCategory(category).map { it.map { e -> e.toDomain() } }

    fun observeProduct(productId: String): Flow<Product?> =
        productDao.observeProductById(productId).map { it?.toDomain() }

    // ── Fetch Single (one-shot) ───────────────────────────────────────────
    suspend fun getProductById(productId: String): Product? =
        productDao.getProductById(productId)?.toDomain()

    // ── Insert ────────────────────────────────────────────────────────────
    suspend fun insertProduct(product: Product) =
        productDao.insertProduct(product.toEntity())

    // ── Update ────────────────────────────────────────────────────────────
    suspend fun updateProduct(product: Product) =
        productDao.updateProduct(product.toEntity())

    // ── Delete ────────────────────────────────────────────────────────────
    suspend fun deleteProduct(product: Product) =
        productDao.deleteProduct(product.toEntity())

    suspend fun deleteProductById(productId: String) =
        productDao.deleteProductById(productId)

    // ── Seed mock data (idempotent due to REPLACE strategy) ───────────────
    suspend fun syncProducts() {
        val mockProducts = MockDataSource.getProducts()
        productDao.insertAll(mockProducts.map { it.toEntity() })
    }
}
