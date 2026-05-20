package com.eui.coffeeshop.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eui.coffeeshop.R
import com.eui.coffeeshop.databinding.FragmentCartBinding
import com.eui.coffeeshop.viewmodel.CartViewModel
import com.eui.coffeeshop.viewmodel.OrderViewModel
import com.eui.coffeeshop.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val factory by lazy { ViewModelFactory(requireContext()) }
    private val cartViewModel: CartViewModel by viewModels { factory }
    private val orderViewModel: OrderViewModel by viewModels { factory }

    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomNav()
        setupRecyclerView()
        observeCart()
        observeOrderEvents()
        binding.btnBrowseProducts.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_homeFragment)
        }
        binding.btnPlaceOrder.setOnClickListener {
            val items = cartViewModel.cartItems.value
            if (items.isEmpty()) {
                Snackbar.make(binding.root, getString(R.string.cart_empty_title), Snackbar.LENGTH_SHORT).show()
            } else {
                orderViewModel.placeOrder(items)
            }
        }
    }

    private fun setupBottomNav() {
        val nav = binding.bottomNav
        nav.ivNavCart.setColorFilter(requireContext().getColor(R.color.color_nav_active))
        nav.tvNavCart.visibility = View.VISIBLE
        nav.tvNavCart.setTextColor(requireContext().getColor(R.color.color_nav_active))
        nav.navHome.setOnClickListener { findNavController().navigate(R.id.action_cartFragment_to_homeFragment) }
        nav.navOrders.setOnClickListener { findNavController().navigate(R.id.orderHistoryFragment) }
        nav.navProfile.setOnClickListener { findNavController().navigate(R.id.profileFragment) }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onIncrease = { cartViewModel.increaseQuantity(it) },
            onDecrease = { cartViewModel.decreaseQuantity(it) },
            onRemove   = { cartViewModel.removeItem(it) }
        )
        binding.rvCartItems.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }
    }

    private fun observeCart() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    cartViewModel.cartItems.collect { items ->
                        cartAdapter.submitList(items)
                        if (items.isEmpty()) {
                            binding.layoutCartEmpty.visibility = View.VISIBLE
                            binding.scrollCartContent.visibility = View.GONE
                            binding.layoutCartBadge.visibility = View.GONE
                        } else {
                            binding.layoutCartEmpty.visibility = View.GONE
                            binding.scrollCartContent.visibility = View.VISIBLE
                            binding.layoutCartBadge.visibility = View.VISIBLE
                            binding.tvCartCount.text = items.size.toString()
                        }
                    }
                }
                launch {
                    cartViewModel.totalPrice.collect { total ->
                        val formatted = getString(R.string.price_format, total)
                        binding.tvSubtotal.text = formatted
                        binding.tvCartTotal.text = formatted
                    }
                }
            }
        }
    }

    private fun observeOrderEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    orderViewModel.orderPlacedEvent.collect { orderId ->
                        val action = CartFragmentDirections.actionCartFragmentToOrderStatusFragment(orderId)
                        findNavController().navigate(action)
                    }
                }
                launch {
                    orderViewModel.errorEvent.collect { message ->
                        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.rvCartItems.adapter = null
        super.onDestroyView()
        _binding = null
    }
}
