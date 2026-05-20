package com.eui.coffeeshop.data.local.entity

import com.eui.coffeeshop.domain.model.CartItem
import com.eui.coffeeshop.domain.model.Order
import com.eui.coffeeshop.domain.model.OrderItem
import com.eui.coffeeshop.domain.model.Product
import com.eui.coffeeshop.domain.model.User

// ── Product ──────────────────────────────────
fun ProductEntity.toDomain(): Product = Product(
    id = id, name = name, price = price, category = category,
    imageUrl = imageUrl, description = description, isAvailable = isAvailable,
    rating = rating, ratingCount = ratingCount
)

fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id, name = name, price = price, category = category,
    imageUrl = imageUrl, description = description, isAvailable = isAvailable,
    rating = rating, ratingCount = ratingCount
)

// ── Cart ─────────────────────────────────────
fun CartItemEntity.toDomain(): CartItem = CartItem(
    productId = productId, name = name, price = price,
    imageUrl = imageUrl, quantity = quantity, category = category
)

fun CartItem.toEntity(): CartItemEntity = CartItemEntity(
    productId = productId, name = name, price = price,
    imageUrl = imageUrl, quantity = quantity, category = category
)

// ── Order ────────────────────────────────────
fun OrderWithItems.toDomain(): Order = Order(
    orderId = order.orderId, userId = order.userId, status = order.status,
    totalPrice = order.totalPrice, timestamp = order.timestamp,
    items = items.map { it.toDomain() }
)

fun OrderItemEntity.toDomain(): OrderItem = OrderItem(
    itemId = itemId, productId = productId, productName = productName,
    quantity = quantity, unitPrice = unitPrice
)

// ── User ─────────────────────────────────────
fun UserEntity.toDomain(): User = User(
    userId = userId, email = email, fullName = fullName, createdAt = createdAt
)

fun User.toEntity(): UserEntity = UserEntity(
    userId = userId, email = email, fullName = fullName, createdAt = createdAt
)
