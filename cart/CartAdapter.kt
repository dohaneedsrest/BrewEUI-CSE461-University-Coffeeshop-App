package com.eui.coffeeshop.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eui.coffeeshop.R
import com.eui.coffeeshop.databinding.ItemCartBinding
import com.eui.coffeeshop.domain.model.CartItem

class CartAdapter(
    private val onIncrease: (CartItem) -> Unit,
    private val onDecrease: (CartItem) -> Unit,
    private val onRemove: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CartViewHolder(ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class CartViewHolder(private val b: ItemCartBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bind(item: CartItem) {
            b.tvCartItemName.text = item.name
            b.tvCartItemCategory.text = item.category
            b.tvCartItemPrice.text = b.root.context.getString(R.string.price_format, item.subtotal)
            b.tvQuantity.text = item.quantity.toString()
            Glide.with(b.ivCartItemImage)
                .load(item.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .centerCrop()
                .into(b.ivCartItemImage)
            b.btnIncreaseQty.setOnClickListener { onIncrease(item) }
            b.btnDecreaseQty.setOnClickListener { onDecrease(item) }
            b.btnRemoveItem.setOnClickListener { onRemove(item) }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(o: CartItem, n: CartItem) = o.productId == n.productId
        override fun areContentsTheSame(o: CartItem, n: CartItem) = o == n
    }
}
