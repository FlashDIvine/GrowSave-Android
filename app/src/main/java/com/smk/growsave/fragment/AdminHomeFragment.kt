package com.smk.growsave.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.smk.growsave.LoginActivity
import com.smk.growsave.MainActivity
import com.smk.growsave.R
import com.smk.growsave.adapter.TransactionAdapter
import com.smk.growsave.databinding.FragmentAdminHomeBinding
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.viewmodel.BillViewModel
import com.smk.growsave.viewmodel.TransactionViewModel
import androidx.lifecycle.lifecycleScope
import com.smk.growsave.network.RetrofitClient
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class AdminHomeFragment : Fragment() {

    private var _binding: FragmentAdminHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var billViewModel: BillViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        // Initialize ViewModels
        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        billViewModel = ViewModelProvider(this)[BillViewModel::class.java]

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
            transactionViewModel.fetchTransactions(token)
            billViewModel.fetchBills(token)

            // Ambil info detail room secara asinkron
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.getRoom(token)
                    if (response.success && response.data != null) {
                        val data = response.data
                        binding.tvRoomName.text = data.roomName
                        binding.tvTotalMembers.text = "${data.totalMembers ?: 0} Warga"
                        binding.tvRoomStatus.text = data.status.replaceFirstChar { 
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
                        }
                        if (data.status.equals("active", ignoreCase = true)) {
                            binding.tvRoomStatus.setTextColor(android.graphics.Color.parseColor("#0D7B43"))
                            binding.tvRoomStatus.setBackgroundResource(R.drawable.bg_badge_success)
                        } else {
                            binding.tvRoomStatus.setTextColor(android.graphics.Color.parseColor("#C81E1E"))
                            binding.tvRoomStatus.setBackgroundResource(R.drawable.bg_badge_danger)
                        }
                    }
                } catch (e: Exception) {
                    binding.tvRoomName.text = "Error Room"
                }
            }
        } else {
            Toast.makeText(requireContext(), "Sesi berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
        binding.rvLatestTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLatestTransactions.adapter = transactionAdapter
    }

    private fun setupObservers() {
        // Observe transactions
        transactionViewModel.transactions.observe(viewLifecycleOwner) { list ->
            val income = list.filter { it.type.equals("income", ignoreCase = true) }.sumOf { it.amount }
            val expense = list.filter { it.type.equals("expense", ignoreCase = true) }.sumOf { it.amount }
            val totalKas = income - expense

            binding.tvTotalKas.text = formatRupiah(totalKas)
            binding.tvIncome.text = formatRupiah(income)
            binding.tvExpense.text = formatRupiah(expense)

            // Show latest 5 transactions
            transactionAdapter.submitList(list.take(5))
        }

        transactionViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
            }
        }

        // Observe bills for progress iuran
        billViewModel.bills.observe(viewLifecycleOwner) { list ->
            val total = list.size
            val paid = list.count { it.status.equals("paid", ignoreCase = true) }
            val percentage = if (total > 0) (paid * 100) / total else 0

            binding.tvProgressPercentage.text = "$percentage%"
            binding.tvProgressDetail.text = "$paid / $total Lunas"
            binding.progressBarIuran.progress = percentage
        }
    }

    private fun setupListeners() {
        // Quick Actions clicks
        binding.btnQuickCreateBill.setOnClickListener {
            // Start CreateBillActivity
            try {
                val intent = Intent(requireContext(), Class.forName("com.smk.growsave.CreateBillActivity"))
                startActivity(intent)
            } catch (e: ClassNotFoundException) {
                Toast.makeText(requireContext(), "Fitur Buat Tagihan belum siap", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnQuickCreateTransaction.setOnClickListener {
            // Start CreateTransactionActivity
            try {
                val intent = Intent(requireContext(), Class.forName("com.smk.growsave.CreateTransactionActivity"))
                startActivity(intent)
            } catch (e: ClassNotFoundException) {
                Toast.makeText(requireContext(), "Fitur Tambah Transaksi belum siap", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnQuickCreateAnnouncement.setOnClickListener {
            // Start CreateAnnouncementActivity
            try {
                val intent = Intent(requireContext(), Class.forName("com.smk.growsave.CreateAnnouncementActivity"))
                startActivity(intent)
            } catch (e: ClassNotFoundException) {
                Toast.makeText(requireContext(), "Fitur Buat Pengumuman belum siap", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnQuickRoomApproval.setOnClickListener {
            // Select Residents tab in Bottom Navigation and open approvals tab
            (activity as? MainActivity)?.selectTab(R.id.menu_admin_residents, showApproval = true)
        }

        binding.btnSeeAll.setOnClickListener {
            // Select Finance tab in Bottom Navigation
            (activity as? MainActivity)?.selectTab(R.id.menu_admin_finance)
        }

        // Profile Avatar click -> Show popup profile and logout
        binding.btnProfile.setOnClickListener {
            showProfileDialog()
        }
    }

    private fun showProfileDialog() {
        val name = sessionManager.getUserName() ?: "Admin"
        val email = sessionManager.getUserEmail() ?: "admin@growsave.com"
        val role = sessionManager.getUserRole() ?: "ADMIN"

        AlertDialog.Builder(requireContext())
            .setTitle("Profil Admin")
            .setMessage("Nama: $name\nEmail: $email\nRole: $role")
            .setPositiveButton("Logout") { _, _ ->
                sessionManager.clearSession()
                Toast.makeText(requireContext(), "Berhasil logout", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("Tutup", null)
            .show()
    }

    private fun formatRupiah(number: Long): String {
        val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
        return format.format(number).replace("Rp", "Rp ").replace(",00", "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
