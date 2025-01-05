package com.example.hospital_management_app.Data

data class DoctorModel(
    val id : String? = null,
    val name: String? = null,
    val age: Int? = null,
    val category: String? = null,
    val specialization: String? = null,
    val pricePerHour: Int? = null,
    val rating: Float? = null,
    val experienceYears: Int? = null,
    val description: String? = null,
    val image : List<String>? = null,
    val timeSlot : List<String>? = null,
    val totalReviews : Int? = null,
    val reviews : List<String>? = null,
    val totalPatients : Int? = null,
    val ongoingPatients : Int? = null

)
