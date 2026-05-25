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
import com.smk.growsave.PaymentActivity
import com.smk.growsave.adapter.BillAdapter
import com.smk.growsave.databinding.FragmentBillsBinding
import com.smk.growsave.model.Bill
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.viewmodel.BillViewModel
import com.smk.growsave.viewmodel.PaymentViewModel

/**
 * BillsFragment menampilkan daftar tagihan yang harus dibayar pengguna.
 * Menghubungkan ke API via BillViewModel dan diikat dengan RecyclerView menggunakan BillAdapter.
 * Terintegrasi dengan PaymentViewModel untuk meminta token transaksi pembayaran (Snap token).
 */
class BillsFragment : Fragment() {

    private var _binding: FragmentBillsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BillViewModel
    private lateinit var paymentViewModel: PaymentViewModel
    private lateinit var adapter: BillAdapter
    private lateinit var sessionManager: SessionManager

    // Menyimpan tagihan yang sedang diklik/diproses
    private var selectedBill: Bill? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBillsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(this)[BillViewModel::class.java]
        paymentViewModel = ViewModelProvider(this)[PaymentViewModel::class.java]

        sessionManager = SessionManager(requireContext())
        setupRecyclerView()
        setupObservers()

        // Ambil JWT token dari session untuk request API
        val token = sessionManager.getToken()
        if (token != null) {
            viewModel.fetchBills(token)
        } else {
            Toast.makeText(requireContext(), "Sesi berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        // Mengirim callback klik item ke adapter
        adapter = BillAdapter(emptyList()) { bill ->
            handleBillClick(bill)
        }
        binding.rvBills.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBills.adapter = adapter
    }

    /**
     * Menangani klik item tagihan. Hanya memproses tagihan yang belum dibayar (unpaid).
     */
    private fun handleBillClick(bill: Bill) {
        if (bill.userPaymentStatus.equals("paid", ignoreCase = true)) {
            Toast.makeText(requireContext(), "Tagihan ini sudah lunas.", Toast.LENGTH_SHORT).show()
        } else {
            val token = sessionManager.getToken()
            if (token != null) {
                selectedBill = bill
                paymentViewModel.createPayment(token, bill.id)
            } else {
                Toast.makeText(requireContext(), "Sesi berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        // 1. Observe loading status untuk daftar tagihan
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // 2. Observe daftar tagihan untuk diperbarui ke Adapter
        viewModel.bills.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        // 3. Observe error message jika load list tagihan gagal
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
        }

        // 4. Observe loading status untuk request pembayaran
        paymentViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // 5. Observe snap token sukses dibuat
        paymentViewModel.snapToken.observe(viewLifecycleOwner) { snapToken ->
            if (snapToken != null) {
                // Buka halaman pembayaran WebView
                val intent = Intent(requireContext(), PaymentActivity::class.java).apply {
                    putExtra(PaymentActivity.EXTRA_SNAP_TOKEN, snapToken)
                    putExtra(PaymentActivity.EXTRA_BILL_TITLE, selectedBill?.title ?: "Tagihan")
                }
                startActivity(intent)
                paymentViewModel.clearSnapToken()
            }
        }

        // 6. Observe error message request pembayaran
        paymentViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg != null) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
                paymentViewModel.clearErrorMessage()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Mencegah memory leak
    }
}
