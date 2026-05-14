package com.example.arogyasahaya3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val uid: String,
    val name: String = "",
    val age: String = "",
    val gender: String = "",
    val bloodGroup: String = "",
    val chronicDiseases: String = "",
    val emergencyContact: String = ""
)
