package com.example.run_core.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.run_core.data.local.UserModel
import com.example.run_core.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class HomeViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
    private val _user = MutableLiveData<Result<UserModel>>()
    val user: LiveData<Result<UserModel>> = _user

    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> = _logoutSuccess

    fun fetchUserData(uid: String) {
        viewModelScope.launch {
            val result = authRepository.getUserData(uid)
            _user.postValue(result)

        }
    }

    fun logout() {
        val result = authRepository.logoutUser()
        if (result.isSuccess) {
            _logoutSuccess.value = true
        } else {
            _logoutSuccess.value = false
        }
    }
}