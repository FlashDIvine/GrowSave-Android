package com.smk.growsave.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.smk.growsave.CreateAnnouncementActivity
import com.smk.growsave.adapter.AnnouncementAdapter
import com.smk.growsave.databinding.FragmentInfoBinding
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.viewmodel.AnnouncementViewModel
import com.smk.growsave.model.Announcement

class InfoFragment : Fragment() {

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var announcementViewModel: AnnouncementViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: AnnouncementAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        announcementViewModel = ViewModelProvider(this)[AnnouncementViewModel::class.java]

        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val token = sessionManager.getToken()
        if (token != null) {
            announcementViewModel.fetchAnnouncements(token)
        } else {
            Toast.makeText(requireContext(), "Sesi berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        val role = sessionManager.getUserRole()
        val isAdmin = role?.lowercase() == "admin"
        adapter = AnnouncementAdapter(
            isAdmin = isAdmin,
            onDeleteClick = { announcement -> showDeleteAnnouncementDialog(announcement) }
        )
        binding.rvAnnouncements.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAnnouncements.adapter = adapter
    }

    private fun setupObservers() {
        announcementViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        announcementViewModel.announcements.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            if (list.isEmpty()) {
                binding.tvNoData.visibility = View.VISIBLE
                binding.rvAnnouncements.visibility = View.GONE
            } else {
                binding.tvNoData.visibility = View.GONE
                binding.rvAnnouncements.visibility = View.VISIBLE
            }
        }

        announcementViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
            }
        }

        announcementViewModel.deleteAnnouncementSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Pengumuman berhasil dihapus", Toast.LENGTH_SHORT).show()
                loadData() // Refresh list otomatis
                announcementViewModel.resetDeleteAnnouncementSuccess()
            }
        }
    }

    private fun showDeleteAnnouncementDialog(announcement: Announcement) {
        // Cegah multiple click jika sedang loading
        if (announcementViewModel.isLoading.value == true) return

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Hapus Pengumuman")
            .setMessage("Yakin ingin menghapus pengumuman ini?")
            .setPositiveButton("Hapus") { _, _ ->
                val token = sessionManager.getToken()
                if (token != null) {
                    announcementViewModel.deleteAnnouncement(token, announcement.id)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun setupListeners() {
        val role = sessionManager.getUserRole()
        val isAdmin = role?.lowercase() == "admin"
        binding.fabAddAnnouncement.visibility = if (isAdmin) View.VISIBLE else View.GONE

        binding.fabAddAnnouncement.setOnClickListener {
            val intent = Intent(requireContext(), CreateAnnouncementActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
