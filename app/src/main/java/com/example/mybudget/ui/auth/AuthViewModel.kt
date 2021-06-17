package com.example.mybudget.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.mybudget.data.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    val signInEvent: MediatorLiveData<AuthResource<FirebaseUser>> = MediatorLiveData()
    val checkFirstInitEvent: MediatorLiveData<Boolean> = MediatorLiveData()

    init {
        signInEvent.addSource(userRepository.checkUser()) {
            signInEvent.value = it
        }
    }

    fun checkFirstInit() {
        checkFirstInitEvent.addSource(userRepository.checkFirstInit()) {
            checkFirstInitEvent.value = it
        }
    }

    fun authWithGoogle(account: GoogleSignInAccount) {
        signInEvent.addSource(userRepository.authWithGoogle(account)) {
            signInEvent.postValue(it)
        }
    }
}