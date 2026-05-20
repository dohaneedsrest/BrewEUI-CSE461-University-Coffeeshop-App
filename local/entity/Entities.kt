package com.eui.coffeeshop.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ─────────────────────────────────────────────
// Product Entity
// ─────────────────────────────────────────────
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val imageUrl: String,
    val description: String,
    val isAvailable: Boolean,
    val rating: Double? = null,
    val ratingCount: Int? = null
)

// ─────────────────────────────────────────────
// User Entity
// ─────────────────────────────────────────────
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val fullName: String,
    val email: String,
    val password: String,
    val role: String = "student"
)

// ─────────────────────────────────────────────
// Order Entity
// ─────────────────────────────────────────────
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val userId: String,
    val status: String,
    val totalPrice: Double,
    val timestamp: Long
)

// ─────────────────────────────────────────────
// OrderItem Entity  (child of Order via FK)
// ─────────────────────────────────────────────
@Entity(
    tableName = "order_items",
    foreignKeys = [ForeignKey(
        entity = OrderEntity::class,
        parentColumns = ["orderId"],
        childColumns = ["orderId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("orderId")]
)
data class OrderItemEntity(
    @PrimaryKey val itemId: String,
    val orderId: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double
)

// ─────────────────────────────────────────────
// Cart Entity
// ─────────────────────────────────────────────
@Entity(tableName = "cart")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val quantity: Int,
    val category: String = ""
)
