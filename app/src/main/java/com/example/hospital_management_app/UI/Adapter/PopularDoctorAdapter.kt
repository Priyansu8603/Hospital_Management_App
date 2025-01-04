package com.example.hospital_management_app.UI.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hospital_management_app.Data.DoctorModel
import com.example.hospital_management_app.databinding.ItemPopularDoctorBinding

class PopularDoctorAdapter(private val onDoctorClick: (String?) -> Unit) : RecyclerView.Adapter<PopularDoctorAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPopularDoctorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(doctor: DoctorModel) {
            // Bind data to the views in the item layout
            binding.apply {
                Glide.with(itemView).load(doctor.image?.get(0)).into(imageView)
                nameTxt.text = doctor.name
                jobTxt.text = doctor.specialization
                ratingBar.rating = doctor.rating!!

                root.setOnClickListener {
                    onDoctorClick(doctor.id)
                    Log.d("DoctorClickOnAdapter", "Doctor ID: ${doctor.id}")
                }
            }

        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<DoctorModel>(){
        override fun areItemsTheSame(oldItem: DoctorModel, newItem: DoctorModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DoctorModel, newItem: DoctorModel): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPopularDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doctor = differ.currentList[position]
        holder.bind(doctor)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}