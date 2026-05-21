package com.smk.growsave.repository

import com.smk.growsave.model.BaseResponse
import com.smk.growsave.model.Bill
import com.smk.growsave.network.ApiService
import com.smk.growsave.network.RetrofitClient

/**
 * BillRepository menghubungkan pemanggilan API tagihan (bills) ke ViewModel.
 */
class BillRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {
    /**
     * Mengambil daftar tagihan dari server menggunakan token JWT.
     */
    suspend fun getBills(token: String): BaseResponse<List<Bill>> {
        val authHeader = "Bearer $token"
        return apiService.getBills(authHeader)
    }
}
