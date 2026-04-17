package com.example.stock_market_design.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.hospital_management_app.R
import com.example.hospital_management_app.UI.Activity.MainActivity
import com.example.hospital_management_app.databinding.FragmentEnterOtpBinding

class EnterOtp : Fragment() {

    private var _binding: FragmentEnterOtpBinding? = null
    private val binding get() = _binding!!
    private var phoneNumber: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEnterOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: EnterOtpArgs by navArgs()
        phoneNumber = args.number

        binding.progressBar2.visibility = View.GONE
        binding.Didntrecieve.visibility = View.VISIBLE
        binding.resendotp.visibility = View.VISIBLE
        binding.btnVerify.isEnabled = true

        Toast.makeText(requireContext(), "Demo: Enter any 6-digit OTP", Toast.LENGTH_LONG).show()

        binding.btnVerify.setOnClickListener {
            val otp = binding.otpView.text.toString()
            if (otp.isEmpty() || otp.length < 6) {
                Toast.makeText(requireContext(), "Please Enter 6 digit OTP", Toast.LENGTH_LONG).show()
            } else {
                // Demo: accept any OTP and go to MainActivity
                val intent = Intent(activity, MainActivity::class.java)
                activity?.startActivity(intent)
                activity?.finish()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
