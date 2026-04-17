package com.example.hospital_management_app.ViewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.hospital_management_app.Data.User
import com.example.hospital_management_app.Data.DummyData
import com.example.hospital_management_app.Utilities.Resource

// Hilt/Firebase removed for demo build
// @HiltViewModel
// class ProfileViewModel @Inject constructor(...) : AndroidViewModel(app)
class ProfileViewModel(app: Application) : AndroidViewModel(app) {

    private val _profile = MutableLiveData<Resource<User>>()
    val profile: LiveData<Resource<User>> = _profile

    private val _updateInfo = MutableLiveData<Resource<User>>()
    val updateInfo: LiveData<Resource<User>> = _updateInfo

    private val _profileImageUri = MutableLiveData<Uri?>()
    val profileImageUri: LiveData<Uri?> get() = _profileImageUri

    init {
        fetchProfile()
    }

    private fun fetchProfile() {
        _profile.value = Resource.Success(DummyData.dummyUser)
    }

    fun updateProfile(user: User) {
        val areInputsValid = user.name?.trim()?.isNotEmpty() == true
                && user.dob?.isNotEmpty() == true
                && user.address?.isNotEmpty() == true

        if (!areInputsValid) {
            _updateInfo.value = Resource.Error("Please fill all the fields")
            return
        }
        _updateInfo.value = Resource.Success(user)
    }

    fun saveImage(uri: Uri?) {
        _profileImageUri.value = uri
    }
}
