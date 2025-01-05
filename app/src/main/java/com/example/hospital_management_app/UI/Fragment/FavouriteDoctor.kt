package com.example.hospital_management_app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hospital_management_app.UI.Adapter.FavouriteDoctorAdapter
import com.example.hospital_management_app.UI.Fragment.HomeFragmentDirections
import com.example.hospital_management_app.ViewModel.FavouriteDoctorViewModel
import com.example.hospital_management_app.databinding.FragmentFavouriteDoctorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouriteDoctorFragment : Fragment() {

    private var _binding: FragmentFavouriteDoctorBinding? = null
    private val binding get() = _binding!!

    // Using Hilt's way of obtaining the ViewModel instance
    private val favouriteDoctorViewModel: FavouriteDoctorViewModel by viewModels()

    private lateinit var favouriteDoctorAdapter: FavouriteDoctorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouriteDoctorBinding.inflate(inflater, container, false)
        favouriteDoctorAdapter = FavouriteDoctorAdapter(::onDoctorClick)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupFavouriteDoctorRv()
        bindObservers()

        // Assuming you have a user ID available (replace with the actual user ID source)
        val userId = "currentUserId" // replace with actual userId
        favouriteDoctorViewModel.loadFavouriteDoctors()
    }

    private fun setupListeners() {
        binding.searchbarFavouriteFrag.setOnClickListener {
            findNavController().navigate(R.id.action_favouriteDoctor_to_doctorDetail)
        }

        binding.backBtnFavDocLyt.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun bindObservers() {
        // Observe changes in favouriteDoctors list from ViewModel
        favouriteDoctorViewModel.favouriteDoctors.observe(viewLifecycleOwner) { doctors ->
            binding.favoritePB.visibility = View.GONE // Hide ProgressBar

            // Submit list to adapter if not null or empty
            if (doctors != null && doctors.isNotEmpty()) {
                favouriteDoctorAdapter.differ.submitList(doctors)
            } else {
                Toast.makeText(requireContext(), "No favourite doctors found.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // When a doctor is clicked
    private fun onDoctorClick(doctorId: String?) {
        doctorId?.let {
            Log.d("onDoctorClick", "Tapped Doctor ID: $doctorId")
            val action = FavouriteDoctorFragmentDirections.actionFavouriteDoctorToDoctorDetail(it)
            findNavController().navigate(action)
        } ?: run {
            Toast.makeText(requireContext(), "Doctor ID is missing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupFavouriteDoctorRv() {
        favouriteDoctorAdapter = FavouriteDoctorAdapter(::onDoctorClick)
        binding.favouriteDoctorRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = favouriteDoctorAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

