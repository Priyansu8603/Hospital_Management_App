package com.example.hospital_management_app.UI.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.hospital_management_app.R
import com.example.hospital_management_app.databinding.FragmentOnboardingTwoBinding


class OnboardingTwo : Fragment() {

    private var _binding : FragmentOnboardingTwoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOnboardingTwoBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.GetStartedBtn.setOnClickListener{
            findNavController().navigate(R.id.action_onboardingTwo_to_onboardingThree)
        }

        binding.skipBtn.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingTwo_to_login_fragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}