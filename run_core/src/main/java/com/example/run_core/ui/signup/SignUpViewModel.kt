package com.example.run_core.ui.signup

import android.net.Uri
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
class SignUpViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
    private val _signUpResult = MutableLiveData<Result<FirebaseUser>>()
    val signUpResult: LiveData<Result<FirebaseUser>> get() = _signUpResult

    fun signUp(userName: String,email: String, password: String, imageUri: Uri) {
        viewModelScope.launch {
            val result = authRepository.signUpUser(userName ,email, password, imageUri)
            _signUpResult.postValue(result)
        }
    }
}