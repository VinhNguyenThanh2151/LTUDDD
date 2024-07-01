package com.example.run_core.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.example.run_core.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<FirebaseUser>>()
    val loginResult: LiveData<Result<FirebaseUser>> get() = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
          val result =  authRepository.loginUser(email, password)
            _loginResult.postValue(result)
        }
    }

}