package com.example.hospital_management_app.Data

import androidx.lifecycle.MutableLiveData

object AppRepository {

    // ---- Favourites ----
    private val _favouriteIds = mutableSetOf<String>()

    fun addFavourite(doctorId: String) {
        _favouriteIds.add(doctorId)
        _favouriteDoctors.value = getFavouriteDoctors()
    }

    fun removeFavourite(doctorId: String) {
        _favouriteIds.remove(doctorId)
        _favouriteDoctors.value = getFavouriteDoctors()
    }

    fun isFavourite(doctorId: String) = _favouriteIds.contains(doctorId)

    private fun getFavouriteDoctors(): List<DoctorModel> =
        (DummyData.popularDoctors + DummyData.featureDoctors).filter { _favouriteIds.contains(it.id) }

    val _favouriteDoctors = MutableLiveData<List<DoctorModel>>(emptyList())

    // ---- Appointments ----
    val bookedAppointments = MutableLiveData<List<AppointmentModel>>(emptyList())

    fun addAppointment(appointment: AppointmentModel) {
        val current = bookedAppointments.value?.toMutableList() ?: mutableListOf()
        current.add(0, appointment)
        bookedAppointments.value = current
    }
}

