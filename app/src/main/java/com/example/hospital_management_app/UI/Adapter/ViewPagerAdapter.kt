package com.example.hospital_management_app.UI.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hospital_management_app.Data.BannerModel
import com.example.hospital_management_app.databinding.ItemBannerBinding

class ViewPagerAdapter(private var banners: List<BannerModel>) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemBannerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val banner = banners[position]
        holder.binding.apply {
            bannerImageView.setBackgroundResource(banner.backgroundDrawable)
            bannerTitleTxt.text = banner.title
            bannerSubtitleTxt.text = banner.subtitle
            bannerDoctorImage.setImageResource(banner.doctorImage)
        }
    }

    override fun getItemCount(): Int = banners.size
}