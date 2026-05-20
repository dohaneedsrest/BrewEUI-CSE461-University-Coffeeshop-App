package com.eui.coffeeshop.data.local.dao

import androidx.room.*
import com.eui.coffeeshop.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // ── Fetch All (reactive) ─────────────────
    /** Flow emits a new list every time the products table changes. */
    @Query("SELECT * FROM products ORDER BY category, name")
    fun getAllProducts(): Flow<List<ProductEntity>>

    // ── Fetch by Category (reactive) ─────────
    @Query("SELECT * FROM products WHERE category = :category ORDER BY name")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    // ── Fetch Single by ID (one-shot) ────────
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): ProductEntity?

    // ── Fetch Single by ID (reactive) ────────
    @Query("SELECT * FROM products WHERE id = :productId")
    fun observeProductById(productId: String): Flow<ProductEntity?>

    // ── Insert single ────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    // ── Insert all (bulk seed / sync) ────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    // ── Update ───────────────────────────────
    @Update
    suspend fun updateProduct(product: ProductEntity)

    // ── Delete single ────────────────────────
    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    // ── Delete by ID ─────────────────────────
    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProductById(productId: String)

    // ── Clear all ────────────────────────────
    @Query("DELETE FROM products")
    suspend fun clearAll()
}
