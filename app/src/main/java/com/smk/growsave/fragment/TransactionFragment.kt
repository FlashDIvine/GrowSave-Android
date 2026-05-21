package com.smk.growsave.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.smk.growsave.adapter.TransactionAdapter
import com.smk.growsave.databinding.FragmentTransactionBinding
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.viewmodel.TransactionViewModel

/**
 * TransactionFragment menampilkan daftar transaksi keuangan.
 * Menghubungkan ke API melalui ViewModel, Repository, dan menampilkannya dalam RecyclerView.
 */
class TransactionFragment : Fragment() {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    // Menggunakan ViewModelProvider agar tidak bergantung pada library fragment-ktx
    private lateinit var viewModel: TransactionViewModel

    private lateinit var adapter: TransactionAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        sessionManager = SessionManager(requireContext())
        setupRecyclerView()
        setupObservers()

        // Ambil token JWT dari SharedPreferences
        val token = sessionManager.getToken()
        if (token != null) {
            // Ambil daftar transaksi dari API
            viewModel.fetchTransactions(token)
        } else {
            Toast.makeText(requireContext(), "Sesi berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter()
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = adapter
    }

    private fun setupObservers() {
        // 1. Mengamati status loading untuk menampilkan / menyembunyikan ProgressBar
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // 2. Mengamati data transaksi dari server untuk ditampilkan ke RecyclerView
        viewModel.transactions.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        // 3. Mengamati pesan error jika terjadi kegagalan request API
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Mencegah kebocoran memori (memory leak)
    }
}
