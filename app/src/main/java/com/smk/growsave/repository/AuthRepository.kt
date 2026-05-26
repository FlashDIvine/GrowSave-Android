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
        return apiService.getRoomRequests(token)
    }

    suspend fun getRoomResidents(token: String): BaseResponse<List<RoomMember>> {
        return apiService.getRoomResidents(token)
    }

    suspend fun approveRoom(token: String, id: Int): BaseResponse<Unit> {
        return apiService.approveRoom(token, id)
    }

    suspend fun rejectRoom(token: String, id: Int): BaseResponse<Unit> {
        return apiService.rejectRoom(token, id)
    }

    /**
     * Memvalidasi token ke backend.
     * Saat ini menggunakan GET /api/room karena endpoint khusus profile / me belum tersedia.
     * TODO: Jika backend sudah menyediakan endpoint auth khusus seperti /api/me atau /api/profile,
     *       ganti pemanggilan apiService.getRoom(token) di bawah ini menjadi endpoint tersebut.
     */
    suspend fun validateToken(token: String): Boolean {
        val response = apiService.getRoom(token)
        return response.success
    }
}

