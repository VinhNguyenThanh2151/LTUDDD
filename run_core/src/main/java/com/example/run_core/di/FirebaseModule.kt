package com.example.run_core.di



import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.example.run_core.repository.AuthRepository
import com.example.run_core.repository.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    fun provideAuthRepository(firebaseAuth: FirebaseAuth, firebaseStorage: FirebaseStorage, firebaseDatabase: FirebaseDatabase): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, firebaseStorage, firebaseDatabase)
    }
}