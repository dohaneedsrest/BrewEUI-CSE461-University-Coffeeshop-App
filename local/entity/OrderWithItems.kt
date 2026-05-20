package com.eui.coffeeshop.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * OrderWithItems — Room relation that joins OrderEntity with its OrderItemEntity children.
 * Used with @Transaction in OrderDao to fetch an order and all its line items atomically.
 */
data class OrderWithItems(
    @Embedded val order: OrderEntity,
    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId"
    )
    val items: List<OrderItemEntity>
)
