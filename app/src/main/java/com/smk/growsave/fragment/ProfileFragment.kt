package com.smk.growsave.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.smk.growsave.LoginActivity
import com.smk.growsave.databinding.FragmentProfileBinding
import com.smk.growsave.utils.SessionManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi SessionManager
        sessionManager = SessionManager(requireContext())

        // Tampilkan nama user dari session
        val name = sessionManager.getUserName() ?: "User"
        binding.tvWelcome.text = "Selamat Datang, $name!"

        // Setup tombol logout
        binding.btnLogout.setOnClickListener {
            // Hapus sesi data lokal
            sessionManager.clearSession()
            Toast.makeText(requireContext(), "Berhasil logout", Toast.LENGTH_SHORT).show()

            // Redirect ke LoginActivity dan bersihkan backstack
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Mencegah kebocoran memori (memory leaks)
    }
}
