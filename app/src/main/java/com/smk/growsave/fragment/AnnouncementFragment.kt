package com.smk.growsave.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.smk.growsave.adapter.AnnouncementAdapter
import com.smk.growsave.databinding.FragmentAnnouncementBinding
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.viewmodel.AnnouncementViewModel

/**
 * AnnouncementFragment menampilkan daftar pengumuman warga.
 * Data diambil secara asinkronus menggunakan ViewModel dan ditampilkan di RecyclerView dengan bantuan Glide.
 */
class AnnouncementFragment : Fragment() {

    private var _binding: FragmentAnnouncementBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AnnouncementViewModel
    private lateinit var adapter: AnnouncementAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnnouncementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(this)[AnnouncementViewModel::class.java]

        sessionManager = SessionManager(requireContext())
        setupRecyclerView()
        setupObservers()

        // Memuat pengumuman menggunakan JWT Token dari SharedPreferences
        val token = sessionManager.getToken()
        if (token != null) {
            viewModel.fetchAnnouncements(token)
        } else {
            Toast.makeText(requireContext(), "Sesi berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = AnnouncementAdapter()
        binding.rvAnnouncements.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAnnouncements.adapter = adapter
    }

    private fun setupObservers() {
        // 1. Mengamati status loading untuk menampilkan / menyembunyikan ProgressBar
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // 2. Mengamati data pengumuman untuk diperbarui ke dalam list adapter
        viewModel.announcements.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        // 3. Mengamati status kegagalan/kesalahan koneksi
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Mencegah kebocoran memori (memory leak)
    }
}
