package com.smk.growsave.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smk.growsave.model.CreateTransactionRequest
import com.smk.growsave.model.Transaction
import com.smk.growsave.repository.TransactionRepository
import kotlinx.coroutines.launch

/**
 * TransactionViewModel mengelola data transaksi untuk ditampilkan di UI (TransactionFragment).
 */
class TransactionViewModel(
    private val repository: TransactionRepository = TransactionRepository()
) : ViewModel() {

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _createTransactionSuccess = MutableLiveData<Boolean>()
    val createTransactionSuccess: LiveData<Boolean> get() = _createTransactionSuccess

    /**
     * Meminta daftar transaksi dari repositori menggunakan token JWT.
     */
    fun fetchTransactions(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getTransactions(token)
                if (response.success) {
                    _transactions.value = response.data ?: emptyList()
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
     * Membuat transaksi baru.
     */
    fun createTransaction(token: String, title: String, type: String, amount: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = CreateTransactionRequest(title, type, amount)
                val response = repository.createTransaction(token, request)
                if (response.success) {
                    _createTransactionSuccess.value = true
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

    fun resetCreateTransactionSuccess() {
        _createTransactionSuccess.value = false
    }
}
