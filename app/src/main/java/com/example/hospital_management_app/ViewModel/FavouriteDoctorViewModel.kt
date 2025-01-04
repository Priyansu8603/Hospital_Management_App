package com.example.hospital_management_app.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hospital_management_app.Data.DoctorModel
import com.example.hospital_management_app.Utilities.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteDoctorViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _favouriteDoctors = MutableLiveData<List<DoctorModel>>()
    val favouriteDoctors: LiveData<List<DoctorModel>> = _favouriteDoctors

    init {
        loadFavouriteDoctors()
    }

    fun loadFavouriteDoctors() {
        viewModelScope.launch {
            try {
                // Step 1: Fetch doctor IDs from the user's 'favourites' sub-collection
                val userId = auth.uid
                if (userId == null) {
                    _favouriteDoctors.value = emptyList()
                    return@launch
                }

                val favouritesRef = firestore.collection("users")
                    .document(userId)
                    .collection("favorites")

                favouritesRef.get()
                    .addOnSuccessListener { snapshot ->
                        val doctorIds = snapshot.documents.mapNotNull { doc ->
                            doc.getString("doctorId") // Get the doctorId field
                        }

                        if (doctorIds.isNotEmpty()) {
                            // Step 2: Fetch doctor details from the 'doctors' collection
                            fetchDoctorsByIds(doctorIds)
                        } else {
                            _favouriteDoctors.value = emptyList()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("FavouriteDoctorViewModel", "Error getting favourite doctors: $exception")
                        _favouriteDoctors.value = emptyList() // Handle failure
                    }
            } catch (e: Exception) {
                Log.e("FavouriteDoctorViewModel", "Error loading favourite doctors: ${e.message}")
                _favouriteDoctors.value = emptyList() // Handle error
            }
        }
    }

    private fun fetchDoctorsByIds(doctorIds: List<String>) {
        // Fetch all doctors where the document ID is in the list of doctorIds
        firestore.collection("doctors")
            .whereIn("id", doctorIds) // Assuming 'id' is the field in the doctors collection
            .get()
            .addOnSuccessListener { snapshot ->
                val doctors = snapshot.documents.mapNotNull { it.toObject<DoctorModel>() }
                _favouriteDoctors.value = doctors
            }
            .addOnFailureListener { exception ->
                Log.e("FavouriteDoctorViewModel", "Error fetching doctors by IDs: $exception")
                _favouriteDoctors.value = emptyList() // Handle failure
            }
    }
}
