package com.eui.coffeeshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eui.coffeeshop.data.repository.ProductRepository
import com.eui.coffeeshop.domain.model.Product
import com.eui.coffeeshop.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ProductViewModel — survives configuration changes (screen rotation).
 *
 * When the screen rotates, the Fragment is destroyed and recreated,
 * but the ViewModel instance is retained by the ViewModelStore.
 * The Fragment re-collects from the same StateFlow and gets the last
 * emitted value instantly — no flicker, no re-fetch.
 *
 * Constructor receives ProductRepository (injected via ViewModelFactory),
 * so the ViewModel has zero knowledge of Android framework / Context.
 */
class ProductViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Loading)
    val products: StateFlow<Resource<List<Product>>> = _products

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _products.value = Resource.Loading
                // Seed mock data into Room (idempotent — REPLACE strategy)
                productRepository.syncProducts()
                // Collect Room's Flow — emits whenever the products table changes
                productRepository.getAllProducts().collect { list ->
                    _products.value = Resource.Success(list)
                }
            } catch (e: Exception) {
                _products.value = Resource.Error(e.message ?: "Failed to load products")
            }
        }
    }

    // ── Admin / test helpers ──────────────────
    fun insertProduct(product: Product) = viewModelScope.launch(Dispatchers.IO) {
        productRepository.insertProduct(product)
    }

    fun updateProduct(product: Product) = viewModelScope.launch(Dispatchers.IO) {
        productRepository.updateProduct(product)
    }

    fun deleteProduct(product: Product) = viewModelScope.launch(Dispatchers.IO) {
        productRepository.deleteProduct(product)
    }
}
