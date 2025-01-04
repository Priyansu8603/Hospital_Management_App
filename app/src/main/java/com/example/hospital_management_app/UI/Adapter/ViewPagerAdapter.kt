package com.example.hospital_management_app.UI.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hospital_management_app.Data.BannerModel
import com.example.hospital_management_app.Data.DoctorModel
import com.example.hospital_management_app.databinding.ItemBannerBinding

class ViewPagerAdapter(private var imageUrls: List<String>) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemBannerBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        Glide.with(holder.binding.bannerImageView.context).load(imageUrl).into(holder.binding.bannerImageView)
    }



    override fun getItemCount(): Int {
        return imageUrls.size
    }

}