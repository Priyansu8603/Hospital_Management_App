package com.example.hospital_management_app.UI.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.hospital_management_app.R
import com.example.hospital_management_app.UI.Adapter.FeatureDoctorAdapter
import com.example.hospital_management_app.UI.Adapter.PopularDoctorAdapter
import com.example.hospital_management_app.UI.Adapter.ViewPagerAdapter
import com.example.hospital_management_app.Utilities.Resource
import com.example.hospital_management_app.ViewModel.DoctorDetailViewModel
import com.example.hospital_management_app.ViewModel.HomeFragmentViewModel
import com.example.hospital_management_app.ViewModel.ProfileViewModel
import com.example.hospital_management_app.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val doctorDetailViewModel by viewModels<DoctorDetailViewModel>()
    private lateinit var popularDoctorAdapter: PopularDoctorAdapter
    private lateinit var featureDoctorAdapter: FeatureDoctorAdapter
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private val homeFragmentModel by viewModels<HomeFragmentViewModel>()
    private val profileViewModel by viewModels<ProfileViewModel>()

    private var circles = mutableListOf<ImageView>()
    private var circleNumber = 0
    private var currentPage = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        featureDoctorAdapter = FeatureDoctorAdapter(::onDoctorClick,doctorDetailViewModel,viewLifecycleOwner)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchBarHomeFrag.setOnClickListener {

            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)

        }

        binding.profileIcon.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profile)
        }

        bindObservers()
        setupPopularDoctorRv()
        setupFeatureDoctorRv()
        setupViewPager()



        // Page change callback for ViewPager2
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position % circleNumber
                updateIndicator(currentPage)
            }
        })


    }

    private fun onDoctorClick(doctor_Id: String?) {
        Log.d("onDoctorClick", "Tapped Doctor ID: $doctor_Id")
        doctor_Id?.let {
            val action = HomeFragmentDirections.actionHomeFragmentToDoctorDetail(it)
            findNavController().navigate(action)
        } ?: run {
            // Handle the case where doctor_Id is null (optional)
            Toast.makeText(requireContext(), "Doctor ID is missing", Toast.LENGTH_SHORT).show()
        }

    }


    private fun updateIndicator(index: Int) {
        for (i in circles.indices) {
            circles[i].setImageResource(if (i == index) R.drawable.viewpager_selected else R.drawable.viewpager_not_selected)
            val lp = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(if (i == index) R.dimen.dp_18 else R.dimen.dp_6),
                resources.getDimensionPixelSize(R.dimen.dp_6)
            )
            lp.marginStart = resources.getDimensionPixelSize(R.dimen.dp_2)
            circles[i].layoutParams = lp

        }
    }


    private fun bindObservers(){

        homeFragmentModel.popularDoctors.observe(viewLifecycleOwner){

            when(it){
                is Resource.Loading ->{
                    binding.progressBar2.visibility = View.VISIBLE
                }
                is Resource.Success ->{
                    binding.progressBar2.visibility = View.GONE
                    popularDoctorAdapter.differ.submitList(it.data)
                }
                is Resource.Error ->{
                    binding.progressBar2.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }

        }

        homeFragmentModel.featureDoctors.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading ->{
                    binding.progressBar3.visibility = View.VISIBLE
                }
                is Resource.Success ->{
                    binding.progressBar3.visibility = View.GONE
                    featureDoctorAdapter.differ.submitList(it.data)
                }
                is Resource.Error ->{
                    binding.progressBar3.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        homeFragmentModel.advertisement.observe(viewLifecycleOwner){
            when (it) {
                is Resource.Success -> {
                    circleNumber = it.data!!.size
                    circles = mutableListOf()
                    binding.viewpagerDetails.removeAllViews() // Clear previous indicators
                    for (i in 0 until circleNumber) {
                        val imageView = ImageView(requireContext())
                        val lp = LinearLayout.LayoutParams(
                            resources.getDimensionPixelSize(R.dimen.dp_10),
                            resources.getDimensionPixelSize(R.dimen.dp_10)
                        )
                        imageView.layoutParams = lp
                        lp.marginStart = resources.getDimensionPixelSize(R.dimen.dp_2)
                        imageView.setImageResource(R.drawable.viewpager_not_selected)
                        binding.viewpagerDetails.addView(imageView)
                        circles.add(imageView)
                    }
                    // Initialize ViewPager2 Adapter and attach it to ViewPager2

                    binding.viewPager2.adapter = ViewPagerAdapter(it.data)
                    updateIndicator(0) // Set the first indicator as selected
                    currentPage = 0

                    binding.progressBar.visibility = View.INVISIBLE
                    binding.viewpagerDetails.visibility = View.VISIBLE

                    // Start auto-scrolling after the data is loaded
                    startAutoScroll()

                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.viewpagerDetails.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.viewpagerDetails.visibility = View.INVISIBLE
                }
                is Resource.Unspecified -> Unit
            }
        }

        profileViewModel.profile.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    // Handle loading state if needed
                }
                is Resource.Success -> {
                    val username = it.data?.name
                    binding.nameTxt.text = username?.let { name ->
                        getString(R.string.greeting_message, name)
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

    }


    private fun setupFeatureDoctorRv() {
        featureDoctorAdapter = FeatureDoctorAdapter(::onDoctorClick,doctorDetailViewModel,viewLifecycleOwner)
        binding.featureDoctorRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = featureDoctorAdapter

        }
    }

    private fun setupPopularDoctorRv() {
        popularDoctorAdapter = PopularDoctorAdapter(::onDoctorClick)
        binding.popularDoctorRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = popularDoctorAdapter
        }
    }

    private fun setupViewPager(){
        viewPagerAdapter = ViewPagerAdapter(emptyList())
        binding.viewPager2.apply {
            binding.viewPager2.adapter = viewPagerAdapter
        }
    }

    private fun startAutoScroll() {
        viewLifecycleOwner.lifecycleScope.launch {
            while (true) {
                delay(3000) // Delay for 3 seconds
                withContext(Dispatchers.Main) {
                    // Safely access binding
                    binding.let {
                        currentPage = (currentPage + 1) % circleNumber
                        it.viewPager2.setCurrentItem(currentPage, true)
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}