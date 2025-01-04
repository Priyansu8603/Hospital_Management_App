package com.example.hospital_management_app.UI.Fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.hospital_management_app.R
import com.example.hospital_management_app.UI.Activity.MainActivity
import com.example.hospital_management_app.databinding.FragmentSplashBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splash : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(inflater, container, false)


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Launch a coroutine in the lifecycleScope
        lifecycleScope.launch {
            // Delay for 3 seconds
            delay(3000)

            // Check if the user is already authenticated
            if (auth.currentUser != null) {
                // Navigate to the MainActivity if user is authenticated
                val intent = Intent(activity, MainActivity::class.java)
                activity?.startActivity(intent)
                activity?.finish()
            } else {
                // Navigate to the onboarding fragment if user is not authenticated
                findNavController().navigate(R.id.action_splash_fragment_to_onboardingOne)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}