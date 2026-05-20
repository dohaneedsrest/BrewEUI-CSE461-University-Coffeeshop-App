package com.eui.coffeeshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eui.coffeeshop.data.repository.CartRepository
import com.eui.coffeeshop.domain.model.CartItem
import com.eui.coffeeshop.domain.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {

    /**
     * stateIn converts Room's cold Flow into a hot StateFlow:
     *  - WhileSubscribed(5000): keeps the upstream active for 5 s after last collector
     *    disappears — prevents re-querying Room on brief config changes (screen rotation).
     *  - initialValue: what the UI sees before the first DB emission.
     */
    val cartItems: StateFlow<List<CartItem>> = cartRepository.getCartItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItemCount: StateFlow<Int> = cartRepository.getCartItemCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalPrice: StateFlow<Double> = cartItems
        .map { items -> items.sumOf { it.subtotal } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // ── Write operations (dispatched to IO thread) ────────────────────────
    fun addItem(product: Product) = viewModelScope.launch(Dispatchers.IO) {
        cartRepository.addItem(product)
    }

    fun removeItem(cartItem: CartItem) = viewModelScope.launch(Dispatchers.IO) {
        cartRepository.removeItem(cartItem)
    }

    fun increaseQuantity(cartItem: CartItem) = viewModelScope.launch(Dispatchers.IO) {
        cartRepository.updateQuantity(cartItem.productId, cartItem.quantity + 1)
    }

    fun decreaseQuantity(cartItem: CartItem) = viewModelScope.launch(Dispatchers.IO) {
        if (cartItem.quantity <= 1) {
            cartRepository.removeItem(cartItem)
        } else {
            cartRepository.updateQuantity(cartItem.productId, cartItem.quantity - 1)
        }
    }

    fun updateItem(cartItem: CartItem) = viewModelScope.launch(Dispatchers.IO) {
        cartRepository.updateItem(cartItem)
    }

    fun clearCart() = viewModelScope.launch(Dispatchers.IO) {
        cartRepository.clearCart()
    }
}
