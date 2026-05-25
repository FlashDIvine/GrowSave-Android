package com.smk.growsave.repository

import com.smk.growsave.model.BaseResponse
import com.smk.growsave.model.CreateTransactionRequest
import com.smk.growsave.model.Transaction
import com.smk.growsave.network.ApiService
import com.smk.growsave.network.RetrofitClient

/**
 * TransactionRepository bertindak sebagai mediator antara sumber data (API) dan ViewModel.
 */
class TransactionRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {
    /**
     * Mengambil daftar transaksi dari server.
     * Mengirimkan token dengan format Bearer token.
     */
    suspend fun getTransactions(token: String): BaseResponse<List<Transaction>> {
        val authHeader = "Bearer $token"
        return apiService.getTransactions(authHeader)
    }

    /**
     * Membuat transaksi baru di server.
     */
    suspend fun createTransaction(token: String, request: CreateTransactionRequest): BaseResponse<Transaction> {
        val authHeader = "Bearer $token"
        return apiService.createTransaction(authHeader, request)
    }
}
