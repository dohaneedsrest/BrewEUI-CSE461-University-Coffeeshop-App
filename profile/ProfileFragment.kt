package com.eui.coffeeshop.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eui.coffeeshop.R
import com.eui.coffeeshop.databinding.FragmentProfileBinding
import com.eui.coffeeshop.viewmodel.AuthViewModel
import com.eui.coffeeshop.viewmodel.ViewModelFactory

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomNav()
        setupMenuItems()
        binding.btnLogout.setOnClickListener {
            authViewModel.logout()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }

    private fun setupBottomNav() {
        val nav = binding.bottomNavProfile
        nav.ivNavProfile.setColorFilter(requireContext().getColor(R.color.color_nav_active))
        nav.tvNavProfile.visibility = View.VISIBLE
        nav.tvNavProfile.setTextColor(requireContext().getColor(R.color.color_nav_active))
        nav.navHome.setOnClickListener { findNavController().navigate(R.id.homeFragment) }
        nav.navCart.setOnClickListener { findNavController().navigate(R.id.cartFragment) }
        nav.navOrders.setOnClickListener { findNavController().navigate(R.id.orderHistoryFragment) }
    }

    private fun setupMenuItems() {
        binding.menuEditProfile.setOnClickListener { /* future */ }
        binding.menuPaymentMethods.setOnClickListener { /* future */ }
        binding.menuAddresses.setOnClickListener { /* future */ }
        binding.menuNotifications.setOnClickListener { /* future */ }
        binding.menuHelp.setOnClickListener { /* future */ }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
