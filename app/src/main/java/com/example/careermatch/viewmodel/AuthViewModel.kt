package com.example.careermatch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        _loading.value = true
        _error.value = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                _loading.value = false
                if (it.isSuccessful) onSuccess()
                else _error.value = it.exception?.localizedMessage
            }
    }

    fun register(email: String, password: String, onSuccess: () -> Unit) {
        _loading.value = true
        _error.value = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                _loading.value = false
                if (it.isSuccessful) onSuccess()
                else _error.value = it.exception?.localizedMessage
            }
    }

    fun resetPassword(email: String, onSent: () -> Unit) {
        _loading.value = true
        _error.value = null

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                _loading.value = false
                if (it.isSuccessful) onSent()
                else _error.value = it.exception?.localizedMessage
            }
    }
}