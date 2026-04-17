package com.example.hospital_management_app.UI.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hospital_management_app.Data.DoctorModel
import com.example.hospital_management_app.Data.DummyData
import com.example.hospital_management_app.R
import com.example.hospital_management_app.ViewModel.DoctorDetailViewModel
import com.example.hospital_management_app.databinding.ItemFeatureDoctorBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeatureDoctorAdapter(
    private val onDoctorClick: (String?) -> Unit,
    private val doctorDetailViewModel: DoctorDetailViewModel,
    private val lifecycleOwner: LifecycleOwner,
) : RecyclerView.Adapter<FeatureDoctorAdapter.ViewHolder>() {

    // Demo userId instead of FirebaseAuth
    private val currentUserId = "demo_user"
    private var lastClickTime: Long = 0
    private val debounceDelay: Long = 600

    inner class ViewHolder(val binding: ItemFeatureDoctorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(doctor: DoctorModel, isFavorite: Boolean) {
            binding.apply {
                val drawableRes = DummyData.getDoctorDrawable(doctor.image?.get(0) ?: "doctor_one")
                imageView.setImageResource(drawableRes)
                nameTxt.text = doctor.name
                jobTxt.text = doctor.specialization
                heartBtn.setImageResource(if (isFavorite) R.drawable.heart2 else R.drawable.heart1)
                root.setOnClickListener {
                    onDoctorClick(doctor.id)
                    Log.d("DoctorClickOnAdapter", "Doctor ID: ${doctor.id}")
                }
                heartBtn.setOnClickListener {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastClickTime > debounceDelay) {
                        lastClickTime = currentTime
                        lifecycleOwner.lifecycleScope.launch {
                            toggleFavorite(doctor)
                        }
                    }
                }
            }
        }

        private suspend fun toggleFavorite(doctor: DoctorModel) {
            val isFavorite = doctorDetailViewModel.isFavorite(currentUserId, doctor.id!!)
            if (isFavorite) {
                doctorDetailViewModel.removeFromFavorites(currentUserId, doctor.id)
            } else {
                doctorDetailViewModel.addToFavorites(currentUserId, doctor.id)
            }
            withContext(Dispatchers.Main) {
                bind(doctor, !isFavorite)
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<DoctorModel>() {
        override fun areItemsTheSame(oldItem: DoctorModel, newItem: DoctorModel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: DoctorModel, newItem: DoctorModel) = oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFeatureDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doctor = differ.currentList[position]
        lifecycleOwner.lifecycleScope.launch {
            val isFavorite = withContext(Dispatchers.IO) {
                doctorDetailViewModel.isFavorite(currentUserId, doctor.id!!)
            }
            withContext(Dispatchers.Main) {
                holder.bind(doctor, isFavorite)
            }
        }

    }

    override fun getItemCount() = differ.currentList.size
}