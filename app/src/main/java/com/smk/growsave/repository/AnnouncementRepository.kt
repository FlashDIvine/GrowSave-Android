package com.smk.growsave.repository

import com.smk.growsave.model.Announcement
import com.smk.growsave.model.BaseResponse
import com.smk.growsave.network.ApiService
import com.smk.growsave.network.RetrofitClient

/**
 * AnnouncementRepository menjembatani pengambilan data pengumuman dari server ke ViewModel.
 */
class AnnouncementRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {
    /**
     * Mengambil daftar pengumuman dari server menggunakan token JWT.
     */
    suspend fun getAnnouncements(token: String): BaseResponse<List<Announcement>> {
        val authHeader = "Bearer $token"
        return apiService.getAnnouncements(authHeader)
    }
}
