package com.eui.coffeeshop.domain.model

/**
 * Product — domain model used across all app layers.
 * Matches the BrewEUI product catalogue including rating data.
 */
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,       // "Coffee" | "Tea" | "Cold Drinks" | "Food" | "Snacks"
    val imageUrl: String,
    val description: String,
    val isAvailable: Boolean = true,
    val rating: Double? = null,
    val ratingCount: Int? = null
)
