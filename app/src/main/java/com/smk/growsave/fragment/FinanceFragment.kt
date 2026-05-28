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
import com.smk.growsave.adapter.BillAdapter
import com.smk.growsave.databinding.FragmentFinanceBinding
import com.smk.growsave.model.Bill
import com.smk.growsave.model.BillStats
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.viewmodel.BillViewModel
import com.smk.growsave.viewmodel.PaymentViewModel
import java.text.NumberFormat
import java.util.Locale

/**
 * FinanceFragment — Dashboard Statistik Tagihan
 *
 * Fragment ini fokus menampilkan ringkasan dan daftar tagihan (bills).
 * TIDAK menampilkan data transaksi kas (itu tanggung jawab TransactionFragment).
 *
 * Konsep pemisahan:
 * - Dashboard Finance = statistik tagihan (total, aktif, lunas, nominal belum bayar)
 * - Halaman Transaction = arus kas / pemasukan / pengeluaran
 *
 * State management:
 * - Loading: saat fetch data dari API
 * - Content: menampilkan stats + list tagihan
 * - Empty: saat tidak ada tagihan
 * - Error: saat API gagal, dengan tombol retry
 */
class FinanceFragment : Fragment() {

    // ==================== VIEW BINDING ====================

    private var _binding: FragmentFinanceBinding? = null
    private val binding get() = _binding!!

    // ==================== VIEW MODELS ====================

    private lateinit var billViewModel: BillViewModel
    private lateinit var paymentViewModel: PaymentViewModel

    // ==================== ADAPTERS & SESSION ====================

    private lateinit var sessionManager: SessionManager
    private lateinit var billAdapter: BillAdapter

    // ==================== STATE ====================

    private var selectedBill: Bill? = null

    /**
     * Currency formatter untuk format Rupiah.
     * Contoh: 4250000 → Rp4.250.000
     */
    private val currencyFormatter: NumberFormat by lazy {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    // ==================== PAYMENT RESULT HANDLER ====================

    private val paymentLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Refresh data secara realtime ketika kembali dari PaymentActivity
        loadBillData()

        val data = result.data
        val transactionStatus = data?.getStringExtra("TRANSACTION_STATUS")

        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val message = transactionStatus?.let { "Status Pembayaran: $it" }
                ?: "Pembayaran berhasil diproses"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        } else if (result.resultCode == android.app.Activity.RESULT_CANCELED) {
            if (transactionStatus != null) {
                Toast.makeText(requireContext(), "Pembayaran dibatalkan/gagal: $transactionStatus", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ==================== LIFECYCLE ====================

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        billViewModel = ViewModelProvider(this)[BillViewModel::class.java]
        paymentViewModel = ViewModelProvider(this)[PaymentViewModel::class.java]

        setupUI()
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        loadBillData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ==================== SETUP ====================

    /**
     * Mengatur visibilitas elemen berdasarkan role user.
     * Tombol "Buat Tagihan Baru" hanya muncul untuk admin.
     */
    private fun setupUI() {
        val role = sessionManager.getUserRole()
        val isAdmin = role?.lowercase() == "admin"
        binding.btnCreateBill.visibility = if (isAdmin) View.VISIBLE else View.GONE
    }

    /**
     * Mengatur RecyclerView dengan BillAdapter.
     * Adapter dikonfigurasi berdasarkan role user (admin vs warga).
     */
    private fun setupRecyclerView() {
        val role = sessionManager.getUserRole()
        val isAdmin = role?.lowercase() == "admin"

        billAdapter = BillAdapter(
            bills = emptyList(),
            isAdmin = isAdmin,
            onCompleteClick = { bill -> showCompleteBillConfirmationDialog(bill) },
            onDeleteClick = { bill -> showDeleteBillConfirmationDialog(bill) },
            onBillClick = { bill -> handleBillPayment(bill) }
        )

        binding.rvFinance.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFinance.adapter = billAdapter
    }

    /**
     * Mengatur semua LiveData observers.
     * Memisahkan observasi untuk: loading, bills data, stats, errors, payment, dan CRUD results.
     */
    private fun setupObservers() {
        // Loading state
        billViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showLoadingState()
            }
        }

        // Bills data — update list dan tentukan state (content vs empty)
        billViewModel.bills.observe(viewLifecycleOwner) { bills ->
            if (bills.isEmpty()) {
                showEmptyState()
            } else {
                showContentState()
                billAdapter.submitList(bills)
            }
        }

        // Bill statistics — update dashboard cards
        billViewModel.billStats.observe(viewLifecycleOwner) { stats ->
            updateDashboardStats(stats)
        }

        // Error handling — tampilkan error state
        billViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                showErrorState(errorMsg)
            }
        }

