package com.example.hospital_management_app.UI.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hospital_management_app.Data.AppointmentModel
import com.example.hospital_management_app.Data.DummyData
import com.example.hospital_management_app.databinding.ItemAppointmentBinding

class AppointmentAdapter : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAppointmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AppointmentModel) {
            binding.apply {
                val drawableRes = DummyData.getDoctorDrawable(item.doctorImage)
                doctorImage.setImageResource(drawableRes)
                doctorName.text = item.doctorName
                doctorSpec.text = item.doctorSpecialization
                amountTxt.text = "₹ ${item.amount}"
                dateTxt.text = item.date
                timeTxt.text = item.time
                bookingIdTxt.text = "Booking ID: ${item.bookingId}"
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<AppointmentModel>() {
        override fun areItemsTheSame(o: AppointmentModel, n: AppointmentModel) = o.id == n.id
        override fun areContentsTheSame(o: AppointmentModel, n: AppointmentModel) = o == n
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }
}

