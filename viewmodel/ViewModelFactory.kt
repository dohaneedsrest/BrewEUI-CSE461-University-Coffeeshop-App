package com.eui.coffeeshop.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eui.coffeeshop.data.local.AppDatabase
import com.eui.coffeeshop.data.repository.AuthRepository
import com.eui.coffeeshop.data.repository.CartRepository
import com.eui.coffeeshop.data.repository.OrderRepository
import com.eui.coffeeshop.data.repository.ProductRepository
import com.eui.coffeeshop.data.repository.UserRepository

/**
 * ViewModelFactory — creates ViewModels with constructor dependencies injected.
 *
 * Why this matters:
 *   ViewModels cannot have constructor parameters by default — the framework
 *   creates them via reflection. Without a factory, we're forced to use
 *   AndroidViewModel and call AppDatabase.getInstance() inside the ViewModel,
 *   which couples the ViewModel to the Android framework and makes unit testing hard.
 *
 *   This factory lets every ViewModel receive its Repository through the constructor
 *   (pure constructor injection) — the ViewModel never touches AppDatabase or Context.
 *
 * Usage in Fragment:
 *   private val viewModel: ProductViewModel by viewModels { ViewModelFactory(requireContext()) }
 */
class ViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val appDatabase = AppDatabase.getInstance(context)
    private val productRepo  = ProductRepository(appDatabase.productDao())
    private val cartRepo     = CartRepository(appDatabase.cartDao())
    private val orderRepo    = OrderRepository(appDatabase.orderDao())
    private val userRepo     = UserRepository(appDatabase.userDao())
    private val authRepo     = AuthRepository(context)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProductViewModel::class.java) ->
                ProductViewModel(productRepo) as T
            modelClass.isAssignableFrom(ProductDetailViewModel::class.java) ->
                ProductDetailViewModel(productRepo) as T
            modelClass.isAssignableFrom(CartViewModel::class.java) ->
                CartViewModel(cartRepo) as T
            modelClass.isAssignableFrom(OrderViewModel::class.java) ->
                OrderViewModel(orderRepo, cartRepo, authRepo) as T
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(authRepo, userRepo) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
