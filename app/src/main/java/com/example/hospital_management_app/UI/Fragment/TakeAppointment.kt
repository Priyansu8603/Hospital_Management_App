package com.example.hospital_management_app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.hospital_management_app.Data.AppRepository
import com.example.hospital_management_app.Data.AppointmentModel
import com.example.hospital_management_app.Data.DummyData
import com.example.hospital_management_app.Utilities.PdfGenerator
import com.example.hospital_management_app.databinding.FragmentTakeAppointmentBinding
import java.text.SimpleDateFormat
import java.util.*

class TakeAppointment : Fragment() {

    private var _binding: FragmentTakeAppointmentBinding? = null
    private val binding get() = _binding!!
    private val args: TakeAppointmentArgs by navArgs()
    private var currentBookingId = ""

    // Permission launcher for Android 9 and below
    private val writePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) downloadPdf()
            else Toast.makeText(requireContext(), "Storage permission denied", Toast.LENGTH_SHORT).show()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTakeAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val doctor = DummyData.getDoctorById(args.doctorId)

        // Fill doctor info
        doctor?.let { d ->
            val drawableRes = DummyData.getDoctorDrawable(d.image?.get(0) ?: "doctor_one")
            binding.doctorImage.setImageResource(drawableRes)
            binding.doctorName.text = d.name
            binding.doctorSpec.text = d.specialization
            binding.priceTxt.text = "  ₹ ${d.pricePerHour}"
            binding.totalPaidTxt.text = "₹ ${d.pricePerHour}"
        }

        // Fill booking details
        val today = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(Date())
        binding.dateTxt.text = today
        binding.timeTxt.text = "11:00 AM"
        currentBookingId = "APT-${doctor?.id?.uppercase() ?: "CH"}-${SimpleDateFormat("ddMMyy", Locale.getDefault()).format(Date())}-1100"
        binding.bookingIdTxt.text = currentBookingId

        // Save appointment to repository (only once per visit)
        doctor?.let { d ->
            val appointment = AppointmentModel(
                id = currentBookingId,
                doctorId = d.id ?: "",
                doctorName = d.name ?: "",
                doctorSpecialization = d.specialization ?: "",
                doctorImage = d.image?.get(0) ?: "doctor_one",
                date = today,
                time = "11:00 AM",
                amount = d.pricePerHour ?: 0,
                bookingId = currentBookingId
            )
            AppRepository.addAppointment(appointment)
        }

        // Navigation buttons
        binding.backToHomeBtn.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
        binding.bookAnotherBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        // Download PDF button
        binding.downloadReceiptBtn.setOnClickListener {
            checkPermissionAndDownload()
        }
    }

    private fun checkPermissionAndDownload() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Android 9 and below need WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                downloadPdf()
            } else {
                writePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        } else {
            // Android 10+ no permission needed
            downloadPdf()
        }
    }

    private fun downloadPdf() {
        val contentView = binding.pdfContent
        val fileName = "Appointment_${currentBookingId}.pdf"
        // Wait for view to be fully laid out before capturing
        contentView.post {
            PdfGenerator.captureAndSave(requireContext(), contentView, fileName)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}