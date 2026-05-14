package com.example.arogyasahaya3.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyasahaya3.data.local.AppDatabase
import com.example.arogyasahaya3.data.local.UserProfile
import com.example.arogyasahaya3.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProfileRepository(AppDatabase.getDatabase(application).profileDao())
    
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    fun loadProfile(uid: String) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val profile = repository.getProfile(uid)
            _uiState.value = ProfileUiState.Success(profile ?: UserProfile(uid = uid))
        }
    }

    fun saveProfile(profile: UserProfile) {
        android.util.Log.d("ProfileVM", "saveProfile called for: ${profile.name}")
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                repository.saveProfile(profile)
                _uiState.value = ProfileUiState.Success(profile)
                _events.emit(ProfileEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Failed to save profile")
            }
        }
    }
}

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val profile: UserProfile) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

sealed interface ProfileEvent {
    data object SaveSuccess : ProfileEvent
}
