package com.example.hospital_management_app.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.hospital_management_app.Data.AppRepository
import com.example.hospital_management_app.Data.DoctorModel

class FavouriteDoctorViewModel : ViewModel() {

    val favouriteDoctors: LiveData<List<DoctorModel>> = AppRepository._favouriteDoctors

    fun loadFavouriteDoctors() {
        // data is driven by AppRepository LiveData, nothing extra needed
    }
}
