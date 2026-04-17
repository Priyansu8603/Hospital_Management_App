package com.example.hospital_management_app.Data

import com.example.hospital_management_app.R

object DummyData {

    val popularDoctors = listOf(
        DoctorModel(
            id = "doc1",
            name = "Dr. Sarah Johnson",
            age = 35,
            category = "Popular",
            specialization = "Cardiologist",
            pricePerHour = 800,
            rating = 4.8f,
            experienceYears = 8,
            description = "Experienced cardiologist at City Heart Hospital specializing in heart health, offering personalized care and advanced diagnostic services to improve patient outcomes.",
            image = listOf("doctor_one"),
            timeSlot = listOf("09:00 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:30 PM", "01:30 PM", "02:00 PM", "02:30 PM", "03:00 PM"),
            totalReviews = 463,
            reviews = listOf("Excellent doctor!", "Very compassionate and thorough"),
            totalPatients = 1200,
            ongoingPatients = 45
        ),
        DoctorModel(
            id = "doc2",
            name = "Dr. David Lee",
            age = 42,
            category = "Popular",
            specialization = "Neurologist",
            pricePerHour = 900,
            rating = 4.7f,
            experienceYears = 10,
            description = "Senior neurologist at Neuro Care Center with expertise in treating stroke, epilepsy, migraines and neurodegenerative disorders.",
            image = listOf("doctor_two"),
            timeSlot = listOf("10:00 AM", "11:00 AM", "12:00 PM", "02:00 PM", "04:30 PM"),
            totalReviews = 389,
            reviews = listOf("Very knowledgeable!", "Great bedside manner"),
            totalPatients = 980,
            ongoingPatients = 38
        ),
        DoctorModel(
            id = "doc3",
            name = "Dr. Emily Watson",
            age = 38,
            category = "Popular",
            specialization = "Ophthalmologist",
            pricePerHour = 700,
            rating = 4.9f,
            experienceYears = 6,
            description = "Highly rated ophthalmologist at Vision Plus Clinic, specializing in LASIK surgery, cataract treatment, and comprehensive eye care.",
            image = listOf("doctor_three"),
            timeSlot = listOf("09:00 AM", "10:00 AM", "11:00 AM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM"),
            totalReviews = 512,
            reviews = listOf("Amazing surgeon", "Best eye doctor"),
            totalPatients = 1500,
            ongoingPatients = 60
        ),
        DoctorModel(
            id = "doc4",
            name = "Dr. Michael Brown",
            age = 50,
            category = "Popular",
            specialization = "General Physician",
            pricePerHour = 600,
            rating = 4.6f,
            experienceYears = 12,
            description = "Experienced general physician at Medico Hospital providing comprehensive primary care, preventive health checks and management of chronic conditions.",
            image = listOf("doctor_four"),
            timeSlot = listOf("09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "02:00 PM", "05:00 PM", "06:00 PM"),
            totalReviews = 290,
            reviews = listOf("Very thorough", "Excellent doctor"),
            totalPatients = 2200,
            ongoingPatients = 70
        )
    )

    val featureDoctors = listOf(
        DoctorModel(
            id = "doc5",
            name = "Dr. Olivia Martin",
            age = 36,
            category = "Feature",
            specialization = "Dentist",
            pricePerHour = 500,
            rating = 4.8f,
            experienceYears = 7,
            description = "Skilled dentist at Smile Dental Care offering cosmetic dentistry, orthodontics, dental implants and comprehensive oral health care.",
            image = listOf("doctor_five"),
            timeSlot = listOf("09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "02:00 PM", "03:00 PM", "04:00 PM"),
            totalReviews = 345,
            reviews = listOf("Painless treatment", "Very professional"),
            totalPatients = 890,
            ongoingPatients = 32
        ),
        DoctorModel(
            id = "doc6",
            name = "Dr. James Wilson",
            age = 45,
            category = "Feature",
            specialization = "Orthopedic Surgeon",
            pricePerHour = 1000,
            rating = 4.9f,
            experienceYears = 15,
            description = "Leading orthopedic surgeon at City Ortho Hospital with expertise in joint replacement, sports injuries, and minimally invasive spine surgery.",
            image = listOf("doctor_six"),
            timeSlot = listOf("09:00 AM", "10:00 AM", "02:00 PM", "03:00 PM", "04:00 PM"),
            totalReviews = 421,
            reviews = listOf("Brilliant surgeon", "Saved my knee"),
            totalPatients = 1800,
            ongoingPatients = 55
        ),
        DoctorModel(
            id = "doc7",
            name = "Dr. Priya Sharma",
            age = 38,
            category = "Feature",
            specialization = "Dermatologist",
            pricePerHour = 650,
            rating = 4.7f,
            experienceYears = 9,
            description = "Renowned dermatologist specializing in skin care, acne treatment, cosmetic dermatology and hair restoration procedures.",
            image = listOf("doctor_one"),
            timeSlot = listOf("10:00 AM", "11:00 AM", "12:00 PM", "03:00 PM", "04:00 PM"),
            totalReviews = 278,
            reviews = listOf("Wonderful doctor!", "Very gentle"),
            totalPatients = 760,
            ongoingPatients = 28
        )
    )

    val banners = listOf(
        BannerModel(
            title = "Your Health, Our Priority",
            subtitle = "Book appointments with top specialists",
            backgroundDrawable = R.drawable.banner_bg_1,
            doctorImage = R.drawable.doctor_one
        ),
        BannerModel(
            title = "50% Off First Consultation",
            subtitle = "Verified doctors available today",
            backgroundDrawable = R.drawable.banner_bg_2,
            doctorImage = R.drawable.doctor_two
        ),
        BannerModel(
            title = "24/7 Emergency Care",
            subtitle = "We are always here for you",
            backgroundDrawable = R.drawable.banner_bg_3,
            doctorImage = R.drawable.doctor_three
        ),
        BannerModel(
            title = "Online Consultations",
            subtitle = "Connect with doctors from home",
            backgroundDrawable = R.drawable.banner_bg_4,
            doctorImage = R.drawable.doctor_four
        )
    )

    val dummyUser = User(
        name = "John Doe",
        phoneNumber = "+91 9876543210",
        email = "john.doe@email.com",
        dob = "15/08/1995",
        address = "123, MG Road, Bengaluru, Karnataka - 560001"
    )

    fun getDoctorById(id: String): DoctorModel? {
        return (popularDoctors + featureDoctors).find { it.id == id }
    }

    fun getDoctorDrawable(imageName: String): Int {
        return when (imageName) {
            "doctor_one" -> R.drawable.doctor_one
            "doctor_two" -> R.drawable.doctor_two
            "doctor_three" -> R.drawable.doctor_three
            "doctor_four" -> R.drawable.doctor_four
            "doctor_five" -> R.drawable.doctor_five
            "doctor_six" -> R.drawable.doctor_six
            else -> R.drawable.doctor_one
        }
    }

    val favouriteDoctors = listOf(popularDoctors[0], featureDoctors[0])
}
