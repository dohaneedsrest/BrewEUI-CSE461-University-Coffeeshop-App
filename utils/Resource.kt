package com.eui.coffeeshop.utils

/**
 * Resource<T> — sealed class that wraps all async results (DB / network).
 *
 * Used by every ViewModel to expose state to the UI layer:
 *   Resource.Loading  → show spinner
 *   Resource.Success  → show data
 *   Resource.Error    → show error message + retry option
 *
 * The UI never needs to know whether data came from Room or a mock source.
 */
sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<T>(val data: T?) : Resource<T>()
    data class Error(val message: String?) : Resource<Nothing>()
}
