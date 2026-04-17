package com.example.hospital_management_app.UI.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hospital_management_app.Data.DoctorModel
import com.example.hospital_management_app.Data.DummyData
import com.example.hospital_management_app.databinding.ItemFindDoctorBinding

class FindDoctorAdapter(
    private val onBookClick: (String?) -> Unit
) : RecyclerView.Adapter<FindDoctorAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemFindDoctorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(doctor: DoctorModel) {
            val drawableRes = DummyData.getDoctorDrawable(doctor.image?.get(0) ?: "doctor_one")
            binding.imageView.setImageResource(drawableRes)
            binding.nameTxt.text = doctor.name
            binding.jobTxt.text = doctor.specialization
            binding.hospitalTxt.text = getHospital(doctor.specialization)
            binding.experienceTxt.text = "${doctor.experienceYears} yrs"
            binding.ratingTxt.text = " ⭐ ${doctor.rating}"
            binding.pricingTxt.text = "  ₹${doctor.pricePerHour}"
            val isAvailableToday = listOf("doc1", "doc3", "doc5", "doc7").contains(doctor.id)
            binding.availabilityTxt.text = if (isAvailableToday) "Available Today" else "Next Slot ${getNextSlot(doctor)}"
            binding.bookNowBtn.setOnClickListener { onBookClick(doctor.id) }
            binding.root.setOnClickListener { onBookClick(doctor.id) }
        }

        private fun getHospital(specialization: String?) = when (specialization) {
            "Cardiologist" -> "City Heart Hospital"
            "Neurologist" -> "Neuro Care Center"
            "Ophthalmologist" -> "Vision Plus Clinic"
            "General Physician" -> "Medico Hospital"
            "Dentist" -> "Smile Dental Care"
            "Orthopedic Surgeon" -> "City Ortho Hospital"
            "Dermatologist" -> "Skin Care Clinic"
            else -> "City Hospital"
        }

        private fun getNextSlot(doctor: DoctorModel): String {
            return when (doctor.id) {
                "doc2" -> "4:30 PM"
                "doc4" -> "6:00 PM"
                "doc6" -> "4:00 PM"
                else -> "10:00 AM"
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<DoctorModel>() {
        override fun areItemsTheSame(oldItem: DoctorModel, newItem: DoctorModel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: DoctorModel, newItem: DoctorModel) = oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFindDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(differ.currentList[position])
    override fun getItemCount() = differ.currentList.size
}

