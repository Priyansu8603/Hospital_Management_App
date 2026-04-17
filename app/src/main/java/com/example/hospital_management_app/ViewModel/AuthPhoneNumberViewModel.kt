package com.example.hospital_management_app.ViewModel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hospital_management_app.Data.User
import com.example.hospital_management_app.Utilities.Resource

// Hilt/Firebase/Phone auth removed for demo build
// @HiltViewModel
// class AuthPhoneNumberViewModel @Inject constructor(val auth: FirebaseAuth, ...) : ViewModel()
class AuthPhoneNumberViewModel : ViewModel() {

    private val _verificationId = MutableLiveData<String>()
    val verificationId: LiveData<String> get() = _verificationId

    private val _otpSent = MutableLiveData<Boolean>()
    val otpSent: LiveData<Boolean> get() = _otpSent

    // For demo: always return success with a fake user
    private val _signInResult = MutableLiveData<Resource<com.google.firebase.auth.FirebaseUser>>()
    val signInResult: LiveData<Resource<com.google.firebase.auth.FirebaseUser>> get() = _signInResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun sendVerificationCode(phoneNumber: String, activity: Activity) {
        // Demo: simulate OTP sent
        _verificationId.value = "DEMO_VERIFICATION_ID"
        _otpSent.value = true
    }

    fun signInWithPhoneAuthCredential(credential: com.google.firebase.auth.PhoneAuthCredential) {
        // Demo: do nothing, navigation handled manually
    }
}
