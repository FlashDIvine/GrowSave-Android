package com.smk.growsave.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smk.growsave.model.Announcement
import com.smk.growsave.repository.AnnouncementRepository
import kotlinx.coroutines.launch

/**
 * AnnouncementViewModel mengelola UI State data pengumuman untuk AnnouncementFragment.
 */
class AnnouncementViewModel(
    private val repository: AnnouncementRepository = AnnouncementRepository()
) : ViewModel() {

    private val _announcements = MutableLiveData<List<Announcement>>()
    val announcements: LiveData<List<Announcement>> get() = _announcements

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    /**
     * Mengambil data pengumuman dari repository.
     */
    fun fetchAnnouncements(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getAnnouncements(token)
                if (response.success) {
                    _announcements.value = response.data ?: emptyList()
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
