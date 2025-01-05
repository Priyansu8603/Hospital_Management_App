package com.example.hospital_management_app.UI.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.hospital_management_app.R
import com.example.hospital_management_app.UI.Activity.MainActivity
import com.example.hospital_management_app.databinding.FragmentContinueWithNumberBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContinueWithNumber : Fragment() {

    private var _binding: FragmentContinueWithNumberBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentContinueWithNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        binding.progressBar.visibility = View.GONE

        // Check if user is already signed in
        if (auth.currentUser != null) {
            val intent = Intent(activity, MainActivity::class.java)
            activity?.startActivity(intent)
            activity?.finish()
        } else {
            binding.editTextPhone.requestFocus()
            binding.ccp.registerCarrierNumberEditText(binding.editTextPhone)
            binding.btngenerate.setOnClickListener {
                if (!binding.ccp.isValidFullNumber) {
                    Toast.makeText(activity, "Please enter a Valid Phone No...", Toast.LENGTH_LONG).show()
                } else {
                    binding.progressBar.visibility = View.VISIBLE
                    val number = binding.ccp.fullNumberWithPlus
                    val action = ContinueWithNumberDirections.actionContinueWithNumberToEnterOtp(number)
                    findNavController().navigate(action)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}