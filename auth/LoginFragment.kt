package com.eui.coffeeshop.ui.auth

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
import com.eui.coffeeshop.R
import com.eui.coffeeshop.databinding.FragmentLoginBinding
import com.eui.coffeeshop.utils.Resource
import com.eui.coffeeshop.viewmodel.AuthViewModel
import com.eui.coffeeshop.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(requireContext())
    }
    private var passwordVisible = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivTogglePassword.setOnClickListener {
            passwordVisible = !passwordVisible
            binding.etPassword.inputType = if (passwordVisible)
                android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            else android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass  = binding.etPassword.text.toString().trim()
            if (validateInputs(email, pass)) authViewModel.login(email, pass)
        }

        binding.btnGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.loginState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressBarLogin.visibility = View.VISIBLE
                            binding.btnLogin.isEnabled = false
                        }
                        is Resource.Success -> {
                            binding.progressBarLogin.visibility = View.GONE
                            authViewModel.resetLoginState()
                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        }
                        is Resource.Error -> {
                            binding.progressBarLogin.visibility = View.GONE
                            binding.btnLogin.isEnabled = true
                            Snackbar.make(binding.root, resource.message ?: getString(R.string.error_login_failed), Snackbar.LENGTH_LONG).show()
                        }
                        null -> Unit
                    }
                }
            }
        }
    }

    private fun validateInputs(email: String, pass: String): Boolean {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(binding.root, getString(R.string.error_email_invalid), Snackbar.LENGTH_SHORT).show()
            return false
        }
        if (pass.length < 6) {
            Snackbar.make(binding.root, getString(R.string.error_password_too_short), Snackbar.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
