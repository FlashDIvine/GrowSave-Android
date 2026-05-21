package com.smk.growsave.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smk.growsave.model.Bill
import com.smk.growsave.repository.BillRepository
import kotlinx.coroutines.launch

/**
 * BillViewModel mengelola data tagihan untuk dikonsumsi oleh BillsFragment.
 */
class BillViewModel(
    private val repository: BillRepository = BillRepository()
) : ViewModel() {

    private val _bills = MutableLiveData<List<Bill>>()
    val bills: LiveData<List<Bill>> get() = _bills

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    /**
     * Mengambil daftar tagihan dari repository.
     */
    fun fetchBills(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getBills(token)
                if (response.success) {
                    _bills.value = response.data ?: emptyList()
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
}
