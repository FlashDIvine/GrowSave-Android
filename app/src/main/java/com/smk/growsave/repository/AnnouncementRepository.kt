package com.smk.growsave.repository

import com.smk.growsave.model.Announcement
import com.smk.growsave.model.BaseResponse
import com.smk.growsave.network.ApiService
import com.smk.growsave.network.RetrofitClient
import okhttp3.MultipartBody
import okhttp3.RequestBody

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

    /**
     * Membuat pengumuman baru dengan multipart/form-data.
     */
    suspend fun createAnnouncement(
        token: String,
        title: RequestBody,
        content: RequestBody,
        category: RequestBody?,
        image: MultipartBody.Part?
    ): BaseResponse<Announcement> {
        val authHeader = "Bearer $token"
        return apiService.createAnnouncement(authHeader, title, content, category, image)
    }
}
