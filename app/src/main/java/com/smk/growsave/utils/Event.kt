package com.smk.growsave.utils

/**
 * Event wrapper untuk data yang dikirim melalui LiveData agar hanya dikonsumsi sekali (one-shot).
 * Sangat penting untuk mencegah re-trigger event navigasi saat terjadi configuration change (misal rotasi layar).
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Mengizinkan pembacaan dari luar, tetapi modifikasi hanya dari dalam

    /**
     * Mengembalikan konten jika belum ditangani, dan menandainya telah ditangani.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Mengembalikan konten bahkan jika konten tersebut sudah ditangani.
     */
    fun peekContent(): T = content
}
