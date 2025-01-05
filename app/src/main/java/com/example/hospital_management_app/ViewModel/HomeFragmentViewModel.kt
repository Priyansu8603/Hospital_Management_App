package com.example.hospital_management_app.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hospital_management_app.Data.BannerModel
import com.example.hospital_management_app.Data.DoctorModel
import com.example.hospital_management_app.Utilities.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.PrivateKey
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage

) : ViewModel() {

    private val _popularDoctors = MutableLiveData<Resource<List<DoctorModel>>>()
    val popularDoctors: LiveData<Resource<List<DoctorModel>>> = _popularDoctors

    private val _featureDoctors = MutableLiveData<Resource<List<DoctorModel>>>()
    val featureDoctors: LiveData<Resource<List<DoctorModel>>> = _featureDoctors

    private val _advertisement = MutableLiveData<Resource<List<String>>>()
    val advertisement: LiveData<Resource<List<String>>> = _advertisement



    init {
        fetchPopularDoctors()
        fetchFeatureDoctors()
        getAdvertisements()
    }

    private fun fetchPopularDoctors() {
        viewModelScope.launch {
            _popularDoctors.value = Resource.Loading()
        }
        firestore.collection("doctors")
            .whereEqualTo("category", "Popular")
            .get().addOnSuccessListener { result ->
                val popularDoctorsList = result.toObjects(DoctorModel::class.java)
                viewModelScope.launch {
                    _popularDoctors.value = Resource.Success(popularDoctorsList)
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _popularDoctors.value = Resource.Error(it.message.toString())
                }
            }

    }

    private fun fetchFeatureDoctors() {
        viewModelScope.launch {
            _featureDoctors.value = Resource.Loading()
        }
        firestore.collection("doctors")
            .whereEqualTo("category", "Feature")
            .get().addOnSuccessListener { result ->
                val featureDoctorsList = result.toObjects(DoctorModel::class.java)
                viewModelScope.launch {
                    _featureDoctors.value = Resource.Success(featureDoctorsList)
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _featureDoctors.value = Resource.Error(it.message.toString())
                }
            }

    }

    private fun getAdvertisements() {
        viewModelScope.launch {
            _advertisement.value = Resource.Loading()

            try {
                val imageUrls = getBannerImageUrls()
                Log.d("HomeFragment", "Advertisement data: $imageUrls")
                _advertisement.value = Resource.Success(imageUrls)
            } catch (e: Exception) {
                _advertisement.value = Resource.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    private suspend fun getBannerImageUrls(): List<String> {
        val storageRef = firebaseStorage.reference.child("banner/")
        val listResult = storageRef.listAll().await()
        val imageUrls = mutableListOf<String>()

        for (item in listResult.items) {
            try {
                val url = item.downloadUrl.await().toString()
                imageUrls.add(url)
            } catch (e: Exception) {
                // Handle individual item download failure if necessary
                // For now, just continue to the next item
                continue
            }
        }

        return imageUrls
    }


}