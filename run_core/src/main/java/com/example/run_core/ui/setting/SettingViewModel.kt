package com.example.run_core.ui.setting

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.run_core.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SettingViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
    private val _updateEmailSuccess = MutableLiveData<Boolean>()
    val updateEmailSuccess: LiveData<Boolean> = _updateEmailSuccess

    private val _changePasswordSuccess = MutableLiveData<Boolean>()
    val changePasswordSuccess: LiveData<Boolean> = _changePasswordSuccess


    private val _updateImageSuccess = MutableLiveData<String?>()
    val updateImageSuccess: LiveData<String?> = _updateImageSuccess

    fun updateEmail(newEmail: String) {
        viewModelScope.launch {
            val result = authRepository.updateEmail(newEmail)
            if (result.isSuccess) {
                _updateEmailSuccess.value = true

            } else {
                _updateEmailSuccess.value = false

            }
        }
    }

    fun changePassword(newPassword: String) {
        viewModelScope.launch {
            val result = authRepository.changePassword(newPassword)
            if (result.isSuccess) {
                _changePasswordSuccess.value = true

            } else {
                _changePasswordSuccess.value = false
            }
        }
    }

    fun updateProfileImage(uid: String, imageUri: Uri) {
        viewModelScope.launch {
            val result = authRepository.updateProfileImage(uid, imageUri)
            if (result.isSuccess) {
                _updateImageSuccess.value = result.getOrNull()
            } else {
            }
        }
    }
}