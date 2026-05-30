package com.smk.growsave.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData

/**
 * SessionManager bertugas menyimpan dan menghapus sesi data pengguna
 * (seperti token JWT dan informasi profil) di SharedPreferences secara aman.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        private const val PREF_NAME = "growsave_session"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_STATUS = "user_status"

        /**
         * LiveData global untuk mendeteksi sesi yang expired (401).
         * Di-observe oleh MainActivity untuk redirect ke LoginActivity.
         * Menggunakan wrapper Event agar one-shot dan tidak terpicu ulang saat konfigurasi berubah.
         */
        val isSessionExpired = MutableLiveData<Event<Boolean>>()
    }

    /**
     * Menyimpan data sesi setelah login berhasil.
     */
    fun saveSession(token: String, name: String, email: String, role: String) {
        editor.putString(KEY_TOKEN, token)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_USER_NAME, name)
        editor.putString(KEY_USER_EMAIL, email)
        editor.putString(KEY_USER_ROLE, role)
        editor.apply() // Simpan secara asinkronus (background thread)
    }

    /**
     * Mengambil JWT Token yang tersimpan.
     */
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    /**
     * Menyimpan atau memperbarui JWT Token secara independen.
     */
    fun saveToken(token: String) {
        editor.putString(KEY_TOKEN, token)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    /**
     * Memeriksa apakah user sudah login.
     * Digunakan untuk Auto-Login.
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Mengambil nama pengguna yang sedang masuk.
     */
    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    /**
     * Mengambil role pengguna yang sedang masuk.
     */
    fun getUserRole(): String? {
        return prefs.getString(KEY_USER_ROLE, null)
    }

    /**
     * Mengambil email pengguna yang sedang masuk.
     */
    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    /**
     * Menyimpan status approval pengguna.
     */
    fun saveUserStatus(status: String) {
        editor.putString(KEY_USER_STATUS, status)
        editor.apply()
    }

    /**
     * Mengambil status approval pengguna.
     */
    fun getUserStatus(): String? {
        return prefs.getString(KEY_USER_STATUS, null)
    }

    /**
     * Menghapus sesi data saat user melakukan Logout.
     */
    fun clearSession() {
        editor.clear()
        editor.apply()
    }
}
