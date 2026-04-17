package com.example.hospital_management_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hospital_management_app.Data.AppRepository
import com.example.hospital_management_app.UI.Adapter.AppointmentAdapter
import com.example.hospital_management_app.databinding.FragmentMyAppointmentBinding

class MyAppointment : Fragment() {

    private var _binding: FragmentMyAppointmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AppointmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMyAppointmentBinding.inflate(layoutInflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtnMyAppointLyt.setOnClickListener {
            findNavController().popBackStack()
        }

        adapter = AppointmentAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        AppRepository.bookedAppointments.observe(viewLifecycleOwner) { list ->
            adapter.differ.submitList(list ?: emptyList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
         _binding = null
    }

}