        // Complete bill success
        billViewModel.completeBillSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Iuran berhasil ditutup secara manual!", Toast.LENGTH_SHORT).show()
                billViewModel.resetCompleteBillSuccess()
                loadBillData()
            }
        }

        // Delete bill success
        billViewModel.deleteBillSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Tagihan berhasil dihapus", Toast.LENGTH_SHORT).show()
                billViewModel.resetDeleteBillSuccess()
                loadBillData()
            }
        }

        // Payment snap token — launch PaymentActivity
        paymentViewModel.snapToken.observe(viewLifecycleOwner) { snapToken ->
            if (snapToken != null) {
                val intent = Intent(requireContext(), com.smk.growsave.PaymentActivity::class.java).apply {
                    putExtra(com.smk.growsave.PaymentActivity.EXTRA_SNAP_TOKEN, snapToken)
                    putExtra(com.smk.growsave.PaymentActivity.EXTRA_BILL_TITLE, selectedBill?.title ?: "Tagihan")
                }
                paymentLauncher.launch(intent)
                paymentViewModel.clearSnapToken()
            }
        }

        // Payment loading state
        paymentViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Payment error
        paymentViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg != null) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
                paymentViewModel.clearErrorMessage()
            }
        }
    }

    /**
     * Mengatur click listeners untuk tombol-tombol.
     */
    private fun setupListeners() {
        // Buat Tagihan Baru (admin only)
        binding.btnCreateBill.setOnClickListener {
            val intent = Intent(requireContext(), com.smk.growsave.CreateBillActivity::class.java)
            startActivity(intent)
        }

        // Retry button pada error state
        binding.btnRetry.setOnClickListener {
            loadBillData()
        }
    }

    // ==================== DATA LOADING ====================

    /**
     * Memuat data tagihan dari API.
     * Menampilkan loading state selama proses fetching.
     */
    private fun loadBillData() {
        val token = sessionManager.getToken()
        if (token != null) {
            billViewModel.fetchBills(token)
        } else {
            Toast.makeText(requireContext(), "Sesi berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
        }
    }

    // ==================== UI STATE MANAGEMENT ====================

    /**
     * Menampilkan loading state.
     * Sembunyikan content, empty, dan error. Tampilkan spinner.
     */
    private fun showLoadingState() {
        binding.layoutLoading.visibility = View.VISIBLE
        binding.rvFinance.visibility = View.GONE
        binding.layoutEmpty.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
    }

    /**
     * Menampilkan content state (ada data tagihan).
     * Tampilkan RecyclerView, sembunyikan states lain.
     */
    private fun showContentState() {
        binding.layoutLoading.visibility = View.GONE
        binding.rvFinance.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
    }

    /**
     * Menampilkan empty state (tidak ada tagihan).
     * Tampilkan ilustrasi dan pesan kosong.
     */
    private fun showEmptyState() {
        binding.layoutLoading.visibility = View.GONE
        binding.rvFinance.visibility = View.GONE
        binding.layoutEmpty.visibility = View.VISIBLE
        binding.layoutError.visibility = View.GONE
    }

    /**
     * Menampilkan error state dengan pesan error.
     * Tampilkan pesan error dan tombol retry.
     */
    private fun showErrorState(message: String) {
        binding.layoutLoading.visibility = View.GONE
        binding.rvFinance.visibility = View.GONE
        binding.layoutEmpty.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
        binding.tvErrorMessage.text = message
    }

    // ==================== DASHBOARD STATS ====================

    /**
     * Memperbarui semua card statistik pada dashboard.
     *
     * @param stats BillStats yang dihitung otomatis dari list bills
     */
    private fun updateDashboardStats(stats: BillStats) {
        // Hero card: Total nominal tagihan belum bayar
        binding.tvTotalUnpaidBills.text = formatCurrency(stats.totalUnpaidAmount)
        binding.tvUnpaidSubtitle.text = "Dari ${stats.activeBills} tagihan aktif"

        // Stats cards
        binding.tvTotalBillsCount.text = stats.totalBills.toString()
        binding.tvActiveBillsCount.text = stats.activeBills.toString()
        binding.tvPaidBillsCount.text = "${stats.paidBills} tagihan"
    }

    /**
     * Format nominal ke format Rupiah.
     * Contoh: 4250000 → "Rp4.250.000"
     */
    private fun formatCurrency(amount: Long): String {
        return currencyFormatter.format(amount)
    }

    // ==================== BILL ACTIONS ====================

    /**
     * Menangani klik pada tagihan untuk pembayaran.
     * Jika sudah lunas, tampilkan toast. Jika belum, buat payment via Midtrans.
     */
    private fun handleBillPayment(bill: Bill) {
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

    /**
     * Dialog konfirmasi untuk menutup iuran secara manual (admin only).
     */
    private fun showCompleteBillConfirmationDialog(bill: Bill) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Selesaikan Iuran")
            .setMessage("Apakah Anda yakin ingin menyelesaikan/menutup iuran '${bill.title}' secara manual? Warga tidak akan dapat membayar iuran ini lagi.")
            .setPositiveButton("Ya") { dialog, _ ->
                val token = sessionManager.getToken()
                if (token != null) {
                    billViewModel.completeBill(token, bill.id)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Dialog konfirmasi untuk menghapus tagihan (admin only).
     * Mencegah multiple click saat sedang loading.
     */
    private fun showDeleteBillConfirmationDialog(bill: Bill) {
        if (billViewModel.isLoading.value == true) return

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Hapus Tagihan")
            .setMessage("Yakin ingin menghapus tagihan '${bill.title}'?")
            .setPositiveButton("Hapus") { dialog, _ ->
                val token = sessionManager.getToken()
                if (token != null) {
                    billViewModel.deleteBill(token, bill.id)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
