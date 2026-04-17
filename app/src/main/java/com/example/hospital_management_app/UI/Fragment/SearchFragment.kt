package com.example.hospital_management_app.UI.Fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hospital_management_app.Data.DummyData
import com.example.hospital_management_app.R
import com.example.hospital_management_app.UI.Adapter.FindDoctorAdapter
import com.example.hospital_management_app.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FindDoctorAdapter
    private val allDoctors = DummyData.popularDoctors + DummyData.featureDoctors

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FindDoctorAdapter { doctorId ->
            val action = SearchFragmentDirections.actionSearchFragmentToDoctorDetail(doctorId ?: "doc1")
            findNavController().navigate(action)
        }

        binding.findDoctorRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@SearchFragment.adapter
        }
        adapter.differ.submitList(allDoctors)

        binding.searchbarSearchFrag.apply {
            isFocusable = true
            requestFocus()
        }

        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchbarSearchFrag.findFocus(), InputMethodManager.SHOW_IMPLICIT)

        binding.backBtnFindDocLyt.setOnClickListener {
            findNavController().popBackStack()
        }

        // Search filter
        binding.searchbarSearchFrag.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterDoctors(s?.toString() ?: "", null)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Category chips
        val chips = listOf(binding.chipAll, binding.chipGeneral, binding.chipCardiology, binding.chipEye, binding.chipNeurology, binding.chipDental)
        val categories = listOf(null, "General Physician", "Cardiologist", "Ophthalmologist", "Neurologist", "Dentist")
        chips.forEachIndexed { index, chip ->
            chip.setOnClickListener {
                chips.forEach { c ->
                    c.setBackgroundResource(R.drawable.date_unselected_bg)
                    c.setTextColor(resources.getColor(R.color.black, null))
                }
                chip.setBackgroundResource(R.drawable.date_selected_bg)
                chip.setTextColor(resources.getColor(R.color.white, null))
                filterDoctors(binding.searchbarSearchFrag.text?.toString() ?: "", categories[index])
            }
        }
    }

    private fun filterDoctors(query: String, category: String?) {
        val filtered = allDoctors.filter { doctor ->
            val matchesQuery = query.isEmpty() || doctor.name?.contains(query, ignoreCase = true) == true ||
                    doctor.specialization?.contains(query, ignoreCase = true) == true
            val matchesCategory = category == null || doctor.specialization == category
            matchesQuery && matchesCategory
        }
        adapter.differ.submitList(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}