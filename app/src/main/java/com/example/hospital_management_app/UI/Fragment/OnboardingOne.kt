package com.example.hospital_management_app.UI.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.hospital_management_app.R
import com.example.hospital_management_app.databinding.FragmentOnboardingOneBinding


class OnboardingOne : Fragment() {

    private var _binding: FragmentOnboardingOneBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOnboardingOneBinding.inflate(layoutInflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.GetStartedBtn.setOnClickListener{
            findNavController().navigate(R.id.action_onboardingOne_to_onboardingTwo)
        }

        binding.skipBtn.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingOne_to_login_fragment)
        }
        // Handle the back press to close the app instead of navigating to the splash screen
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finishAffinity()
                }
            }
        )


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}