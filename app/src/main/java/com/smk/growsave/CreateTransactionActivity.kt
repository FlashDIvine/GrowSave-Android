package com.smk.growsave

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.smk.growsave.databinding.ActivityCreateTransactionBinding
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.viewmodel.TransactionViewModel

class CreateTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateTransactionBinding
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTransactionTitle.text.toString().trim()
            val amountStr = binding.etTransactionAmount.text.toString().trim()

            if (title.isEmpty()) {
                binding.etTransactionTitle.error = "Nama transaksi tidak boleh kosong"
                return@setOnClickListener
            }
            if (amountStr.isEmpty()) {
                binding.etTransactionAmount.error = "Jumlah uang tidak boleh kosong"
                return@setOnClickListener
            }

            val amount = amountStr.toLongOrNull()
            if (amount == null || amount <= 0) {
                binding.etTransactionAmount.error = "Jumlah uang harus lebih besar dari 0"
                return@setOnClickListener
            }

            val type = if (binding.rbIncome.isChecked) "income" else "expense"

            val token = sessionManager.getToken()
            if (token != null) {
                transactionViewModel.createTransaction(token, title, type, amount)
            } else {
                Toast.makeText(this, "Sesi berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        transactionViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !isLoading
        }

        transactionViewModel.createTransactionSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Transaksi berhasil disimpan!", Toast.LENGTH_SHORT).show()
                transactionViewModel.resetCreateTransactionSuccess()
                finish()
            }
        }

        transactionViewModel.errorMessage.observe(this) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }
}
