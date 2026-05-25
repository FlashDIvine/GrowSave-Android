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
import com.smk.growsave.R
import com.smk.growsave.adapter.BillAdapter
import com.smk.growsave.adapter.TransactionAdapter
import com.smk.growsave.databinding.FragmentFinanceBinding
import com.smk.growsave.model.Bill
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.viewmodel.BillViewModel
import com.smk.growsave.viewmodel.TransactionViewModel
import com.smk.growsave.viewmodel.PaymentViewModel

class FinanceFragment : Fragment() {

    private var _binding: FragmentFinanceBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var billViewModel: BillViewModel
    private lateinit var paymentViewModel: PaymentViewModel
    private lateinit var sessionManager: SessionManager

    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var billAdapter: BillAdapter

    private var selectedBill: Bill? = null
    private var isShowingTransactions = true

    private val paymentLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Refresh data secara realtime ketika kembali dari PaymentActivity
        loadData()
        
        val data = result.data
        val transactionStatus = data?.getStringExtra("TRANSACTION_STATUS")
        
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            if (transactionStatus != null) {
                Toast.makeText(requireContext(), "Status Pembayaran: $transactionStatus", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Pembayaran berhasil diproses", Toast.LENGTH_SHORT).show()
            }
        } else if (result.resultCode == android.app.Activity.RESULT_CANCELED) {
            if (transactionStatus != null) {
                Toast.makeText(requireContext(), "Pembayaran dibatalkan/gagal: $transactionStatus", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        billViewModel = ViewModelProvider(this)[BillViewModel::class.java]
        paymentViewModel = ViewModelProvider(this)[PaymentViewModel::class.java]

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
        } else {
            Toast.makeText(requireContext(), "Sesi berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
        
        val role = sessionManager.getUserRole()
        val isAdmin = role?.lowercase() == "admin"

        billAdapter = BillAdapter(
            bills = emptyList(),
            isAdmin = isAdmin,
            onCompleteClick = { bill ->
                showCompleteBillConfirmationDialog(bill)
            },
            onBillClick = { bill ->
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
        )

        binding.rvFinance.layoutManager = LinearLayoutManager(requireContext())
        updateAdapter()
    }

    private fun setupObservers() {
        transactionViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isShowingTransactions) {
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        billViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (!isShowingTransactions) {
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        transactionViewModel.transactions.observe(viewLifecycleOwner) { list ->
            transactionAdapter.submitList(list)
            if (isShowingTransactions) {
                updateUIState(list.isEmpty())
            }
        }

        billViewModel.bills.observe(viewLifecycleOwner) { list ->
            billAdapter.submitList(list)
            if (!isShowingTransactions) {
                updateUIState(list.isEmpty())
            }
        }

        transactionViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (isShowingTransactions && !errorMsg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
            }
        }

        billViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
            }
        }

        billViewModel.completeBillSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Iuran berhasil ditutup secara manual!", Toast.LENGTH_SHORT).show()
                billViewModel.resetCompleteBillSuccess()
                loadData()
            }
        }

        paymentViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (!isShowingTransactions) {
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

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

        paymentViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg != null) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
                paymentViewModel.clearErrorMessage()
            }
        }
    }

    private fun setupListeners() {
        val role = sessionManager.getUserRole()
        val isAdmin = role?.lowercase() == "admin"
        binding.btnCreateBill.visibility = if (isAdmin) View.VISIBLE else View.GONE

        binding.btnTransactions.setOnClickListener {
            if (!isShowingTransactions) {
                isShowingTransactions = true
                updateAdapter()
            }
        }
        binding.btnBills.setOnClickListener {
            if (isShowingTransactions) {
                isShowingTransactions = false
                updateAdapter()
            }
        }
        binding.btnCreateBill.setOnClickListener {
            val intent = Intent(requireContext(), com.smk.growsave.CreateBillActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateAdapter() {
        updateTabVisuals()
        if (isShowingTransactions) {
            binding.rvFinance.adapter = transactionAdapter
            val list = transactionViewModel.transactions.value ?: emptyList()
            updateUIState(list.isEmpty())
            binding.progressBar.visibility = if (transactionViewModel.isLoading.value == true) View.VISIBLE else View.GONE
        } else {
            binding.rvFinance.adapter = billAdapter
            val list = billViewModel.bills.value ?: emptyList()
            updateUIState(list.isEmpty())
            binding.progressBar.visibility = if (billViewModel.isLoading.value == true) View.VISIBLE else View.GONE
        }
    }

    private fun updateTabVisuals() {
        if (isShowingTransactions) {
            binding.btnTransactions.setBackgroundResource(R.drawable.bg_filter_active)
            binding.btnTransactions.setTextColor(android.graphics.Color.WHITE)
            binding.btnTransactions.setTypeface(null, android.graphics.Typeface.BOLD)

            binding.btnBills.setBackgroundResource(R.drawable.bg_filter)
            binding.btnBills.setTextColor(android.graphics.Color.parseColor("#1E40AF"))
            binding.btnBills.setTypeface(null, android.graphics.Typeface.NORMAL)
        } else {
            binding.btnTransactions.setBackgroundResource(R.drawable.bg_filter)
            binding.btnTransactions.setTextColor(android.graphics.Color.parseColor("#1E40AF"))
            binding.btnTransactions.setTypeface(null, android.graphics.Typeface.NORMAL)

            binding.btnBills.setBackgroundResource(R.drawable.bg_filter_active)
            binding.btnBills.setTextColor(android.graphics.Color.WHITE)
            binding.btnBills.setTypeface(null, android.graphics.Typeface.BOLD)
        }
    }

    private fun updateUIState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.tvNoData.visibility = View.VISIBLE
            binding.rvFinance.visibility = View.GONE
        } else {
            binding.tvNoData.visibility = View.GONE
            binding.rvFinance.visibility = View.VISIBLE
        }
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
