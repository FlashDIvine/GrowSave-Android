package com.smk.growsave.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.smk.growsave.MainActivity
import com.smk.growsave.R
import com.smk.growsave.adapter.ResidentAdapter
import com.smk.growsave.adapter.RoomRequestAdapter
import com.smk.growsave.databinding.FragmentResidentsBinding
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.viewmodel.AuthViewModel

class ResidentsFragment : Fragment() {

    private var _binding: FragmentResidentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: ResidentAdapter
    private lateinit var requestAdapter: RoomRequestAdapter

    private var isShowingRequests = false

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
        setupSearch()
        setupListeners()

        // Check navigation flag from MainActivity
        val showApproval = (activity as? MainActivity)?.selectApprovalTab ?: false
        (activity as? MainActivity)?.selectApprovalTab = false

        switchTab(showApproval)
    }

    private fun loadData() {
        val token = sessionManager.getToken()
        if (token != null) {
            if (isShowingRequests) {
                authViewModel.fetchRoomRequests(token)
            } else {
                authViewModel.fetchRoomResidents(token)
            }
        } else {
            context?.let {
                Toast.makeText(it, "Sesi berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ResidentAdapter()
        binding.rvResidents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvResidents.adapter = adapter

        requestAdapter = RoomRequestAdapter(
            onApproveClicked = { request ->
                val token = sessionManager.getToken()
                if (token != null) {
                    authViewModel.approveRoom(token, request.id)
                }
            },
            onRejectClicked = { request ->
                val token = sessionManager.getToken()
                if (token != null) {
                    authViewModel.rejectRoom(token, request.id)
                }
            }
        )
        binding.rvRequests.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRequests.adapter = requestAdapter
    }

    private fun setupObservers() {
        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        authViewModel.roomResidents.observe(viewLifecycleOwner) { list ->
            if (!isShowingRequests) {
                adapter.submitList(list)
                updateSummary(list.size)
                if (list.isEmpty()) {
                    binding.tvNoData.text = "Tidak ada data penghuni."
                    binding.tvNoData.visibility = View.VISIBLE
                    binding.rvResidents.visibility = View.GONE
                } else {
                    binding.tvNoData.visibility = View.GONE
                    binding.rvResidents.visibility = View.VISIBLE
                }
            }
        }

        authViewModel.roomRequests.observe(viewLifecycleOwner) { list ->
            if (isShowingRequests) {
                requestAdapter.submitList(list)
                if (list.isEmpty()) {
                    binding.tvNoData.text = "Tidak ada permohonan persetujuan."
                    binding.tvNoData.visibility = View.VISIBLE
                    binding.rvRequests.visibility = View.GONE
                } else {
                    binding.tvNoData.visibility = View.GONE
                    binding.rvRequests.visibility = View.VISIBLE
                }
            }
        }

        authViewModel.roomActionSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                context?.let {
                    Toast.makeText(it, "Berhasil memproses permintaan", Toast.LENGTH_SHORT).show()
                }
                authViewModel.resetRoomActionSuccess()
                loadData()
            }
        }

        authViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                context?.let {
                    Toast.makeText(it, errorMsg, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
                updateSummary(adapter.getFilteredSize())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupListeners() {
        binding.btnTabResidents.setOnClickListener {
            if (isShowingRequests) {
                switchTab(false)
            }
        }

        binding.btnTabRequests.setOnClickListener {
            if (!isShowingRequests) {
                switchTab(true)
            }
        }
    }

    private fun switchTab(showRequests: Boolean) {
        isShowingRequests = showRequests
        if (showRequests) {
            binding.layoutResidentsTab.visibility = View.GONE
            binding.layoutRequestsTab.visibility = View.VISIBLE

            binding.btnTabResidents.setBackgroundResource(android.R.color.transparent)
            binding.btnTabResidents.setTextColor(android.graphics.Color.parseColor("#6B7280"))

            binding.btnTabRequests.setBackgroundResource(R.drawable.bg_segment_item_active)
            binding.btnTabRequests.setTextColor(android.graphics.Color.parseColor("#0D7B43"))
        } else {
            binding.layoutResidentsTab.visibility = View.VISIBLE
            binding.layoutRequestsTab.visibility = View.GONE

            binding.btnTabResidents.setBackgroundResource(R.drawable.bg_segment_item_active)
            binding.btnTabResidents.setTextColor(android.graphics.Color.parseColor("#0D7B43"))

            binding.btnTabRequests.setBackgroundResource(android.R.color.transparent)
            binding.btnTabRequests.setTextColor(android.graphics.Color.parseColor("#6B7280"))
        }

        binding.tvNoData.visibility = View.GONE
        loadData()
    }

    private fun updateSummary(count: Int) {
        binding.tvSummaryText.text = "Total penghuni aktif di room ini: $count warga."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

