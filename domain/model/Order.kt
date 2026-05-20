package com.eui.coffeeshop.domain.model

data class Order(
    val orderId: String,
    val userId: String,
    val status: String,
    val totalPrice: Double,
    val timestamp: Long,
    val items: List<OrderItem> = emptyList()
)

data class OrderItem(
    val itemId: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double
)

data class CartItem(
    val productId: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    var quantity: Int = 1,
    val category: String = ""
) {
    val subtotal: Double get() = price * quantity
}
