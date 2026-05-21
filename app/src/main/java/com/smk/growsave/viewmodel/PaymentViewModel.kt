package com.smk.growsave.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smk.growsave.repository.PaymentRepository
import kotlinx.coroutines.launch

/**
 * PaymentViewModel mengelola pengambilan snap token transaksi pembayaran.
 */
class PaymentViewModel(
    private val repository: PaymentRepository = PaymentRepository()
) : ViewModel() {

    private val _snapToken = MutableLiveData<String?>()
    val snapToken: LiveData<String?> get() = _snapToken

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    /**
     * Meminta snap token dari backend untuk tagihan tertentu.
     */
    fun createPayment(token: String, billId: Int) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val response = repository.createPayment(token, billId)
                if (response.success) {
                    _snapToken.value = response.data?.snapToken
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
     * Membersihkan snap token setelah digunakan untuk mencegah peluncuran ulang activity.
     */
    fun clearSnapToken() {
        _snapToken.value = null
    }

    /**
     * Membersihkan error message.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
