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

class ContinueWithNumber : Fragment() {

    private var _binding: FragmentContinueWithNumberBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContinueWithNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = View.GONE
        binding.editTextPhone.requestFocus()
        binding.ccp.registerCarrierNumberEditText(binding.editTextPhone)

        binding.btngenerate.setOnClickListener {
            if (!binding.ccp.isValidFullNumber) {
                Toast.makeText(activity, "Please enter a Valid Phone No...", Toast.LENGTH_LONG).show()
            } else {
                // Demo: skip OTP, go directly to MainActivity
                val intent = Intent(activity, MainActivity::class.java)
                activity?.startActivity(intent)
                activity?.finish()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}