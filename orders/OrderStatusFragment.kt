package com.eui.coffeeshop.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.eui.coffeeshop.R
import com.eui.coffeeshop.data.repository.OrderRepository
import com.eui.coffeeshop.databinding.FragmentOrderStatusBinding
import com.eui.coffeeshop.domain.model.Order
import com.eui.coffeeshop.utils.Resource
import com.eui.coffeeshop.viewmodel.OrderViewModel
import com.eui.coffeeshop.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OrderStatusFragment : Fragment() {

    private var _binding: FragmentOrderStatusBinding? = null
    private val binding get() = _binding!!
    private val args: OrderStatusFragmentArgs by navArgs()

    private val orderViewModel: OrderViewModel by viewModels {
        ViewModelFactory(requireContext())
    }
    private var itemsExpanded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentOrderStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvOrderIdTitle.text = getString(R.string.order_id_label, args.orderId.takeLast(7).uppercase())

        orderViewModel.loadOrderById(args.orderId)
        orderViewModel.simulateOrderProgress(args.orderId)

        binding.btnBackToHome.setOnClickListener {
            findNavController().navigate(R.id.action_orderStatusFragment_to_homeFragment)
        }
        binding.layoutToggleItems.setOnClickListener {
            itemsExpanded = !itemsExpanded
            binding.layoutOrderItems.visibility = if (itemsExpanded) View.VISIBLE else View.GONE
            binding.tvToggleIcon.text = if (itemsExpanded) "▲" else "▼"
        }
        binding.btnDismissNotification.setOnClickListener {
            binding.layoutNotificationBanner.visibility = View.GONE
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderViewModel.currentOrderStatus.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {}
                        is Resource.Success -> resource.data?.let { updateUI(it) }
                        is Resource.Error   -> {}
                    }
                }
            }
        }
    }

    private fun updateUI(order: Order) {
        binding.tvOrderTimestamp.text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            .format(Date(order.timestamp))
        binding.tvOrderDetailTotal.text = getString(R.string.price_format, order.totalPrice)

        binding.containerOrderItems.removeAllViews()
        order.items.forEach { item ->
            val tv = TextView(requireContext()).apply {
                text = "${item.productName} × ${item.quantity}  —  " +
                    getString(R.string.price_format, item.unitPrice * item.quantity)
                textSize = 14f
                setPadding(0, 4, 0, 4)
                setTextColor(requireContext().getColor(R.color.color_on_background))
            }
            binding.containerOrderItems.addView(tv)
        }

        val activeColor   = requireContext().getColor(R.color.color_primary)
        val completeColor = requireContext().getColor(R.color.color_success)
        val inactiveColor = requireContext().getColor(R.color.color_divider)

        when (order.status) {
            OrderRepository.ORDER_STATUS_PENDING -> {
                setStep(1, completeColor, "✓"); setStep(2, inactiveColor, "○")
                setStep(3, inactiveColor, "○"); setStep(4, inactiveColor, "○")
                setStepTextColor(2, false); setStepTextColor(3, false); setStepTextColor(4, false)
                binding.layoutEstimatedTime.visibility = View.GONE
            }
            OrderRepository.ORDER_STATUS_PREPARING -> {
                setStep(1, completeColor, "✓"); setStep(2, activeColor, "⚙")
                setStep(3, inactiveColor, "○"); setStep(4, inactiveColor, "○")
                setStepTextColor(2, true); setStepTextColor(3, false); setStepTextColor(4, false)
                binding.layoutEstimatedTime.visibility = View.VISIBLE
            }
            OrderRepository.ORDER_STATUS_READY -> {
                setStep(1, completeColor, "✓"); setStep(2, completeColor, "✓")
                setStep(3, activeColor, "★"); setStep(4, inactiveColor, "○")
                setStepTextColor(2, true); setStepTextColor(3, true); setStepTextColor(4, false)
                binding.layoutEstimatedTime.visibility = View.GONE
                binding.layoutNotificationBanner.visibility = View.GONE
            }
        }
    }

    private fun setStep(step: Int, color: Int, icon: String) {
        val (circle, iconView) = when (step) {
            1 -> binding.stepCircle1 to null
            2 -> binding.stepCircle2 to binding.tvStep2Icon
            3 -> binding.stepCircle3 to binding.tvStep3Icon
            4 -> binding.stepCircle4 to binding.tvStep4Icon
            else -> return
        }
        circle.setBackgroundColor(color)
        iconView?.text = icon
        iconView?.setTextColor(requireContext().getColor(R.color.color_on_primary))
    }

    private fun setStepTextColor(step: Int, active: Boolean) {
        val tv = when (step) {
            2 -> binding.tvStep2Label
            3 -> binding.tvStep3Label
            4 -> binding.tvStep4Label
            else -> null
        } ?: return
        tv.setTextColor(requireContext().getColor(
            if (active) R.color.color_on_background else R.color.color_on_background_secondary
        ))
        tv.textSize = if (active) 16f else 14f
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
