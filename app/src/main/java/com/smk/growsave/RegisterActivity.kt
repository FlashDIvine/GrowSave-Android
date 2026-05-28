package com.smk.growsave

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.smk.growsave.databinding.ActivityRegisterBinding
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.viewmodel.AuthViewModel

/**
 * RegisterActivity menangani form pendaftaran pengguna baru.
 * Setelah registrasi berhasil, token dan sesi langsung disimpan
 * lalu pengguna diarahkan ke MainActivity.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sessionManager: SessionManager
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Tombol Daftar ditekan
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val roomCode = binding.etRoom.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            // Validasi input sederhana
            if (name.isEmpty()) {
                binding.etName.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.etEmail.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }
            if (roomCode.isEmpty()) {
                binding.etRoom.error = "Room code wajib diisi"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.etPassword.error = "Password tidak boleh kosong"
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                binding.etConfirmPassword.error = "Konfirmasi password tidak boleh kosong"
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                binding.etConfirmPassword.error = "Password tidak sama"
                return@setOnClickListener
            }

            // Memicu proses register pada ViewModel
            viewModel.register(
                name = name,
                email = email,
                password = password,
                roomCode = roomCode
            )
        }

        // Link ke halaman Login
        binding.tvLogin.setOnClickListener {
            finish() // Kembali ke LoginActivity
        }

        // Tombol Close
        binding.btnClose.setOnClickListener {
            finish()
        }

        // Switch Tab ke pendaftaran Admin (RegisterAdminActivity)
        binding.tabAdmin.setOnClickListener {
            val intent = Intent(this, RegisterAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        setupObservers()
    }

    private fun setupObservers() {
        // 1. Observe Loading
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnRegister.isEnabled = false
            } else {
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true
            }
        }

        // 2. Observe Hasil Register (sukses / gagal)
        viewModel.registerResult.observe(this) { response ->
            if (response.success) {
                val user = response.data?.user
                val token = response.data?.token

                // Simpan token dan data user ke SessionManager
                if (token != null && user != null) {
                    sessionManager.saveSession(token, user.name, user.email, user.role.name)
                }

                Toast.makeText(this, "Registrasi berhasil! Selamat datang ${user?.name}", Toast.LENGTH_SHORT).show()

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
