package com.smk.growsave.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smk.growsave.model.Announcement
import com.smk.growsave.repository.AnnouncementRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

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

    private val _createAnnouncementSuccess = MutableLiveData<Boolean>()
    val createAnnouncementSuccess: LiveData<Boolean> get() = _createAnnouncementSuccess

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

    /**
     * Membuat pengumuman baru dengan multipart/form-data.
     */
    fun createAnnouncement(
        token: String,
        title: RequestBody,
        content: RequestBody,
        category: RequestBody?,
        image: MultipartBody.Part?
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.createAnnouncement(token, title, content, category, image)
                if (response.success) {
                    _createAnnouncementSuccess.value = true
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

    fun resetCreateAnnouncementSuccess() {
        _createAnnouncementSuccess.value = false
    }

    private val _deleteAnnouncementSuccess = MutableLiveData<Boolean>()
    val deleteAnnouncementSuccess: LiveData<Boolean> get() = _deleteAnnouncementSuccess

    /**
     * Menghapus pengumuman menggunakan token dan ID.
     */
    fun deleteAnnouncement(token: String, id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.deleteAnnouncement(token, id)
                if (response.success) {
                    _deleteAnnouncementSuccess.value = true
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

    fun resetDeleteAnnouncementSuccess() {
        _deleteAnnouncementSuccess.value = false
    }
}
