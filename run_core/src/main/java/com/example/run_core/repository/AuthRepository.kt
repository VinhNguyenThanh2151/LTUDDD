package com.example.run_core.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.example.run_core.data.local.UserModel

interface AuthRepository {
    suspend fun signUpUser(userName: String,email: String, password: String, imageUri: Uri) : Result<FirebaseUser>
    suspend fun loginUser(email: String, password: String) : Result<FirebaseUser>
    suspend fun getUserData(uid: String): Result<UserModel>
    fun logoutUser(): Result<Unit>
    suspend fun updateEmail(newEmail: String): Result<Unit>
    suspend fun changePassword(newPassword: String): Result<Unit>
    suspend fun updateProfileImage(uid: String, imageUri: Uri): Result<String>
}