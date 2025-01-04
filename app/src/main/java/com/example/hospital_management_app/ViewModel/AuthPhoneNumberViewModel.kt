package com.example.hospital_management_app.ViewModel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hospital_management_app.Data.User
import com.example.hospital_management_app.Utilities.Resource
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AuthPhoneNumberViewModel @Inject constructor(
    val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : ViewModel() {

    private val _verificationId = MutableLiveData<String>()
    val verificationId: LiveData<String> get() = _verificationId

    private val _otpSent = MutableLiveData<Boolean>()
    val otpSent: LiveData<Boolean> get() = _otpSent

    private val _signInResult = MutableLiveData<Resource<FirebaseUser>>()
    val signInResult: LiveData<Resource<FirebaseUser>> get() = _signInResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    fun sendVerificationCode(phoneNumber: String, activity: Activity) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            credential.smsCode?.let {
                _verificationId.value = it
                viewModelScope.launch {
                    signInWithPhoneAuthCredential(credential)
                }
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            _errorMessage.value = when (e) {
                is FirebaseAuthInvalidCredentialsException -> "Invalid request"
                is FirebaseTooManyRequestsException -> "SMS quota exceeded"
                else -> "Verification failed"
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            _verificationId.value = verificationId
            resendToken = token
            _otpSent.value = true
        }
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        _signInResult.value =
            Resource.Loading()  // Set loading state before starting sign-in process
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = auth.signInWithCredential(credential).await()
                val user = result.user
                if (user != null) {
                    checkUserInFirestore(user)
                } else {
                    _signInResult.postValue(Resource.Error("User is null"))
                }
            } catch (e: Exception) {
                _signInResult.postValue(Resource.Error(e.message ?: "Sign in failed"))
            }
        }
    }

    private fun checkUserInFirestore(user: FirebaseUser) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val document = firestore.collection("users").document(user.uid).get().await()
                if (document.exists()) {
                    _signInResult.postValue(Resource.Success(user))
                } else {
                    _signInResult.postValue(Resource.Error("User does not exist in Firestore"))
                }
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
                _signInResult.postValue(Resource.Error(e.message ?: "Failed to check user in Firestore"))
            }
        }
    }

    fun createUser(name: String, uid: String, phoneNumber: String) {
        val user = User(name, phoneNumber)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                firestore.collection("users").document(uid).set(user).await()
                // User creation successful
                _signInResult.postValue(Resource.Success(auth.currentUser!!))
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
            }
        }

    }
}
