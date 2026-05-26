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
        return apiService.getAnnouncements(token)
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
        return apiService.createAnnouncement(token, title, content, category, image)
    }

    /**
     * Menghapus pengumuman dari server menggunakan token JWT.
     */
    suspend fun deleteAnnouncement(token: String, id: Int): BaseResponse<Unit> {
        return apiService.deleteAnnouncement(token, id)
    }
}
