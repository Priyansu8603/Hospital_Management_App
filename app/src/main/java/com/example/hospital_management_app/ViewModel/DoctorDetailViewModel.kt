package com.example.hospital_management_app.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hospital_management_app.Data.DoctorModel
import com.example.hospital_management_app.Utilities.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class DoctorDetailViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _doctorDetail = MutableLiveData<Resource<DoctorModel>>()
    val doctorDetail: LiveData<Resource<DoctorModel>> = _doctorDetail

    private val _favoriteStatus = MutableLiveData<Resource<Boolean>>()
    val favoriteStatus: LiveData<Resource<Boolean>> = _favoriteStatus


    suspend fun fetchDoctorDetail(doctorId: String) {
        viewModelScope.launch {
            _doctorDetail.value = Resource.Loading()
            try {
                // Log the doctor ID to ensure it's correct
                Log.d("DoctorDetailViewModel", "Fetching details for Doctor ID: $doctorId")

                // Query Firestore to find the document with the matching doctor ID
                val querySnapshot = firestore.collection("doctors")
                    .whereEqualTo("id", doctorId)
                    .get()
                    .await()

                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    val doctorDetail = documentSnapshot.toObject<DoctorModel>()

                    if (doctorDetail != null) {
                        Log.d("DoctorDetailViewModel", "Doctor Detail: $doctorDetail")
                        _doctorDetail.value = Resource.Success(doctorDetail)
                    } else {
                        Log.e("DoctorDetailViewModel", "Doctor not found")
                        _doctorDetail.value = Resource.Error("Doctor not found")
                    }
                } else {
                    Log.e("DoctorDetailViewModel", "Doctor not found")
                    _doctorDetail.value = Resource.Error("Doctor not found")
                }

            } catch (e: Exception) {
                Log.e("DoctorDetailViewModelError", "Error fetching doctor detail", e)
                _doctorDetail.value = Resource.Error(e.message.toString())
            }
        }

    }


    fun addToFavorites(userId: String, doctorId: String) {
        viewModelScope.launch {
            _favoriteStatus.value = Resource.Loading()
            try {
                val favoriteDocRef = firestore.collection("users")
                    .document(userId)
                    .collection("favorites")
                    .document(doctorId)

                // Set the doctor ID as a field in the document
                favoriteDocRef.set(mapOf("doctorId" to doctorId)).await()

                Log.d(
                    "DoctorDetailViewModel",
                    "Doctor $doctorId added to favorites for user $userId"
                )
                _favoriteStatus.value = Resource.Success(true)
            } catch (e: Exception) {
                Log.e("DoctorDetailViewModelError", "Error adding doctor to favorites", e)
                _favoriteStatus.value = Resource.Error(e.message.toString())
            }
        }
    }

    fun removeFromFavorites(userId: String, doctorId: String) {
        viewModelScope.launch {
            _favoriteStatus.value = Resource.Loading()
            try {
                val favoriteDocRef = firestore.collection("users")
                    .document(userId)
                    .collection("favorites")
                    .document(doctorId)

                favoriteDocRef.delete().await()

                Log.d(
                    "DoctorDetailViewModel",
                    "Doctor $doctorId removed from favorites for user $userId"
                )
                _favoriteStatus.value = Resource.Success(false)
            } catch (e: Exception) {
                Log.e("DoctorDetailViewModelError", "Error removing doctor from favorites", e)
                _favoriteStatus.value = Resource.Error(e.message.toString())
            }
        }
    }

    suspend fun isFavorite(userId: String, doctorId: String): Boolean {
        return try {
            val favoriteDocRef = firestore.collection("users")
                .document(userId)
                .collection("favorites")
                .document(doctorId)
                .get()
                .await()

            favoriteDocRef.exists()
        } catch (e: Exception) {
            Log.e("DoctorDetailViewModelError", "Error checking if doctor is favorite", e)
            false
        }
    }
}
