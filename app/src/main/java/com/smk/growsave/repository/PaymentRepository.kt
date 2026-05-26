package com.smk.growsave.repository

import com.smk.growsave.model.BaseResponse
import com.smk.growsave.model.PaymentRequest
import com.smk.growsave.model.PaymentResponse
import com.smk.growsave.network.ApiService
import com.smk.growsave.network.RetrofitClient

/**
 * PaymentRepository mengelola pemanggilan API untuk transaksi pembayaran.
 */
class PaymentRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {
    /**
     * Membuat token transaksi pembayaran (Snap token) di backend.
     */
    suspend fun createPayment(token: String, billId: Int): BaseResponse<PaymentResponse> {
        val request = PaymentRequest(billId)
        return apiService.createPayment(token, request)
    }
}
