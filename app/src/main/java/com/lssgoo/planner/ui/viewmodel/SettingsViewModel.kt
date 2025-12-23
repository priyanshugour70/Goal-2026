package com.lssgoo.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.AppSettings
import com.lssgoo.planner.data.model.UserProfile
import com.lssgoo.planner.data.remote.S3Manager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Settings and Profile management
 */
class SettingsViewModel(application: Application) : BaseViewModel(application) {
    
    private val storageManager = LocalStorageManager(application)
    private val s3Manager = S3Manager(application)
    
    private val _settings = MutableStateFlow(storageManager.getSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()
    
    private val _userProfile = MutableStateFlow(storageManager.getUserProfile() ?: UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()
    
    fun updateSettings(settings: AppSettings) {
        viewModelScope.launch {
            storageManager.saveSettings(settings)
            _settings.value = settings
        }
    }
    
    fun saveUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            storageManager.saveUserProfile(profile)
            _userProfile.value = profile
        }
    }
    
    fun syncToCloud() {
        viewModelScope.launch(Dispatchers.IO) {
            _isSyncing.value = true
            try {
                val appData = "TODO: Serialize all data" // Placeholder for now
                s3Manager.uploadToS3(appData)
                showSnackbar("Sync successful")
            } catch (e: Exception) {
                showSnackbar("Sync failed: ${e.message}")
            } finally {
                _isSyncing.value = false
            }
        }
    }
}
