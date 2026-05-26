package com.smk.growsave.repository

import com.smk.growsave.model.BaseResponse
import com.smk.growsave.model.RoomRequest
import com.smk.growsave.model.RoomMember
import com.smk.growsave.model.auth.LoginRequest
import com.smk.growsave.model.auth.LoginResponse
import com.smk.growsave.model.auth.RegisterRequest
import com.smk.growsave.network.ApiService
import com.smk.growsave.network.RetrofitClient

/**
 * Repository bertindak sebagai layer abstraksi yang mengelola sumber data (Network/Database).
 * Menghubungkan ViewModel dengan API Service agar ViewModel tidak berhubungan langsung dengan Retrofit.
 */
class AuthRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    /**
     * Fungsi login untuk memanggil API.
     */
    suspend fun login(request: LoginRequest): BaseResponse<LoginResponse> {
        return apiService.login(request)
    }

    /**
     * Fungsi register untuk mendaftarkan pengguna baru.
     * Response identik dengan login (berisi token dan user).
     */
    suspend fun register(request: RegisterRequest): BaseResponse<LoginResponse> {
        return apiService.register(request)
    }

    suspend fun getRoomRequests(token: String): BaseResponse<List<RoomRequest>> {
        val authHeader = "Bearer $token"
        return apiService.getRoomRequests(authHeader)
    }

    suspend fun getRoomResidents(token: String): BaseResponse<List<RoomMember>> {
        val authHeader = "Bearer $token"
        return apiService.getRoomResidents(authHeader)
    }

    suspend fun approveRoom(token: String, id: Int): BaseResponse<Unit> {
        val authHeader = "Bearer $token"
        return apiService.approveRoom(authHeader, id)
    }

    suspend fun rejectRoom(token: String, id: Int): BaseResponse<Unit> {
        val authHeader = "Bearer $token"
        return apiService.rejectRoom(authHeader, id)
    }
}

