package com.example.hospital_management_app.UI.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hospital_management_app.Data.DoctorModel
import com.example.hospital_management_app.Data.DummyData
import com.example.hospital_management_app.databinding.ItemPopularDoctorBinding

class PopularDoctorAdapter(private val onDoctorClick: (String?) -> Unit) : RecyclerView.Adapter<PopularDoctorAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPopularDoctorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(doctor: DoctorModel) {
            binding.apply {
                val drawableRes = DummyData.getDoctorDrawable(doctor.image?.get(0) ?: "doctor_one")
                imageView.setImageResource(drawableRes)
                nameTxt.text = doctor.name
                jobTxt.text = doctor.specialization
                ratingBar.rating = doctor.rating ?: 4f
                root.setOnClickListener {
                    onDoctorClick(doctor.id)
                    Log.d("DoctorClickOnAdapter", "Doctor ID: ${doctor.id}")
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<DoctorModel>() {
        override fun areItemsTheSame(oldItem: DoctorModel, newItem: DoctorModel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: DoctorModel, newItem: DoctorModel) = oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPopularDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(differ.currentList[position])

    override fun getItemCount() = differ.currentList.size
}