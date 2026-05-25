package com.smk.growsave.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.smk.growsave.adapter.RoomRequestAdapter
import com.smk.growsave.databinding.FragmentResidentsBinding
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.viewmodel.AuthViewModel

class ResidentsFragment : Fragment() {

    private var _binding: FragmentResidentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: RoomRequestAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResidentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        setupRecyclerView()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val token = sessionManager.getToken()
        if (token != null) {
            authViewModel.fetchRoomRequests(token)
        } else {
            Toast.makeText(requireContext(), "Sesi berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        val token = sessionManager.getToken()
        adapter = RoomRequestAdapter(
            onApproveClicked = { request ->
                if (token != null) {
                    authViewModel.approveRoom(token, request.id)
                }
            },
            onRejectClicked = { request ->
                if (token != null) {
                    authViewModel.rejectRoom(token, request.id)
                }
            }
        )
        binding.rvRoomRequests.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRoomRequests.adapter = adapter
    }

    private fun setupObservers() {
        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        authViewModel.roomRequests.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            if (list.isEmpty()) {
                binding.tvNoData.visibility = View.VISIBLE
                binding.rvRoomRequests.visibility = View.GONE
            } else {
                binding.tvNoData.visibility = View.GONE
                binding.rvRoomRequests.visibility = View.VISIBLE
            }
        }

        authViewModel.roomActionSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Aksi berhasil diproses!", Toast.LENGTH_SHORT).show()
                authViewModel.resetRoomActionSuccess()
                loadData() // Refresh list
            }
        }

        authViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
