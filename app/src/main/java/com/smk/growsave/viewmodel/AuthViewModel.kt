package com.smk.growsave.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smk.growsave.model.BaseResponse
import com.smk.growsave.model.auth.LoginRequest
import com.smk.growsave.model.auth.LoginResponse
import com.smk.growsave.model.auth.RegisterRequest
import com.smk.growsave.repository.AuthRepository
import kotlinx.coroutines.launch

/**
 * AuthViewModel mengelola state UI dan logika bisnis untuk fitur Autentikasi.
 * Menggunakan LiveData untuk mengalirkan perubahan state (loading, success, error) ke View.
 */
class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    // === LOGIN ===
    private val _loginResult = MutableLiveData<BaseResponse<LoginResponse>>()
    val loginResult: LiveData<BaseResponse<LoginResponse>> get() = _loginResult

    // === REGISTER ===
    private val _registerResult = MutableLiveData<BaseResponse<LoginResponse>>()
    val registerResult: LiveData<BaseResponse<LoginResponse>> get() = _registerResult

    // === LOADING & ERROR (dipakai bersama oleh login dan register) ===
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    /**
     * Fungsi login yang dipicu dari LoginActivity.
     */
    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = LoginRequest(email, password)
                val response = repository.login(request)
                _loginResult.value = response
            } catch (e: Exception) {
                _errorMessage.value = "Koneksi gagal: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Fungsi register yang dipicu dari RegisterActivity.
     * Role default = "user".
     */
    fun register(name: String, email: String, password: String, passwordConfirm: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = RegisterRequest(name, email, password, passwordConfirm)
                val response = repository.register(request)
                _registerResult.value = response
            } catch (e: Exception) {
                _errorMessage.value = "Koneksi gagal: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

