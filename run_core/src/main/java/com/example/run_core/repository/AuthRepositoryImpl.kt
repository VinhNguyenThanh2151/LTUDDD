package com.example.run_core.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.example.run_core.data.local.UserModel
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthRepositoryImpl(
    val auth: FirebaseAuth, val storageRef: FirebaseStorage, val firebaseDatabase: FirebaseDatabase
) : AuthRepository {
    override suspend fun signUpUser(
        userName: String,
        email: String,
        password: String,
        imageUri: Uri
    ): Result<FirebaseUser> {
        return suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            uploadImageToFirebaseStorage(imageUri, userId, email, userName)
                        }
                        continuation.resume(Result.success(auth.currentUser!!))
                    } else {
                        Log.e("TAG", "Lỗi tạo người dùng: ${task.exception}")
                        continuation.resumeWithException(task.exception ?: IllegalStateException("Lỗi không xác định"))
                    }
                }
        }
    }


    override suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        continuation.resume(Result.success(user))
                    } else {
                        continuation.resumeWithException(IllegalStateException("Người dùng là null"))
                    }
                } else {
                    continuation.resumeWithException(task.exception ?: IllegalStateException("Lỗi không xác định"))
                }
            }
        }
    }

    override suspend fun getUserData(uid: String): Result<UserModel> {
        return try {
            val dataSnapshot = firebaseDatabase.getReference("users").child(uid).get().await()
            val userModel = dataSnapshot.getValue(UserModel::class.java)
            userModel?.let {
                Result.success(it)
            } ?: Result.failure(Exception("User not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logoutUser(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEmail(newEmail: String): Result<Unit> {
        return try {
            val user = auth.currentUser
            user?.updateEmail(newEmail)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changePassword(newPassword: String): Result<Unit> {
        return try {
            val user = auth.currentUser
            user?.updatePassword(newPassword)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfileImage(uid: String, imageUri: Uri): Result<String> {
        return try {
            val imageRef = storageRef.reference.child("profile_images/$uid.jpg")
            val uploadTask = imageRef.putFile(imageUri).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()

            // Update the user profile image URL in the database
            firebaseDatabase.getReference("users").child(uid).child("image").setValue(downloadUrl).await()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    private fun uploadImageToFirebaseStorage(
        imageUri: Uri,
        userId: String,
        email: String,
        userName: String
    ) {
        val imageRef = storageRef.reference.child("profile_images/${userId}.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                Log.d("TAG", "Image uploaded successfully")
                it.storage.downloadUrl.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val imageUrl = task.result.toString()
                        saveUserToDatabase(userId, imageUrl, email, userName)
                    }
                }
                    .addOnFailureListener { exception ->
                        Log.e("TAG", "Error uploading image: $exception")
                    }
            }
    }

    private fun saveUserToDatabase(
        userId: String,
        imageUrl: String,
        email: String,
        userName: String
    ) {
        checkEmailExists(email,
            onSuccess = { emailExists ->
                if (!emailExists) {
                    val usersRef = firebaseDatabase.reference.child("users")
                    val userRef = usersRef.child(userId)
                    val userModel = UserModel(email, userName, imageUrl, true)
                    userRef.setValue(userModel)
                        .addOnSuccessListener {
                            Log.d("TAG", "User saved successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("TAG", "Error saving user: $e")
                        }
                } else {
                    Log.e("TAG", "Email đã tồn tại trong cơ sở dữ liệu")
                }
            },
            onFailure = { e ->
                Log.e("TAG", "Lỗi khi kiểm tra email: $e")
            }
        )
    }

    private fun checkEmailExists(email: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        val usersRef = firebaseDatabase.reference.child("users")
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    onSuccess(true) // Email tồn tại
                } else {
                    onSuccess(false) // Email không tồn tại
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.toException()) // Xử lý khi có lỗi xảy ra
            }
        })
    }
}


