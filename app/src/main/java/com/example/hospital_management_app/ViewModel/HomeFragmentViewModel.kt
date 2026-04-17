package com.example.hospital_management_app.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hospital_management_app.Data.BannerModel
import com.example.hospital_management_app.Data.DoctorModel
import com.example.hospital_management_app.Data.DummyData
import com.example.hospital_management_app.Utilities.Resource

// Hilt/Firebase removed for demo build
// @HiltViewModel
// class HomeFragmentViewModel @Inject constructor(...) : ViewModel()
class HomeFragmentViewModel : ViewModel() {

    private val _popularDoctors = MutableLiveData<Resource<List<DoctorModel>>>()
    val popularDoctors: LiveData<Resource<List<DoctorModel>>> = _popularDoctors

    private val _featureDoctors = MutableLiveData<Resource<List<DoctorModel>>>()
    val featureDoctors: LiveData<Resource<List<DoctorModel>>> = _featureDoctors

    private val _banners = MutableLiveData<Resource<List<BannerModel>>>()
    val banners: LiveData<Resource<List<BannerModel>>> = _banners

    // Keep for backward compatibility
    private val _advertisement = MutableLiveData<Resource<List<String>>>()
    val advertisement: LiveData<Resource<List<String>>> = _advertisement

    init {
        fetchPopularDoctors()
        fetchFeatureDoctors()
        getAdvertisements()
    }

    private fun fetchPopularDoctors() {
        _popularDoctors.value = Resource.Success(DummyData.popularDoctors)
    }

    private fun fetchFeatureDoctors() {
        _featureDoctors.value = Resource.Success(DummyData.featureDoctors)
    }

    private fun getAdvertisements() {
        _banners.value = Resource.Success(DummyData.banners)
        // dummy string list for backward compat
        _advertisement.value = Resource.Success(List(DummyData.banners.size) { it.toString() })
    }


}