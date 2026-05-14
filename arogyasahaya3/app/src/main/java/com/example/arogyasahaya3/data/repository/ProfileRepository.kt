package com.example.arogyasahaya3.data.repository

import com.example.arogyasahaya3.data.local.ProfileDao
import com.example.arogyasahaya3.data.local.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepository(private val profileDao: ProfileDao) {
    private val firestore = FirebaseFirestore.getInstance()
    private val profilesCollection = firestore.collection("profiles")

    suspend fun saveProfile(profile: UserProfile) {
        // Save to Local Room Database
        profileDao.insertOrUpdateProfile(profile)
        
        // Save to Firebase Firestore
        try {
            profilesCollection.document(profile.uid).set(profile).await()
        } catch (e: Exception) {
            android.util.Log.e("ProfileRepo", "Error saving to Firestore", e)
            // We still have it locally, so we don't necessarily throw here 
            // unless we want to force cloud sync success
        }
    }

    suspend fun getProfile(uid: String): UserProfile? {
        // Try local first
        var profile = profileDao.getProfile(uid)
        
        if (profile == null) {
            // Try fetching from Firestore
            try {
                val snapshot = profilesCollection.document(uid).get().await()
                profile = snapshot.toObject(UserProfile::class.java)
                
                // If found in Firestore, cache it locally
                profile?.let {
                    profileDao.insertOrUpdateProfile(it)
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileRepo", "Error fetching from Firestore", e)
            }
        }
        
        return profile
    }
}
