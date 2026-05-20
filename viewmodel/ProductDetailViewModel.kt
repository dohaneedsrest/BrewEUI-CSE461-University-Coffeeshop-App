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

class ProductDetailViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _productState = MutableStateFlow<Resource<Product>>(Resource.Loading)
    val productState: StateFlow<Resource<Product>> = _productState

    /**
     * Load a product by ID and keep observing Room for live updates.
     * If availability changes (e.g., from admin panel), the UI reflects it immediately.
     */
    fun loadProduct(productId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _productState.value = Resource.Loading
            try {
                productRepository.observeProduct(productId).collect { product ->
                    _productState.value = if (product != null) {
                        Resource.Success(product)
                    } else {
                        Resource.Error("Product not found")
                    }
                }
            } catch (e: Exception) {
                _productState.value = Resource.Error(e.message ?: "Error loading product")
            }
        }
    }
}
