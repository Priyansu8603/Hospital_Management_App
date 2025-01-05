package com.example.hospital_management_app.UI.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hospital_management_app.Data.DoctorModel
import com.example.hospital_management_app.databinding.ItemFavouriteDoctorBinding


class FavouriteDoctorAdapter(
    private val onDoctorClick: (String?) -> Unit
) : RecyclerView.Adapter<FavouriteDoctorAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemFavouriteDoctorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(doctor: DoctorModel) {
            binding.apply {
                Glide.with(itemView).load(doctor.image?.get(0)).into(imageView)
                nameTxt.text = doctor.name
                jobTxt.text = doctor.specialization

                root.setOnClickListener {
                    onDoctorClick(doctor.id)
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<DoctorModel>() {
        override fun areItemsTheSame(oldItem: DoctorModel, newItem: DoctorModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DoctorModel, newItem: DoctorModel): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFavouriteDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doctor = differ.currentList[position]
        holder.bind(doctor)
    }
}