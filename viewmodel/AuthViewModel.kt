package com.eui.coffeeshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eui.coffeeshop.data.repository.AuthRepository
import com.eui.coffeeshop.data.repository.UserRepository
import com.eui.coffeeshop.domain.model.User
import com.eui.coffeeshop.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * AuthViewModel — manages login, registration, and logout state.
 *
 * Now uses ViewModel (not AndroidViewModel) — pure constructor injection
 * via ViewModelFactory means no Android framework dependency here.
 *
 * On successful registration, the user profile is also saved to Room
 * via UserRepository so profile data can be queried locally.
 */
class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<String>?>(null)
    val loginState: StateFlow<Resource<String>?> = _loginState

    private val _registerState = MutableStateFlow<Resource<String>?>(null)
    val registerState: StateFlow<Resource<String>?> = _registerState

    // ── Queries ───────────────────────────────
    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()
    fun getCurrentUserId(): String = authRepository.getCurrentUserId()
    fun getCurrentUserName(): String = authRepository.getCurrentUserName()

    // ── Login ─────────────────────────────────
    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loginState.value = Resource.Loading
            val result = authRepository.login(email, password)
            _loginState.value = if (result.isSuccess) {
                Resource.Success(result.getOrNull())
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    // ── Register ──────────────────────────────
    fun register(email: String, password: String, fullName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _registerState.value = Resource.Loading
            val result = authRepository.register(email, password, fullName)
            if (result.isSuccess) {
                // Save user profile to Room for local querying
                val userId = result.getOrNull() ?: return@launch
                userRepository.saveUser(
                    User(userId = userId, email = email, fullName = fullName)
                )
                _registerState.value = Resource.Success(userId)
            } else {
                _registerState.value = Resource.Error(
                    result.exceptionOrNull()?.message ?: "Registration failed"
                )
            }
        }
    }

    // ── Logout ────────────────────────────────
    fun logout() {
        authRepository.logout()
    }

    // ── Reset state (after navigation) ────────
    fun resetLoginState() { _loginState.value = null }
    fun resetRegisterState() { _registerState.value = null }
}
