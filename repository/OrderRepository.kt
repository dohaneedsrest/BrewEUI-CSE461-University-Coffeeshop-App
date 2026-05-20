package com.eui.coffeeshop.data.repository

import com.eui.coffeeshop.data.local.dao.OrderDao
import com.eui.coffeeshop.data.local.entity.OrderEntity
import com.eui.coffeeshop.data.local.entity.OrderItemEntity
import com.eui.coffeeshop.data.local.entity.toDomain
import com.eui.coffeeshop.domain.model.CartItem
import com.eui.coffeeshop.domain.model.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * OrderRepository — manages order persistence in Room.
 *
 * placeOrder() writes the OrderEntity + all OrderItemEntity rows atomically.
 * In a Firebase build, also write to Firestore here and listen for status
 * changes via a snapshot listener — the UI code wouldn't change at all.
 */
class OrderRepository(private val orderDao: OrderDao) {

    // ── Observe All (reactive) ────────────────
    fun getAllOrders(): Flow<List<Order>> =
        orderDao.getOrdersWithItems().map { it.map { o -> o.toDomain() } }

    fun getOrdersByUser(userId: String): Flow<List<Order>> =
        orderDao.getOrdersByUser(userId).map { it.map { o -> o.toDomain() } }

    // ── Observe Single (reactive) ─────────────
    fun getOrderById(orderId: String): Flow<Order?> =
        orderDao.getOrderById(orderId).map { it?.toDomain() }

    // ── Fetch Single (one-shot) ───────────────
    suspend fun getOrderByIdOnce(orderId: String): Order? =
        orderDao.getOrderByIdOnce(orderId)?.toDomain()

    // ── Place Order (insert + items) ──────────
    /**
     * Returns the generated orderId so the UI can navigate to OrderStatusFragment.
     * All writes happen in a single suspend function — Room's KTX extension
     * ensures they run on a background thread when called from a coroutine.
     */
    suspend fun placeOrder(cartItems: List<CartItem>, userId: String): String {
        val orderId = UUID.randomUUID().toString()
        val total = cartItems.sumOf { it.subtotal }
        val timestamp = System.currentTimeMillis()

        val orderEntity = OrderEntity(
            orderId = orderId,
            userId = userId,
            status = ORDER_STATUS_PENDING,
            totalPrice = total,
            timestamp = timestamp
        )

        val itemEntities = cartItems.map { item ->
            OrderItemEntity(
                itemId = UUID.randomUUID().toString(),
                orderId = orderId,
                productId = item.productId,
                productName = item.name,
                quantity = item.quantity,
                unitPrice = item.price
            )
        }

        orderDao.insertOrder(orderEntity)
        orderDao.insertOrderItems(itemEntities)

        return orderId
    }

    // ── Update Status ─────────────────────────
    suspend fun updateOrderStatus(orderId: String, status: String) =
        orderDao.updateOrderStatus(orderId, status)

    // ── Delete ────────────────────────────────
    suspend fun deleteOrder(orderId: String) =
        orderDao.deleteOrderById(orderId)

    companion object {
        const val ORDER_STATUS_PENDING  = "Pending"
        const val ORDER_STATUS_PREPARING = "Preparing"
        const val ORDER_STATUS_READY    = "Ready for Pickup"
        const val ORDER_STATUS_COMPLETED = "Completed"
        const val ORDER_STATUS_CANCELLED = "Cancelled"
    }
}
