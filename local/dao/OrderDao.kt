package com.eui.coffeeshop.data.local.dao

import androidx.room.*
import com.eui.coffeeshop.data.local.entity.OrderEntity
import com.eui.coffeeshop.data.local.entity.OrderItemEntity
import com.eui.coffeeshop.data.local.entity.OrderWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    // ── Fetch All Orders with Items (reactive) ─
    /**
     * @Transaction is required with @Relation to prevent partial reads
     * if the database changes mid-query (atomicity guarantee).
     */
    @Transaction
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getOrdersWithItems(): Flow<List<OrderWithItems>>

    // ── Fetch Orders by User (reactive) ───────
    @Transaction
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY timestamp DESC")
    fun getOrdersByUser(userId: String): Flow<List<OrderWithItems>>

    // ── Fetch Single Order (reactive) ─────────
    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    fun getOrderById(orderId: String): Flow<OrderWithItems?>

    // ── Fetch Single Order (one-shot) ─────────
    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    suspend fun getOrderByIdOnce(orderId: String): OrderWithItems?

    // ── Insert Order ──────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    // ── Insert Order Items ────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    // ── Update Order Status ───────────────────
    @Query("UPDATE orders SET status = :status WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)

    // ── Update full Order ─────────────────────
    @Update
    suspend fun updateOrder(order: OrderEntity)

    // ── Delete single Order ───────────────────
    @Delete
    suspend fun deleteOrder(order: OrderEntity)

    // ── Delete by ID (cascade deletes items) ──
    @Query("DELETE FROM orders WHERE orderId = :orderId")
    suspend fun deleteOrderById(orderId: String)

    // ── Clear all ────────────────────────────
    @Query("DELETE FROM orders")
    suspend fun clearAll()
}
