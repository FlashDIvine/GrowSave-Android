package com.smk.growsave.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smk.growsave.model.BaseResponse
import com.smk.growsave.model.RoomRequest
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

    // === ROOM REQUESTS ===
    private val _roomRequests = MutableLiveData<List<RoomRequest>>()
    val roomRequests: LiveData<List<RoomRequest>> get() = _roomRequests

    private val _roomActionSuccess = MutableLiveData<Boolean>()
    val roomActionSuccess: LiveData<Boolean> get() = _roomActionSuccess

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
            } catch (e: retrofit2.HttpException) {
                _errorMessage.value = parseError(e)
            } catch (e: Exception) {
                _errorMessage.value = "Koneksi gagal: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Fungsi register yang dipicu dari RegisterActivity atau RegisterAdminActivity.
     * Mendukung data opsional seperti role dan roomCode.
     */
    fun register(
        name: String,
        email: String,
        password: String,
        role: String = "user",
        roomCode: String? = null
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = RegisterRequest(
                    name = name,
                    email = email,
                    password = password,
                    role = role,
                    roomCode = roomCode
                )
                val response = repository.register(request)
                _registerResult.value = response
            } catch (e: retrofit2.HttpException) {
                _errorMessage.value = parseError(e)
            } catch (e: Exception) {
                _errorMessage.value = "Koneksi gagal: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Helper untuk memparsing error response dari Laravel (misal 422 validation error).
     */
    private fun parseError(e: retrofit2.HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            if (!errorBody.isNullOrEmpty()) {
                val jsonObject = org.json.JSONObject(errorBody)
                val message = jsonObject.optString("message", "Terjadi kesalahan")
                val errors = jsonObject.optJSONObject("errors")
                if (errors != null) {
                    val keys = errors.keys()
                    if (keys.hasNext()) {
                        val firstKey = keys.next()
                        val firstErrorArray = errors.optJSONArray(firstKey)
                        if (firstErrorArray != null && firstErrorArray.length() > 0) {
                            firstErrorArray.getString(0)
                        } else {
                            message
                        }
                    } else {
                        message
                    }
                } else {
                    message
                }
            } else {
                "Error: ${e.message()}"
            }
        } catch (jsonEx: Exception) {
            "Error: ${e.localizedMessage}"
        }
    }

    /**
     * Mengambil daftar permohonan room pending.
     */
    fun fetchRoomRequests(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getRoomRequests(token)
                if (response.success) {
                    _roomRequests.value = response.data ?: emptyList()
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Koneksi gagal: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Menyetujui permohonan room.
     */
    fun approveRoom(token: String, id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.approveRoom(token, id)
                if (response.success) {
                    _roomActionSuccess.value = true
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Koneksi gagal: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Menolak permohonan room.
     */
    fun rejectRoom(token: String, id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.rejectRoom(token, id)
                if (response.success) {
                    _roomActionSuccess.value = true
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Koneksi gagal: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetRoomActionSuccess() {
        _roomActionSuccess.value = false
    }
}

