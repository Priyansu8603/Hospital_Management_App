package com.example.hospital_management_app.UI.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hospital_management_app.Data.DoctorModel
import com.example.hospital_management_app.R
import com.example.hospital_management_app.ViewModel.DoctorDetailViewModel
import com.example.hospital_management_app.databinding.ItemFeatureDoctorBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeatureDoctorAdapter(
    private val onDoctorClick: (String?) -> Unit,
    private val doctorDetailViewModel: DoctorDetailViewModel, // Inject ViewModel to access favorite logic
    private val lifecycleOwner: LifecycleOwner,
) : RecyclerView.Adapter<FeatureDoctorAdapter.ViewHolder>() {

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private var lastClickTime: Long = 0
    private val debounceDelay: Long = 600 // 600 milliseconds

    inner class ViewHolder(val binding: ItemFeatureDoctorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(doctor: DoctorModel, isFavorite: Boolean) {
            binding.apply {
                Glide.with(itemView).load(doctor.image?.get(0)).into(imageView)
                nameTxt.text = doctor.name
                jobTxt.text = doctor.specialization

                // Set heart button resource based on whether it's a favorite
                heartBtn.setImageResource(if (isFavorite) R.drawable.heart2 else R.drawable.heart1)

                root.setOnClickListener {
                    onDoctorClick(doctor.id)
                    Log.d("DoctorClickOnAdapter", "Doctor ID: ${doctor.id}")
                }

                // Handle heart button clicks
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
            val isFavorite = doctorDetailViewModel.isFavorite(currentUserId!!, doctor.id!!)
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


//    private fun toggleFavorite(doctor: DoctorModel, imageButton: ImageButton) {
//        val isFavorite = checkIfFavorite(product)
//        if (isFavorite) {
//            removeFromFavorites(product)
//            showSnackbar("Removed from favorites")
//            updateFavoriteIcon(imageButton, false)
//        } else {
//            addToFavorites(product)
//            showSnackbar("Added to favorites")
//            updateFavoriteIcon(imageButton, true)
//        }
//    }

//    private fun checkIfFavorite(product: DoctorModel): Boolean {
//        return DoctorViewModel.isFavorite
//    }

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
            ItemFeatureDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doctor = differ.currentList[position]

        // Launch a coroutine to handle async tasks
        lifecycleOwner.lifecycleScope.launch {
            // Perform the favorite check in a background thread
            val isFavorite = withContext(Dispatchers.IO) {
                doctorDetailViewModel.isFavorite(currentUserId!!, doctor.id!!)
            }

            // Now bind the data to the UI on the main thread
            withContext(Dispatchers.Main) {
                holder.bind(doctor, isFavorite)
            }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}