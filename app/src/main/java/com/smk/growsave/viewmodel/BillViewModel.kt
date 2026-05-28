package com.smk.growsave.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.smk.growsave.model.Bill
import com.smk.growsave.model.BillStats
import com.smk.growsave.model.CreateBillRequest
import com.smk.growsave.repository.BillRepository
import kotlinx.coroutines.launch

/**
 * BillViewModel mengelola data tagihan untuk dikonsumsi oleh FinanceFragment.
 *
 * Menyediakan:
 * - bills: daftar tagihan dari API
 * - billStats: statistik tagihan yang dihitung secara otomatis dari bills
 * - CRUD operations: fetch, create, complete, delete
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

    private val _createBillSuccess = MutableLiveData<Boolean>()
    val createBillSuccess: LiveData<Boolean> get() = _createBillSuccess

    private val _completeBillSuccess = MutableLiveData<Boolean>()
    val completeBillSuccess: LiveData<Boolean> get() = _completeBillSuccess

    private val _deleteBillSuccess = MutableLiveData<Boolean>()
    val deleteBillSuccess: LiveData<Boolean> get() = _deleteBillSuccess

    /**
     * Statistik tagihan yang dihitung otomatis dari daftar bills.
     *
     * Menggunakan LiveData.map() sehingga setiap kali _bills berubah,
     * statistik akan otomatis diperbarui tanpa query tambahan ke API.
     *
     * Definisi:
     * - totalBills: seluruh tagihan yang ada
     * - activeBills: tagihan dengan status "active" dan belum completed
     * - paidBills: tagihan yang sudah completed atau status bukan "active"
     * - totalUnpaidAmount: total nominal dari tagihan aktif yang belum selesai
     */
    val billStats: LiveData<BillStats> = _bills.map { bills ->
        BillStats(
            totalBills = bills.size,
            activeBills = bills.count { it.status.equals("active", ignoreCase = true) && !it.isCompleted },
            paidBills = bills.count { !it.status.equals("active", ignoreCase = true) || it.isCompleted },
            totalUnpaidAmount = bills
                .filter { it.status.equals("active", ignoreCase = true) && !it.isCompleted }
                .sumOf { it.amount }
        )
    }

    /**
     * Mengambil daftar tagihan dari repository.
     */
    fun fetchBills(token: String) {
        _isLoading.value = true
        _errorMessage.value = "" // Reset error sebelum fetch
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

    /**
     * Membuat tagihan baru.
     */
    fun createBill(token: String, title: String, amount: Long, dueDate: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = CreateBillRequest(title, null, 0L, amount, dueDate)
                val response = repository.createBill(token, request)
                if (response.success) {
                    _createBillSuccess.value = true
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
     * Membuat tagihan iuran baru dengan detail crowdfunding lengkap.
     */
    fun createCrowdfundBill(token: String, title: String, description: String?, targetAmount: Long, requiredAmount: Long, dueDate: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = CreateBillRequest(title, description, targetAmount, requiredAmount, dueDate)
                val response = repository.createBill(token, request)
                if (response.success) {
                    _createBillSuccess.value = true
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
     * Menyelesaikan/menutup tagihan iuran secara manual oleh Admin.
     */
    fun completeBill(token: String, billId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.completeBill(token, billId)
                if (response.success) {
                    _completeBillSuccess.value = true
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
     * Menghapus tagihan menggunakan token dan ID.
     */
    fun deleteBill(token: String, id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.deleteBill(token, id)
                if (response.success) {
                    _deleteBillSuccess.value = true
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

    fun resetCreateBillSuccess() {
        _createBillSuccess.value = false
    }

    fun resetCompleteBillSuccess() {
        _completeBillSuccess.value = false
    }

    fun resetDeleteBillSuccess() {
        _deleteBillSuccess.value = false
    }

    /**
     * Reset error message setelah ditampilkan.
     */
    fun clearError() {
        _errorMessage.value = ""
    }
}
