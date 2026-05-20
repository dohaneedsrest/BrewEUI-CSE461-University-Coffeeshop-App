package com.eui.coffeeshop.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eui.coffeeshop.R
import com.eui.coffeeshop.databinding.ItemProductCardBinding
import com.eui.coffeeshop.domain.model.Product

/**
 * ProductAdapter — ListAdapter with DiffUtil for the 2-column product grid.
 * DiffUtil compares old vs new lists on a background thread, dispatching
 * only changed items — keeps RecyclerView at ≥60fps even with large lists.
 */
class ProductAdapter(
    private val onProductClick: (Product) -> Unit,
    private val onAddToCart: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ProductViewHolder(ItemProductCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ProductViewHolder(private val binding: ItemProductCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvProductCategory.text = product.category
            binding.tvProductPrice.text = binding.root.context.getString(R.string.price_format, product.price)

            // Availability badge
            if (product.isAvailable) {
                binding.tvAvailabilityBadge.text = binding.root.context.getString(R.string.badge_available)
                binding.tvAvailabilityBadge.setBackgroundResource(R.drawable.bg_badge_available)
            } else {
                binding.tvAvailabilityBadge.text = binding.root.context.getString(R.string.badge_sold_out)
                binding.tvAvailabilityBadge.setBackgroundResource(R.drawable.bg_badge_unavailable)
            }

            // Glide: load image with placeholder
            Glide.with(binding.ivProductImage)
                .load(product.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .centerCrop()
                .into(binding.ivProductImage)

            // Add button: disable and dim if unavailable
            binding.btnAddToCart.isClickable = product.isAvailable
            binding.btnAddToCart.alpha = if (product.isAvailable) 1f else 0.4f

            binding.root.setOnClickListener { onProductClick(product) }
            binding.btnAddToCart.setOnClickListener { if (product.isAvailable) onAddToCart(product) }
        }
    }

    /**
     * DiffUtil.ItemCallback:
     * areItemsTheSame — checks identity (same entity by ID, avoids unnecessary moves)
     * areContentsTheSame — checks if visual data changed (data class equality checks all fields)
     */
    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(old: Product, new: Product) = old.id == new.id
        override fun areContentsTheSame(old: Product, new: Product) = old == new
    }
}
