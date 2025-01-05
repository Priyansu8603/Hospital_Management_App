package com.example.hospital_management_app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.hospital_management_app.Utilities.Resource
import com.example.hospital_management_app.ViewModel.DoctorDetailViewModel
import com.example.hospital_management_app.databinding.FragmentDoctorDetailBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DoctorDetail : Fragment() {

    private var _binding: FragmentDoctorDetailBinding? = null
    private val binding get() = _binding!!
    private val doctorDetailViewModel by viewModels<DoctorDetailViewModel>()

    // Use SafeArgs to get the argument
    private val args: DoctorDetailArgs by navArgs()

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private var lastClickTime: Long = 0
    private val debounceDelay: Long = 600 // 600 milliseconds


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDoctorDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the doctorId from the arguments
        val doctorId = args.doctorId
        Log.d("DoctorDetail", "Doctor ID: $doctorId")

        setupInitialData(doctorId)

        binding.backBtnDocDetailLyt.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.heartFavourite.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > debounceDelay) {
                lastClickTime = currentTime

                lifecycleScope.launch(Dispatchers.IO) {
                    val isFavorite = doctorDetailViewModel.isFavorite(currentUserId!!, doctorId)
                    withContext(Dispatchers.Main) {
                        if (isFavorite) {
                            doctorDetailViewModel.removeFromFavorites(currentUserId, doctorId)
                        } else {
                            doctorDetailViewModel.addToFavorites(currentUserId, doctorId)
                        }
                    }

                }
            }
        }


    }

    private fun setupInitialData(doctorId: String) {
        // Fetch doctor details using the ViewModel
        viewLifecycleOwner.lifecycleScope.launch() {
            doctorDetailViewModel.fetchDoctorDetail(doctorId)
            val isFavorite = doctorDetailViewModel.isFavorite(currentUserId!!, doctorId)
            if(isFavorite){
                binding.heartFavourite.setImageResource(R.drawable.heart2)
            }
            else{
                binding.heartFavourite.setImageResource(R.drawable.heart1)
            }

        }
        bindObservers()
    }

    private fun bindObservers() {
        doctorDetailViewModel.doctorDetail.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Show loading state (e.g., progress bar)
                    binding.detailCardView.visibility = View.INVISIBLE
                    binding.cardViewShimmerView.visibility = View.VISIBLE
                    binding.cardViewShimmerView.startShimmer()
                }

                is Resource.Success -> {
                    // Update UI with doctor details
                    val doctor = resource.data
                    Glide.with(this).load(doctor?.image?.get(0)).into(binding.imageView)
                    binding.nameTxt.text = doctor?.name
                    binding.jobTxt.text = doctor?.specialization
                    binding.experienceTxt.text = doctor?.experienceYears.toString()
                    binding.pricingTxt.text = doctor?.pricePerHour.toString() + "/hr"
                    binding.ratingBar.rating = doctor?.rating.toString().toFloat()
                    binding.ongoingTxt.text = doctor?.ongoingPatients.toString()
                    binding.totalPatientTxt.text = doctor?.totalPatients.toString()
                    binding.ReviewTxt.text = doctor?.totalReviews.toString()
                    binding.ratingTxt.text = "( " + doctor?.rating.toString() + " )"
                    binding.descTxt.text = doctor?.description.toString()

                    binding.cardViewShimmerView.stopShimmer()
                    binding.cardViewShimmerView.visibility = View.GONE
                    binding.detailCardView.visibility = View.VISIBLE
                }

                is Resource.Error -> {
                    // Show error message

                }

                else -> Unit
            }
        }

        doctorDetailViewModel.favoriteStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Error -> {

                }

                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    Log.d("DoctorDetailStatusUpdate", "Favorite status updated: ${resource.data}")
                    updateFavorite(resource.data!!)
                }

                is Resource.Unspecified ->Unit
            }
        }

    }

    private fun updateFavorite(isFavorite: Boolean) {
        binding.heartFavourite.setImageResource(
            if (isFavorite) R.drawable.heart2 else R.drawable.heart1
        )
        val message = if (isFavorite) {
            "Added to favorites"
        } else {
            "Removed from favourites"
        }
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
