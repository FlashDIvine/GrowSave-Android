package com.smk.growsave

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.smk.growsave.databinding.ActivitySplashBinding
import com.smk.growsave.utils.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * SplashActivity menampilkan layar pembuka selama 2-3 detik saat aplikasi dijalankan.
 * Melakukan pengecekan sesi login pengguna menggunakan SessionManager:
 * - Jika sudah login, langsung mengarahkan ke MainActivity.
 * - Jika belum login, mengarahkan ke LoginActivity.
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
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
    }

    /**
     * Memeriksa status login pengguna dan mengarahkan ke aktivitas yang sesuai.
     */
    private fun checkLoginSession() {
        if (sessionManager.isLoggedIn()) {
            // Sudah login -> Pindah ke MainActivity
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
        } else {
            // Belum login -> Pindah ke LoginActivity
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
        }
        finish() // Menutup SplashActivity dari backstack
    }
}
