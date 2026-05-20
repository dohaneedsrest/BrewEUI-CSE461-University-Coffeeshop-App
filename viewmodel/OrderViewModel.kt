package com.eui.coffeeshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eui.coffeeshop.data.repository.AuthRepository
import com.eui.coffeeshop.data.repository.CartRepository
import com.eui.coffeeshop.data.repository.OrderRepository
import com.eui.coffeeshop.domain.model.CartItem
import com.eui.coffeeshop.domain.model.Order
import com.eui.coffeeshop.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // All orders for the order history screen
    val orderHistory: StateFlow<List<Order>> = orderRepository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently viewed order (for OrderStatusFragment)
    private val _currentOrderStatus = MutableStateFlow<Resource<Order>>(Resource.Loading)
    val currentOrderStatus: StateFlow<Resource<Order>> = _currentOrderStatus

    /**
     * SharedFlow (not StateFlow) for one-shot navigation events.
     * SharedFlow has no initial value — it only fires when explicitly emitted.
     * This prevents the event from re-firing on screen rotation.
     */
    private val _orderPlacedEvent = MutableSharedFlow<String>()
    val orderPlacedEvent: SharedFlow<String> = _orderPlacedEvent

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent: SharedFlow<String> = _errorEvent

    // ── Read ──────────────────────────────────
    fun loadOrderById(orderId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.getOrderById(orderId).collect { order ->
                _currentOrderStatus.value = if (order != null) {
                    Resource.Success(order)
                } else {
                    Resource.Error("Order not found")
                }
            }
        }
    }

    // ── Write: place order ────────────────────
    /**
     * Atomically:
     *  1. Writes order + all items to Room
     *  2. Clears the cart
     *  3. Emits the new orderId as a navigation event
     *
     * All DB operations run on Dispatchers.IO (background thread).
     */
    fun placeOrder(cartItems: List<CartItem>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = authRepository.getCurrentUserId()
                val orderId = orderRepository.placeOrder(cartItems, userId)
                cartRepository.clearCart()
                _orderPlacedEvent.emit(orderId)
            } catch (e: Exception) {
                _errorEvent.emit("Failed to place order: ${e.message}")
            }
        }
    }

    // ── Write: update status ──────────────────
    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.updateOrderStatus(orderId, status)
        }
    }

    // ── Write: delete order ───────────────────
    fun deleteOrder(orderId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.deleteOrder(orderId)
        }
    }

    /** Simulates order status progression (replace with Firestore listener in production). */
    fun simulateOrderProgress(orderId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(3000)
            orderRepository.updateOrderStatus(orderId, OrderRepository.ORDER_STATUS_PREPARING)
            delay(5000)
            orderRepository.updateOrderStatus(orderId, OrderRepository.ORDER_STATUS_READY)
        }
    }
}
