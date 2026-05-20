package com.eui.coffeeshop.ui.detail

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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.eui.coffeeshop.R
import com.eui.coffeeshop.databinding.FragmentProductDetailBinding
import com.eui.coffeeshop.domain.model.Product
import com.eui.coffeeshop.utils.Resource
import com.eui.coffeeshop.viewmodel.CartViewModel
import com.eui.coffeeshop.viewmodel.ProductDetailViewModel
import com.eui.coffeeshop.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val args: ProductDetailFragmentArgs by navArgs()

    private val factory by lazy { ViewModelFactory(requireContext()) }
    private val productDetailViewModel: ProductDetailViewModel by viewModels { factory }
    private val cartViewModel: CartViewModel by viewModels { factory }

    private var currentProduct: Product? = null
    private var quantity = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        productDetailViewModel.loadProduct(args.productId)

        binding.btnDecreaseQty.setOnClickListener {
            if (quantity > 1) { quantity--; updateQtyUI() }
        }
        binding.btnIncreaseQty.setOnClickListener {
            quantity++; updateQtyUI()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productDetailViewModel.productState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> binding.btnAddToCart.isEnabled = false
                        is Resource.Success -> {
                            binding.btnAddToCart.isEnabled = true
                            resource.data?.let { bindProduct(it) }
                        }
                        is Resource.Error -> Snackbar.make(binding.root,
                            resource.message ?: getString(R.string.error_loading_product),
                            Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun bindProduct(product: Product) {
        currentProduct = product
        Glide.with(this).load(product.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .centerCrop().into(binding.ivProductDetailImage)

        binding.tvDetailProductName.text = product.name
        binding.tvDetailCategory.text = product.category
        binding.tvDetailDescription.text = product.description
        updateQtyUI()

        val rating = product.rating ?: 4.0
        val stars = listOf(binding.tvStar1, binding.tvStar2, binding.tvStar3, binding.tvStar4, binding.tvStar5)
        val fullStars = rating.toInt()
        stars.forEachIndexed { i, tv ->
            tv.setTextColor(requireContext().getColor(
                if (i < fullStars) R.color.color_accent else android.R.color.darker_gray
            ))
        }
        binding.tvRatingCount.text = getString(R.string.rating_count, product.ratingCount ?: 0)

        if (product.isAvailable) {
            binding.tvDetailAvailabilityBadge.text = getString(R.string.badge_available)
            binding.tvDetailAvailabilityBadge.setBackgroundResource(R.drawable.bg_badge_available)
            binding.btnAddToCart.isEnabled = true
            binding.btnAddToCart.alpha = 1f
        } else {
            binding.tvDetailAvailabilityBadge.text = getString(R.string.badge_sold_out)
            binding.tvDetailAvailabilityBadge.setBackgroundResource(R.drawable.bg_badge_unavailable)
            binding.btnAddToCart.isEnabled = false
            binding.btnAddToCart.alpha = 0.5f
        }

        binding.btnAddToCart.setOnClickListener {
            currentProduct?.let { p ->
                repeat(quantity) { cartViewModel.addItem(p) }
                Snackbar.make(binding.root,
                    getString(R.string.snack_added_to_cart, p.name), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateQtyUI() {
        binding.tvQuantity.text = quantity.toString()
        val unitPrice = currentProduct?.price ?: 0.0
        binding.tvDetailPrice.text = getString(R.string.price_format, unitPrice)
        binding.tvDetailTotal.text = getString(R.string.price_format, unitPrice * quantity)
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
