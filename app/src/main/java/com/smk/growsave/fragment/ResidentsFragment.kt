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
import com.smk.growsave.adapter.ResidentAdapter
import com.smk.growsave.databinding.FragmentResidentsBinding
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.viewmodel.AuthViewModel

class ResidentsFragment : Fragment() {

    private var _binding: FragmentResidentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: ResidentAdapter

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

        // Load data sekali saat fragment dibuat (onViewCreated) untuk menghindari request berulang
        loadData()
    }

    private fun loadData() {
        val token = sessionManager.getToken()
        if (token != null) {
            authViewModel.fetchRoomResidents(token)
        } else {
            Toast.makeText(requireContext(), "Sesi berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = ResidentAdapter()
        binding.rvResidents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvResidents.adapter = adapter
    }

    private fun setupObservers() {
        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        authViewModel.roomResidents.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            updateSummary(list.size)
            if (list.isEmpty()) {
                binding.tvNoData.visibility = View.VISIBLE
                binding.rvResidents.visibility = View.GONE
            } else {
                binding.tvNoData.visibility = View.GONE
                binding.rvResidents.visibility = View.VISIBLE
            }
        }

        authViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
                // Update summary based on filtered result size
                updateSummary(adapter.getFilteredSize())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateSummary(count: Int) {
        binding.tvSummaryText.text = "Total penghuni aktif di room ini: $count warga."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
