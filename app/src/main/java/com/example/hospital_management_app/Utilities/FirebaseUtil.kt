package com.example.hospital_management_app.Utilities

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseUtil {

    private const val COLLECTION_PATH = "users"

    // Get the current user ID
    fun currentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    // Check if the user is logged in
    fun isLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    // Get the current user details document reference
    fun currentUserDetails(): DocumentReference? {
        val userId = currentUserId()
        return if (userId != null) {
            FirebaseFirestore.getInstance().collection(COLLECTION_PATH).document(userId)
        } else {
            null
        }
    }
}
