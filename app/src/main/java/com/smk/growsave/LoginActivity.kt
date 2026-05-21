package com.smk.growsave

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.smk.growsave.databinding.ActivityLoginBinding
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.viewmodel.AuthViewModel

/**
 * LoginActivity menangani antarmuka pengguna untuk proses login.
 * Menghubungkan input pengguna ke ViewModel dan mengamati perubahan state (loading, success, error).
 */
class LoginActivity : AppCompatActivity() {

    // Menginisialisasi ViewBinding untuk menggantikan findViewById
    private lateinit var binding: ActivityLoginBinding

    // Deklarasi SessionManager untuk menyimpan status login
    private lateinit var sessionManager: SessionManager

    // Menginisialisasi ViewModel menggunakan properti delegasi 'by viewModels()'
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi SessionManager
        sessionManager = SessionManager(this)

        // Cek Auto-Login: Jika sudah login, langsung ke MainActivity
        if (sessionManager.isLoggedIn()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Setup ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Event Listener saat tombol login ditekan
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Validasi input sederhana sebelum mengirim ke server
            if (email.isEmpty()) {
                binding.etEmail.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.etPassword.error = "Password tidak boleh kosong"
                return@setOnClickListener
            }

            // Memicu proses login pada ViewModel
            viewModel.login(email, password)
        }

        // Link menuju halaman Register
        binding.tvGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Memulai pengamatan data (Observe LiveData)
        setupObservers()
    }

    /**
     * Mengamati perubahan state yang dipaparkan oleh AuthViewModel.
     */
    private fun setupObservers() {
        
        // 1. Mengamati Status Loading (Menampilkan / Menyembunyikan ProgressBar)
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnLogin.isEnabled = false // Nonaktifkan tombol agar tidak tertekan dua kali
            } else {
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true
            }
        }

        // 2. Mengamati Hasil Respon Login (Sukses / Gagal secara Logika Bisnis)
        viewModel.loginResult.observe(this) { response ->
            if (response.success) {
                val user = response.data?.user
                val token = response.data?.token

                // Simpan token dan data user ke SessionManager jika tidak null
                if (token != null && user != null) {
                    sessionManager.saveSession(token, user.name, user.email, user.role.name)
                }

                Toast.makeText(this, "Login Berhasil! Selamat datang ${user?.name}", Toast.LENGTH_SHORT).show()

                // Pindah ke MainActivity setelah login sukses
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Menutup LoginActivity dari backstack
            } else {
                // Menampilkan pesan kegagalan dari server (misal: "Email salah" atau "Password salah")
                Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
            }
        }

        // 3. Mengamati Kesalahan Jaringan / Server Error
        viewModel.errorMessage.observe(this) { errorMsg ->
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
        }
    }
}
