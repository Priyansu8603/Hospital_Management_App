package com.example.hospital_management_app.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hospital_management_app.Data.AppRepository
import com.example.hospital_management_app.Data.DoctorModel
import com.example.hospital_management_app.Data.DummyData
import com.example.hospital_management_app.Utilities.Resource

// Hilt/Firebase removed for demo build
// @HiltViewModel
// class DoctorDetailViewModel @Inject constructor(private val firestore: FirebaseFirestore) : ViewModel()
class DoctorDetailViewModel : ViewModel() {

    private val _doctorDetail = MutableLiveData<Resource<DoctorModel>>()
    val doctorDetail: LiveData<Resource<DoctorModel>> = _doctorDetail

    private val _favoriteStatus = MutableLiveData<Resource<Boolean>>()
    val favoriteStatus: LiveData<Resource<Boolean>> = _favoriteStatus

    suspend fun fetchDoctorDetail(doctorId: String) {
        _doctorDetail.value = Resource.Loading()
        val doctor = DummyData.getDoctorById(doctorId)
        if (doctor != null) {
            _doctorDetail.value = Resource.Success(doctor)
        } else {
            _doctorDetail.value = Resource.Error("Doctor not found")
        }
    }

    fun addToFavorites(userId: String, doctorId: String) {
        AppRepository.addFavourite(doctorId)
        _favoriteStatus.value = Resource.Success(true)
    }

    fun removeFromFavorites(userId: String, doctorId: String) {
        AppRepository.removeFavourite(doctorId)
        _favoriteStatus.value = Resource.Success(false)
    }

    suspend fun isFavorite(userId: String, doctorId: String): Boolean {
        return AppRepository.isFavourite(doctorId)
    }
}
