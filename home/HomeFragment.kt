package com.eui.coffeeshop.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.eui.coffeeshop.R
import com.eui.coffeeshop.databinding.FragmentHomeBinding
import com.eui.coffeeshop.domain.model.Product
import com.eui.coffeeshop.utils.Resource
import com.eui.coffeeshop.viewmodel.CartViewModel
import com.eui.coffeeshop.viewmodel.ProductViewModel
import com.eui.coffeeshop.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val factory by lazy { ViewModelFactory(requireContext()) }
    private val productViewModel: ProductViewModel by viewModels { factory }
    private val cartViewModel: CartViewModel by viewModels { factory }

    private lateinit var productAdapter: ProductAdapter
    private var allProducts: List<Product> = emptyList()
    private var currentCategory = "All"
    private var searchQuery = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupChipFilters()
        setupSearch()
        setupBottomNav()
        setupTopBarActions()
        binding.btnRetry.setOnClickListener { productViewModel.loadProducts() }
        observeProducts()
        observeCartCount()
    }

    private fun setupTopBarActions() {
        binding.ivCart.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_cartFragment) }
        binding.ivProfile.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_profileFragment) }
    }

    private fun setupBottomNav() {
        val nav = binding.bottomNav
        nav.ivNavHome.setColorFilter(requireContext().getColor(R.color.color_nav_active))
        nav.tvNavHome.visibility = View.VISIBLE
        nav.tvNavHome.setTextColor(requireContext().getColor(R.color.color_nav_active))
        nav.navHome.setOnClickListener { /* already here */ }
        nav.navCart.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_cartFragment) }
        nav.navOrders.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_orderHistoryFragment) }
        nav.navProfile.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_profileFragment) }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onProductClick = { product ->
                val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(product.id)
                findNavController().navigate(action)
            },
            onAddToCart = { product ->
                cartViewModel.addItem(product)
                Snackbar.make(binding.root, getString(R.string.snack_added_to_cart, product.name), Snackbar.LENGTH_SHORT).show()
            }
        )
        binding.rvProducts.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
        }
    }

    private fun setupChipFilters() {
        binding.chipGroupCategory.setOnCheckedStateChangeListener { _, checkedIds ->
            currentCategory = when {
                checkedIds.contains(R.id.chipCoffee)     -> "Coffee"
                checkedIds.contains(R.id.chipTea)        -> "Tea"
                checkedIds.contains(R.id.chipColdDrinks) -> "Cold Drinks"
                checkedIds.contains(R.id.chipFood)       -> "Food"
                checkedIds.contains(R.id.chipSnacks)     -> "Snacks"
                else -> "All"
            }
            applyFilters()
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s?.toString()?.trim() ?: ""
                applyFilters()
            }
        })
    }

    private fun applyFilters() {
        var filtered = if (currentCategory == "All") allProducts
        else allProducts.filter { it.category == currentCategory }
        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
            }
        }
        productAdapter.submitList(filtered)
        binding.tvItemCount.text = "${filtered.size} items"
    }

    private fun observeProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productViewModel.products.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBarHome.visibility = View.VISIBLE
                            binding.rvProducts.visibility = View.GONE
                            binding.layoutEmptyState.visibility = View.GONE
                            binding.layoutErrorState.visibility = View.GONE
                        }
                        is Resource.Success -> {
                            binding.progressBarHome.visibility = View.GONE
                            val products = resource.data ?: emptyList()
                            allProducts = products
                            if (products.isEmpty()) {
                                binding.layoutEmptyState.visibility = View.VISIBLE
                                binding.rvProducts.visibility = View.GONE
                            } else {
                                binding.layoutEmptyState.visibility = View.GONE
                                binding.rvProducts.visibility = View.VISIBLE
                                applyFilters()
                            }
                            binding.layoutErrorState.visibility = View.GONE
                        }
                        is Resource.Error -> {
                            binding.progressBarHome.visibility = View.GONE
                            binding.rvProducts.visibility = View.GONE
                            binding.layoutEmptyState.visibility = View.GONE
                            binding.layoutErrorState.visibility = View.VISIBLE
                            binding.tvErrorMessage.text = resource.message
                        }
                    }
                }
            }
        }
    }

    private fun observeCartCount() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.cartItemCount.collect { count ->
                    if (count > 0) {
                        binding.tvCartBadge.visibility = View.VISIBLE
                        binding.tvCartBadge.text = count.toString()
                    } else {
                        binding.tvCartBadge.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.rvProducts.adapter = null
        super.onDestroyView()
        _binding = null
    }
}
