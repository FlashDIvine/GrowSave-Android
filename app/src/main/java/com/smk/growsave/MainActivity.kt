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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi SessionManager
        sessionManager = SessionManager(this)

        // Cek Login: Jika user belum login, langsung redirect ke LoginActivity
        if (!sessionManager.isLoggedIn()) {
            goToLogin()
            return
        }

        // Setup ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            // Set Fragment default saat pertama kali dibuka (UserHomeFragment)
            if (savedInstanceState == null) {
                loadFragment(UserHomeFragment())
            }

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
        }
    }

    /**
     * Memilih tab bottom navigation secara programmatis.
     */
    fun selectTab(itemId: Int) {
        binding.bottomNavigation.selectedItemId = itemId
    }

    /**
     * Memasang/mengganti Fragment di dalam FrameLayout container.
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Tutup MainActivity agar tidak bisa kembali ke halaman ini jika menekan back button
    }
}