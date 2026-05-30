package com.smk.growsave

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.smk.growsave.databinding.ActivityMainBinding
import com.smk.growsave.fragment.AnnouncementFragment
import com.smk.growsave.fragment.HomeFragment
import com.smk.growsave.fragment.UserHomeFragment
import com.smk.growsave.fragment.ProfileFragment
import com.smk.growsave.fragment.TransactionFragment
import com.smk.growsave.fragment.AdminHomeFragment
import com.smk.growsave.fragment.ResidentsFragment
import com.smk.growsave.fragment.FinanceFragment
import com.smk.growsave.fragment.InfoFragment
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.fragment.PendingApprovalFragment
import com.smk.growsave.network.RetrofitClient
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.Toast
import android.view.View

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    var selectApprovalTab: Boolean = false

    private var isNavigating = false
    private var lastNavigationTime: Long = 0L
    private val NAVIGATION_DEBOUNCE_MS = 300L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi SessionManager
        sessionManager = SessionManager(this)

        // Cek Login: Jika user belum login, langsung redirect ke LoginActivity
        if (!sessionManager.isLoggedIn() || sessionManager.getToken().isNullOrEmpty()) {
            goToLogin()
            return
        }

        // Setup ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observe event sesi expired dari interceptor secara global (decoupled)
        SessionManager.isSessionExpired.observe(this) { event ->
            event.getContentIfNotHandled()?.let { expired ->
                if (expired) {
                    sessionManager.clearSession()
                    goToLogin()
                }
            }
        }

        val role = sessionManager.getUserRole()
        val isAdmin = role?.lowercase() == "admin"

        if (isAdmin) {
            // Swap ke Bottom Navigation Menu khusus Admin
            binding.bottomNavigation.menu.clear()
            binding.bottomNavigation.inflateMenu(R.menu.bottom_menu_admin)

            // Set Fragment default saat pertama kali dibuka (AdminHomeFragment)
            if (savedInstanceState == null) {
                loadFragment(AdminHomeFragment())
            }

            // Setup Listener untuk Admin Bottom Navigation View
            binding.bottomNavigation.setOnItemSelectedListener { item ->
                val fragment: Fragment = when (item.itemId) {
                    R.id.menu_admin_home -> AdminHomeFragment()
                    R.id.menu_admin_residents -> ResidentsFragment()
                    R.id.menu_admin_finance -> FinanceFragment()
                    R.id.menu_admin_info -> InfoFragment()
                    else -> AdminHomeFragment()
                }
                loadFragment(fragment)
                true
            }
        } else {
            // Setup Listener untuk User Bottom Navigation View
            binding.bottomNavigation.setOnItemSelectedListener { item ->
                val fragment: Fragment = when (item.itemId) {
                    R.id.menu_home -> UserHomeFragment()
                    R.id.menu_transaction -> FinanceFragment()
                    R.id.menu_announcement -> InfoFragment()
                    R.id.menu_profile -> ProfileFragment()
                    else -> UserHomeFragment()
                }
                loadFragment(fragment)
                true
            }

            // Status Routing & Otorisasi Warga
            val cachedStatus = sessionManager.getUserStatus()
            if (cachedStatus == null) {
                // State unknown: Sembunyikan bottom nav dan tunggu hasil API (tidak default ke pending)
                binding.bottomNavigation.visibility = View.GONE
                checkUserStatus(showLoading = false)
            } else {
                handleStatusRouting(cachedStatus, savedInstanceState == null)
                // Tetap lakukan background sync satu kali saat activity dibuat
                checkUserStatus(showLoading = false)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Proteksi: Jika sesi hilang saat aplikasi di-resume, redirect ke login
        if (!sessionManager.isLoggedIn() || sessionManager.getToken().isNullOrEmpty()) {
            goToLogin()
        }
    }

    /**
     * Memilih tab bottom navigation secara programmatis.
     */
    fun selectTab(itemId: Int, showApproval: Boolean = false) {
        selectApprovalTab = showApproval
        binding.bottomNavigation.selectedItemId = itemId
    }

    /**
     * Memasang/mengganti Fragment di dalam FrameLayout container dengan proteksi dari spam klik dan race condition.
     */
    private fun loadFragment(fragment: Fragment) {
        // 0. Lifecycle guard: abaikan transaksi jika activity sedang dihancurkan atau state tersimpan
        if (isFinishing || isDestroyed) {
            return
        }
        if (supportFragmentManager.isStateSaved) {
            return
        }

        // 1. Navigation lock guard: abaikan jika sedang memproses navigasi
        if (isNavigating) {
            return
        }

        // 2. Duplicate fragment guard: abaikan jika destination fragment sama dengan yang aktif
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment != null && currentFragment::class.java == fragment::class.java) {
            return
        }

        // 3. Debounce guard: batasi agar tidak dipanggil dalam interval pendek (< 300ms)
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastNavigationTime < NAVIGATION_DEBOUNCE_MS) {
            return
        }
        lastNavigationTime = currentTime

        isNavigating = true
        try {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .runOnCommit { isNavigating = false }
                .commit()
        } catch (e: Exception) {
            isNavigating = false
        }
    }

    fun checkUserStatus(showLoading: Boolean = false) {
        val token = sessionManager.getToken() ?: return
        if (showLoading) {
            Toast.makeText(this, "Memeriksa status pendaftaran...", Toast.LENGTH_SHORT).show()
        }
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getRoom(token)
                if (response.success && response.data != null) {
                    val status = response.data.status ?: "pending"
                    sessionManager.saveUserStatus(status)
                    if (showLoading) {
                        Toast.makeText(this@MainActivity, "Status diperbarui: ${status.uppercase()}", Toast.LENGTH_SHORT).show()
                    }
                    handleStatusRouting(status, shouldLoadFragment = true)
                } else {
                    if (showLoading) {
                        Toast.makeText(this@MainActivity, "Gagal memeriksa status: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                    if (sessionManager.getUserStatus() == null) {
                        binding.bottomNavigation.visibility = View.GONE
                        loadFragment(PendingApprovalFragment())
                    }
                }
            } catch (e: retrofit2.HttpException) {
                val code = e.code()
                var handled = false
                if (code == 403) {
                    val errorBody = e.response()?.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = org.json.JSONObject(errorBody)
                            val message = jsonObject.optString("message", "")
                            if (message.contains("Menunggu persetujuan admin", ignoreCase = true) || !jsonObject.optBoolean("success", true)) {
                                sessionManager.saveUserStatus("pending")
                                if (showLoading) {
                                    Toast.makeText(this@MainActivity, "Status Anda masih menunggu persetujuan admin", Toast.LENGTH_SHORT).show()
                                }
                                handleStatusRouting("pending", shouldLoadFragment = true)
                                handled = true
                            }
                        } catch (jsonEx: Exception) {
                            // Abaikan parsing error
                        }
                    }
                }
                if (!handled) {
                    if (showLoading) {
                        Toast.makeText(this@MainActivity, "Gagal memeriksa status: HTTP $code", Toast.LENGTH_SHORT).show()
                    }
                    if (sessionManager.getUserStatus() == null) {
                        binding.bottomNavigation.visibility = View.GONE
                        loadFragment(PendingApprovalFragment())
                    }
                }
            } catch (e: Exception) {
                if (showLoading) {
                    Toast.makeText(this@MainActivity, "Koneksi gagal saat memeriksa status", Toast.LENGTH_SHORT).show()
                }
                if (sessionManager.getUserStatus() == null) {
                    binding.bottomNavigation.visibility = View.GONE
                    loadFragment(PendingApprovalFragment())
                }
            }
        }
    }

    private fun handleStatusRouting(status: String, shouldLoadFragment: Boolean) {
        when (status.lowercase()) {
            "approved", "active" -> {
                binding.bottomNavigation.visibility = View.VISIBLE
                if (shouldLoadFragment) {
                    val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                    if (currentFragment == null || currentFragment is PendingApprovalFragment) {
                        loadFragment(UserHomeFragment())
                    }
                }
            }
            "pending" -> {
                binding.bottomNavigation.visibility = View.GONE
                if (shouldLoadFragment) {
                    loadFragment(PendingApprovalFragment())
                }
            }
            "rejected" -> {
                binding.bottomNavigation.visibility = View.GONE
                showRejectionDialog()
            }
            else -> {
                binding.bottomNavigation.visibility = View.GONE
                if (shouldLoadFragment) {
                    loadFragment(PendingApprovalFragment())
                }
            }
        }
    }

    private fun showRejectionDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Pendaftaran Ditolak")
            .setMessage("Maaf, permohonan bergabung Anda ke ruangan ini telah ditolak oleh Admin. Silakan hubungi Admin untuk informasi lebih lanjut.")
            .setPositiveButton("Logout & Daftar Kembali") { _, _ ->
                sessionManager.clearSession()
                goToLogin()
            }
            .setCancelable(false)
            .show()
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Tutup MainActivity agar tidak bisa kembali ke halaman ini jika menekan back button
    }
}