package com.smk.growsave.repository

import com.smk.growsave.model.BaseResponse
import com.smk.growsave.model.Bill
import com.smk.growsave.model.CreateBillRequest
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

    /**
     * Membuat tagihan baru di server menggunakan token JWT.
     */
    suspend fun createBill(token: String, request: CreateBillRequest): BaseResponse<Bill> {
        val authHeader = "Bearer $token"
        return apiService.createBill(authHeader, request)
    }

    /**
     * Menyelesaikan/menutup tagihan secara manual di server menggunakan token JWT.
     */
    suspend fun completeBill(token: String, billId: Int): BaseResponse<Bill> {
        val authHeader = "Bearer $token"
        return apiService.completeBill(authHeader, billId)
    }
}
