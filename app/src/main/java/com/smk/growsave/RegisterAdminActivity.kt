package com.smk.growsave

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.smk.growsave.databinding.ActivityAdminRegisterBinding
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.viewmodel.AuthViewModel

/**
 * RegisterAdminActivity menangani form pendaftaran pengguna dengan hak akses Admin.
 * Mengirimkan admin_code ke backend untuk validasi.
 * Setelah registrasi berhasil, sesi disimpan dan pengguna diarahkan ke MainActivity.
 */
class RegisterAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminRegisterBinding
    private lateinit var sessionManager: SessionManager
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup ViewBinding
        binding = ActivityAdminRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Tombol Daftar Admin ditekan
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            val adminCode = binding.etAdminCode.text.toString().trim()

            // Validasi input sederhana
            if (name.isEmpty()) {
                binding.etName.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.etEmail.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.etPassword.error = "Kata sandi tidak boleh kosong"
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                binding.etConfirmPassword.error = "Konfirmasi kata sandi tidak boleh kosong"
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                binding.etConfirmPassword.error = "Kata sandi tidak cocok"
                return@setOnClickListener
            }
            if (adminCode.isEmpty()) {
                binding.etAdminCode.error = "Kode admin tidak boleh kosong"
                return@setOnClickListener
            }

            // Kirim admin_code ke backend untuk validasi
            viewModel.register(
                name = name,
                email = email,
                password = password,
                adminCode = adminCode
            )
        }

        // Kembali ke LoginActivity via text link
        binding.tvLogin.setOnClickListener {
            finish()
        }

        // Kembali ke LoginActivity via tombol Close
        binding.btnClose.setOnClickListener {
            finish()
        }

        // Switch Tab ke pendaftaran Warga (RegisterActivity)
        binding.tabWarga.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        setupObservers()
    }

    private fun setupObservers() {
        // 1. Observe Loading State
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnRegister.isEnabled = false
            } else {
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true
            }
        }

        // 2. Observe Hasil Register
        viewModel.registerResult.observe(this) { response ->
            if (response.success) {
                val user = response.data?.user
                val token = response.data?.token

                // Simpan token dan data user ke SessionManager
                if (token != null && user != null) {
                    sessionManager.saveSession(token, user.name, user.email, user.role.name)
                }

                Toast.makeText(this, "Registrasi Admin berhasil! Selamat datang ${user?.name}", Toast.LENGTH_SHORT).show()

                // Langsung pindah ke MainActivity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
            }
        }

        // 3. Observe Error Koneksi
        viewModel.errorMessage.observe(this) { errorMsg ->
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
        }
    }
}
