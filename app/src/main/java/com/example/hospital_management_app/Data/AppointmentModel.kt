package com.example.hospital_management_app.Data

data class AppointmentModel(
    val id: String,
    val doctorId: String,
    val doctorName: String,
    val doctorSpecialization: String,
    val doctorImage: String,
    val date: String,
    val time: String,
    val amount: Int,
    val bookingId: String
)

