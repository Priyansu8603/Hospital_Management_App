package com.example.stock_market_design.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.hospital_management_app.R
import com.example.hospital_management_app.UI.Activity.MainActivity
import com.example.hospital_management_app.Utilities.Resource
import com.example.hospital_management_app.ViewModel.AuthPhoneNumberViewModel
import com.example.hospital_management_app.databinding.FragmentEnterOtpBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class EnterOtp : Fragment() {

    private var _binding: FragmentEnterOtpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthPhoneNumberViewModel by viewModels()
    private var phoneNumber: String? = null
    private var dialog: BottomSheetDialog? = null

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

        binding.btnVerify.isEnabled = false
        binding.progressBar2.visibility = View.VISIBLE
        binding.Didntrecieve.visibility = View.GONE
        binding.resendotp.visibility = View.GONE

        // Send verification code when the fragment is created
        viewModel.sendVerificationCode(phoneNumber!!, requireActivity())

        binding.btnVerify.setOnClickListener {
            val otp = binding.otpView.text.toString()
            if (otp.isEmpty() || otp.length < 6) {
                Toast.makeText(requireContext(), "Please Enter 6 digit OTP", Toast.LENGTH_LONG).show()
            } else {
                binding.progressBar2.visibility = View.VISIBLE
                val credential = PhoneAuthProvider.getCredential(viewModel.verificationId.value!!, otp)
                viewModel.signInWithPhoneAuthCredential(credential)
            }
        }

        binding.resendotp.setOnClickListener {
            viewModel.sendVerificationCode(phoneNumber!!, requireActivity())
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.otpSent.observe(viewLifecycleOwner) { otpSent ->
            if (otpSent) {
                startResendTimer()
                Toast.makeText(requireContext(), "OTP sent successfully", Toast.LENGTH_LONG).show()
                binding.otpView.requestFocus()
                binding.Didntrecieve.visibility = View.VISIBLE
                binding.resendotp.visibility = View.GONE
                binding.btnVerify.isEnabled = true
                binding.progressBar2.visibility = View.GONE
            }
        }

        viewModel.signInResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    val user = result.data
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val userDoc = FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(user!!.uid)
                                .get()
                                .await()

                            if (userDoc.exists()) {
                                withContext(Dispatchers.Main) {
                                    val intent = Intent(requireContext(), MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    createUserDialog(user.uid)
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    binding.btnVerify.visibility = View.VISIBLE
                    binding.progressBar2.visibility = View.GONE
                    binding.btnVerify.isEnabled = true

                    if (result.message == "User does not exist in Firestore") {
                        createUserDialog(viewModel.auth.currentUser!!.uid)
                    } else {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                    }
                }

                is Resource.Loading -> {
                    binding.btnVerify.visibility = View.VISIBLE
                    binding.progressBar2.visibility = View.VISIBLE
                    binding.btnVerify.isEnabled = false
                }

                is Resource.Unspecified -> {
                    binding.btnVerify.visibility = View.VISIBLE
                    binding.progressBar2.visibility = View.VISIBLE
                    binding.btnVerify.isEnabled = false
                }
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun createUserDialog(uid: String) {
        if (isAdded && !isRemoving && !requireActivity().isFinishing) {
            dialog = BottomSheetDialog(requireContext()).apply {
                setCancelable(false)
                setContentView(R.layout.username_dialog_layout)

                val continueButton =
                    findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.ContinueButton)!!
                val usernameEditText =
                    findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.dialog_usernameEditText)!!
                val progressBar = findViewById<android.widget.ProgressBar>(R.id.progressBar3)!!

                progressBar.visibility = View.GONE

                continueButton.setOnClickListener {
                    val name = usernameEditText.text.toString().trim()
                    if (name.isEmpty()) {
                        Toast.makeText(requireContext(), "Please enter a name", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        progressBar.visibility = View.VISIBLE
                        lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.createUser(name, uid, phoneNumber!!)
                            withContext(Dispatchers.Main) {
                                progressBar.visibility = View.GONE
                            }
                        }
                    }
                }
            }
            dialog?.show()
        }
    }

    private fun startResendTimer() {
        binding.resendotp.visibility = View.GONE
        binding.Didntrecieve.isEnabled = false

        lifecycleScope.launch {
            for (i in 60 downTo 1) {
                binding.Didntrecieve.text = "Resend OTP in $i seconds"
                binding.resendotp.isEnabled = false
                delay(1000)
            }
            binding.resendotp.visibility = View.VISIBLE
            binding.Didntrecieve.text = "Didn't receive the OTP?"
            binding.resendotp.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dialog?.dismiss()
        dialog = null
    }
}

