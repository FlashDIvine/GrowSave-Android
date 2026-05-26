package com.smk.growsave

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.smk.growsave.databinding.ActivitySplashBinding
import com.smk.growsave.repository.AuthRepository
import com.smk.growsave.utils.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * SplashActivity menampilkan layar pembuka selama 2-3 detik saat aplikasi dijalankan.
 * Melakukan pengecekan sesi login pengguna menggunakan SessionManager:
 * - Jika belum login (token kosong), langsung mengarahkan ke LoginActivity.
 * - Jika sudah login, melakukan validasi token ke backend (online-only).
 *   - Jika valid, mengarahkan ke MainActivity.
 *   - Jika expired (401), membersihkan sesi dan mengarahkan ke LoginActivity.
 *   - Jika error jaringan / server down, menampilkan retry screen dan TIDAK masuk ke MainActivity.
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManager: SessionManager
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Menonaktifkan mode gelap di seluruh aplikasi secara programmatik
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        // Menggunakan ViewBinding untuk inflasi layout activity_splash
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi SessionManager
        sessionManager = SessionManager(this)

        // Menjalankan delay splash screen menggunakan Kotlin Coroutines
        lifecycleScope.launch {
            delay(2500) // Delay selama 2.5 detik
            checkLoginSession()
        }

        // Listener untuk tombol retry dan login kembali
        binding.btnRetry.setOnClickListener {
            lifecycleScope.launch {
                checkLoginSession()
            }
        }
        binding.btnToLogin.setOnClickListener {
            sessionManager.clearSession()
            goToLogin()
        }
    }

    /**
     * Memeriksa status login pengguna dan melakukan validasi token ke backend.
     * Menggunakan AuthRepository.validateToken (yang di dalamnya memanggil endpoint getRoom).
     */
    private suspend fun checkLoginSession() {
        val token = sessionManager.getToken()

        if (token.isNullOrEmpty()) {
            // Belum login -> langsung ke LoginActivity
            goToLogin()
            return
        }

        // Tampilkan loading area, sembunyikan error
        binding.layoutLoading.visibility = View.VISIBLE
        binding.layoutError.visibility = View.GONE

        try {
            // Validasi token secara online menggunakan repository
            val isValid = authRepository.validateToken(token)
            if (isValid) {
                // Token valid -> Masuk ke MainActivity
                goToMain()
            } else {
                // Response sukses tapi data menunjukkan kegagalan
                sessionManager.clearSession()
                goToLogin()
            }
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 401) {
                // Token expired/invalid -> Bersihkan sesi dan ke LoginActivity
                sessionManager.clearSession()
                goToLogin()
            } else {
                // HTTP error lain (500, 503, dll) -> Tampilkan retry screen
                showError("Gagal terhubung ke server (HTTP ${e.code()}). Silakan coba lagi.")
            }
        } catch (e: java.io.IOException) {
            // Error jaringan (timeout, DNS, server mati, offline) -> Tampilkan retry screen
            showError("Koneksi internet bermasalah. Periksa jaringan Anda.")
        } catch (e: Exception) {
            // Error tak terduga lainnya -> Tampilkan retry screen
            showError("Terjadi kesalahan sistem: ${e.message}")
        }
    }

    private fun showError(message: String) {
        binding.layoutLoading.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
        binding.tvErrorMessage.text = message
    }

    private fun goToMain() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToLogin() {
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
