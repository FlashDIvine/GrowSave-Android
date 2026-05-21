package com.smk.growsave

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.smk.growsave.databinding.ActivityMainBinding
import com.smk.growsave.fragment.AnnouncementFragment
import com.smk.growsave.fragment.HomeFragment
import com.smk.growsave.fragment.ProfileFragment
import com.smk.growsave.fragment.TransactionFragment
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

        // Set Fragment default saat pertama kali dibuka (HomeFragment)
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Setup Listener untuk Bottom Navigation View
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.menu_home -> HomeFragment()
                R.id.menu_transaction -> TransactionFragment()
                R.id.menu_announcement -> AnnouncementFragment()
                R.id.menu_profile -> ProfileFragment()
                else -> HomeFragment()
            }
            loadFragment(fragment)
            true
        }
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