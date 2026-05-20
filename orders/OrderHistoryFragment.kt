package com.eui.coffeeshop.ui.orders

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
import com.eui.coffeeshop.databinding.FragmentOrderHistoryBinding
import com.eui.coffeeshop.domain.model.Order
import com.eui.coffeeshop.viewmodel.OrderViewModel
import com.eui.coffeeshop.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class OrderHistoryFragment : Fragment() {

    private var _binding: FragmentOrderHistoryBinding? = null
    private val binding get() = _binding!!

    private val orderViewModel: OrderViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    private lateinit var orderAdapter: OrderAdapter
    private var allOrders: List<Order> = emptyList()
    private var activeFilter = "All"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomNav()
        setupRecyclerView()
        setupFilterChips()
        observeOrders()
        binding.btnBrowseMenu.setOnClickListener { findNavController().navigate(R.id.homeFragment) }
    }

    private fun setupBottomNav() {
        val nav = binding.bottomNavHistory
        nav.ivNavOrders.setColorFilter(requireContext().getColor(R.color.color_nav_active))
        nav.tvNavOrders.visibility = View.VISIBLE
        nav.tvNavOrders.setTextColor(requireContext().getColor(R.color.color_nav_active))
        nav.navHome.setOnClickListener { findNavController().navigate(R.id.homeFragment) }
        nav.navCart.setOnClickListener { findNavController().navigate(R.id.cartFragment) }
        nav.navProfile.setOnClickListener { findNavController().navigate(R.id.profileFragment) }
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter { order ->
            val action = OrderHistoryFragmentDirections
                .actionOrderHistoryFragmentToOrderStatusFragment(order.orderId)
            findNavController().navigate(action)
        }
        binding.rvOrders.apply {
            adapter = orderAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupFilterChips() {
        val chips = mapOf(
            binding.chipFilterAll       to "All",
            binding.chipFilterPreparing to "Preparing",
            binding.chipFilterCompleted to "Completed",
            binding.chipFilterCancelled to "Cancelled"
        )
        chips.forEach { (chip, filter) ->
            chip.setOnClickListener {
                activeFilter = filter
                chips.keys.forEach { c ->
                    c.setBackgroundResource(R.drawable.bg_chip_unselected)
                    c.setTextColor(requireContext().getColor(R.color.color_primary))
                }
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(requireContext().getColor(R.color.color_on_primary))
                applyFilter()
            }
        }
    }

    private fun applyFilter() {
        val filtered = if (activeFilter == "All") allOrders
        else allOrders.filter {
            it.status == activeFilter ||
            (activeFilter == "Completed" && it.status == "Ready for Pickup")
        }
        orderAdapter.submitList(filtered)
        binding.layoutEmptyOrders.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.rvOrders.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun observeOrders() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderViewModel.orderHistory.collect { orders ->
                    allOrders = orders
                    applyFilter()
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.rvOrders.adapter = null
        super.onDestroyView()
        _binding = null
    }
}
