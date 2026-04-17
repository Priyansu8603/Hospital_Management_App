package com.example.hospital_management_app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.hospital_management_app.Data.DoctorModel
import com.example.hospital_management_app.Data.DummyData
import com.example.hospital_management_app.Utilities.Resource
import com.example.hospital_management_app.ViewModel.DoctorDetailViewModel
import com.example.hospital_management_app.databinding.FragmentDoctorDetailBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DoctorDetail : Fragment() {

    private var _binding: FragmentDoctorDetailBinding? = null
    private val binding get() = _binding!!
    private val doctorDetailViewModel by viewModels<DoctorDetailViewModel>()
    private val args: DoctorDetailArgs by navArgs()
    private val currentUserId = "demo_user"
    private var lastClickTime: Long = 0
    private val debounceDelay: Long = 600
    private var selectedSlotIndex = 2   // default "11:00 AM" selected
    private var selectedTimeText = "11:00 AM"

    private val slotIds by lazy {
        listOf(
            binding.slot0, binding.slot1, binding.slot2,
            binding.slot3, binding.slot4, binding.slot5,
            binding.slot6, binding.slot7
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDoctorDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val doctorId = args.doctorId
        Log.d("DoctorDetail", "Doctor ID: $doctorId")

        setupInitialData(doctorId)

        binding.backBtnDocDetailLyt.setOnClickListener { findNavController().popBackStack() }

        binding.heartFavourite.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > debounceDelay) {
                lastClickTime = currentTime
                lifecycleScope.launch(Dispatchers.IO) {
                    val isFav = doctorDetailViewModel.isFavorite(currentUserId, doctorId)
                    withContext(Dispatchers.Main) {
                        if (isFav) doctorDetailViewModel.removeFromFavorites(currentUserId, doctorId)
                        else doctorDetailViewModel.addToFavorites(currentUserId, doctorId)
                    }
                }
            }
        }

        setupSlotSelection()

        // Confirm buttons both navigate to TakeAppointment
        val navigateToBooking = View.OnClickListener {
            val action = DoctorDetailDirections.actionDoctorDetailToTakeAppointment(doctorId)
            findNavController().navigate(action)
        }
        binding.confirmBtn.setOnClickListener(navigateToBooking)
        binding.totalPatientTxt.setOnClickListener(navigateToBooking)

        // Date chips
        binding.dateToday.setOnClickListener { selectDate(binding.dateToday, binding.dateTomorrow, binding.dateDay3, label = "Wed 16 Apr") }
        binding.dateTomorrow.setOnClickListener { selectDate(binding.dateTomorrow, binding.dateToday, binding.dateDay3, label = "Thu 17 Apr") }
        binding.dateDay3.setOnClickListener { selectDate(binding.dateDay3, binding.dateToday, binding.dateTomorrow, label = "Fri 18 Apr") }
    }

    private fun selectDate(selected: TextView, vararg others: TextView, label: String) {
        selected.setBackgroundResource(R.drawable.date_selected_bg)
        selected.setTextColor(resources.getColor(R.color.white, null))
        others.forEach {
            it.setBackgroundResource(R.drawable.date_unselected_bg)
            it.setTextColor(resources.getColor(R.color.black, null))
        }
        binding.dateLabelTxt.text = label
    }

    private fun setupSlotSelection() {
        slotIds.forEachIndexed { index, tv ->
            tv.setOnClickListener {
                selectSlot(index, tv.text.toString())
            }
        }
        selectSlot(selectedSlotIndex, selectedTimeText)
    }

    private fun selectSlot(index: Int, time: String) {
        selectedSlotIndex = index
        selectedTimeText = time
        slotIds.forEachIndexed { i, tv ->
            if (i == index) {
                tv.setBackgroundResource(R.drawable.time_slot_selected_bg)
                tv.setTextColor(resources.getColor(R.color.white, null))
            } else {
                tv.setBackgroundResource(R.drawable.time_slot_unselected_bg)
                tv.setTextColor(resources.getColor(R.color.black, null))
            }
        }
    }

    private fun setupInitialData(doctorId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            doctorDetailViewModel.fetchDoctorDetail(doctorId)
            val isFav = doctorDetailViewModel.isFavorite(currentUserId, doctorId)
            binding.heartFavourite.setImageResource(if (isFav) R.drawable.heart2 else R.drawable.heart1)
        }
        bindObservers()
    }

    private fun bindObservers() {
        doctorDetailViewModel.doctorDetail.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.cardViewShimmerView.visibility = View.VISIBLE
                    binding.cardViewShimmerView.startShimmer()
                    binding.detailCardView.visibility = View.INVISIBLE
                }
                is Resource.Success -> {
                    val d: DoctorModel? = resource.data
                    binding.cardViewShimmerView.stopShimmer()
                    binding.cardViewShimmerView.visibility = View.GONE
                    binding.detailCardView.visibility = View.VISIBLE

                    val drawableRes = DummyData.getDoctorDrawable(d?.image?.get(0) ?: "doctor_one")
                    binding.imageView.setImageResource(drawableRes)
                    binding.nameTxt.text = d?.name
                    binding.jobTxt.text = d?.specialization
                    binding.experienceTxt.text = "${d?.experienceYears} yrs Experience"
                    binding.ratingBar.rating = d?.rating ?: 4f
                    binding.ratingTxt.text = d?.rating?.toString() ?: "4.5"
                    binding.ReviewTxt.text = " (${d?.totalReviews} reviews)"
                    binding.descTxt.text = d?.description

                    // Update time slots from data
                    d?.timeSlot?.let { slots ->
                        slotIds.forEachIndexed { i, tv ->
                            if (i < slots.size) {
                                tv.text = slots[i]
                                tv.visibility = View.VISIBLE
                            } else {
                                tv.visibility = View.INVISIBLE
                            }
                        }
                    }
                    selectSlot(2, slotIds.getOrNull(2)?.text?.toString() ?: "11:00 AM")
                }
                is Resource.Error -> {}
                else -> Unit
            }
        }

        doctorDetailViewModel.favoriteStatus.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                updateFavorite(resource.data!!)
            }
        }
    }

    private fun updateFavorite(isFavorite: Boolean) {
        binding.heartFavourite.setImageResource(if (isFavorite) R.drawable.heart2 else R.drawable.heart1)
        Snackbar.make(binding.root, if (isFavorite) "Added to favorites" else "Removed from favourites", Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
