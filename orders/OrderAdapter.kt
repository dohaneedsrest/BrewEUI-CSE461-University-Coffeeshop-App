package com.eui.coffeeshop.ui.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eui.coffeeshop.R
import com.eui.coffeeshop.databinding.ItemOrderCardBinding
import com.eui.coffeeshop.domain.model.Order
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(private val onOrderClick: (Order) -> Unit) :
    ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        OrderViewHolder(ItemOrderCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class OrderViewHolder(private val b: ItemOrderCardBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(order: Order) {
            b.tvOrderIdLabel.text = b.root.context.getString(
                R.string.order_id_label, order.orderId.takeLast(7).uppercase())
            b.tvOrderTotal.text = b.root.context.getString(R.string.price_format, order.totalPrice)
            b.tvOrderTimestamp.text = formatRelativeTime(order.timestamp)

            // Item summary: "Cappuccino × 2, Cold Brew × 1"
            b.tvOrderItemSummary.text = order.items.joinToString(", ") {
                "${it.productName} × ${it.quantity}"
            }.ifEmpty { "${order.items.size} items" }

            // Status badge color
            b.tvOrderStatusBadge.text = order.status
            val badgeColor = when (order.status) {
                "Pending" -> b.root.context.getColor(R.color.color_status_pending)
                "Preparing" -> b.root.context.getColor(R.color.color_status_preparing)
                "Ready for Pickup" -> b.root.context.getColor(R.color.color_status_ready)
                "Completed" -> b.root.context.getColor(R.color.color_success)
                "Cancelled" -> b.root.context.getColor(R.color.color_status_cancelled)
                else -> b.root.context.getColor(R.color.color_on_background_secondary)
            }
            b.tvOrderStatusBadge.setBackgroundColor(badgeColor)

            b.btnViewDetails.setOnClickListener { onOrderClick(order) }
            b.root.setOnClickListener { onOrderClick(order) }
        }

        private fun formatRelativeTime(timestamp: Long): String {
            val diff = System.currentTimeMillis() - timestamp
            return when {
                diff < 60_000 -> "Just now"
                diff < 3_600_000 -> "${diff / 60_000} min ago"
                diff < 86_400_000 -> "${diff / 3_600_000} hours ago"
                else -> "${diff / 86_400_000} day${if (diff / 86_400_000 > 1) "s" else ""} ago"
            }
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(o: Order, n: Order) = o.orderId == n.orderId
        override fun areContentsTheSame(o: Order, n: Order) = o == n
    }
}
