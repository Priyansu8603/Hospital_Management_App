package com.example.hospital_management_app.ViewModel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.hospital_management_app.Data.User
import com.example.hospital_management_app.HospitalApplication
import com.example.hospital_management_app.Utilities.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth, // Inject FirebaseAuth to get the current user's UID
    private val storage: FirebaseStorage,
    app: Application
) : AndroidViewModel(app) {

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
        viewModelScope.launch {
            _profile.value = Resource.Loading()
        }
        firestore.collection("users")
            .document(auth.uid!!)
            .get().addOnSuccessListener {
                val user = it.toObject(User::class.java)
                user?.let {
                    viewModelScope.launch {
                        Log.d("ProfileViewModel", "User data: $user")
                        _profile.value = Resource.Success(user!!)
                    }
                }
            }.addOnFailureListener {
                _profile.value = Resource.Error(it.message.toString())
            }
    }

    fun updateProfile(user: User) {

        val areInputsValid = user.name?.trim()?.isNotEmpty() == true
                && user.dob?.isNotEmpty() == true
                && user.address?.isNotEmpty() == true

        if (!areInputsValid) {
            viewModelScope.launch {
                _updateInfo.value = Resource.Error("Please fill all the fields")
            }
            return
        }

        viewModelScope.launch {
            _updateInfo.value = Resource.Loading()
        }

        saveUserInformation(user)

    }

    fun saveImage(imageUri: Uri?) {
        viewModelScope.launch {
            try {
                val imageBitmap = MediaStore.Images.Media.getBitmap(
                    getApplication<HospitalApplication>().contentResolver,
                    imageUri
                )
                val byteArrayOutputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val imageByteArray = byteArrayOutputStream.toByteArray()
                val imageDirectory =
                    storage.reference.child("profileImages/${auth.uid!!}/${UUID.randomUUID()}")
                val result = imageDirectory.putBytes(imageByteArray).await()
                val imageUrl = result.storage.downloadUrl.await().toString()

                // Step 5: Update the user profile with the new image URL
                firestore.collection("users")
                    .document(auth.uid!!)
                    .update("profileImageUrl", imageUrl)
                    .addOnSuccessListener {
                        // Update _profileImageUri LiveData to reflect the new image
                        val uri = Uri.parse(imageUrl)
                        _profileImageUri.postValue(uri)
                        viewModelScope.launch {
                            _updateInfo.value = Resource.Success(User(profileImage = imageUrl))
                        }
                    }
                    .addOnFailureListener { exception ->
                        viewModelScope.launch {
                            _updateInfo.value = Resource.Error("Failed to update profile image: ${exception.message}")
                        }
                    }


            } catch (e: Exception) {
                viewModelScope.launch {
                    _updateInfo.value = Resource.Error(e.message.toString())
                }
            }
        }
    }

    private fun saveUserInformation(user: User) {
        firestore.runTransaction { transaction ->
            val documentRef = firestore.collection("users").document(auth.uid!!)
            transaction.set(documentRef, user)

        }.addOnSuccessListener {
            viewModelScope.launch {
                _updateInfo.value = Resource.Success(user)
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _updateInfo.value = Resource.Error(it.message.toString())
            }
        }

    }
}

