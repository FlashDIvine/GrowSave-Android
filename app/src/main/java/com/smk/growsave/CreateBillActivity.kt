package com.smk.growsave

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.smk.growsave.databinding.ActivityCreateBillBinding
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.viewmodel.BillViewModel
import java.util.Calendar

class CreateBillActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateBillBinding
    private lateinit var billViewModel: BillViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBillBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        billViewModel = ViewModelProvider(this)[BillViewModel::class.java]

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.etBillDueDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etBillTitle.text.toString().trim()
            val description = binding.etBillDescription.text.toString().trim().let { if (it.isEmpty()) null else it }
            val targetAmountStr = binding.etBillTargetAmount.text.toString().trim()
            val requiredAmountStr = binding.etBillAmount.text.toString().trim()
            val dueDate = binding.etBillDueDate.text.toString().trim()

            if (title.isEmpty()) {
                binding.etBillTitle.error = "Nama tagihan tidak boleh kosong"
                return@setOnClickListener
            }
            if (targetAmountStr.isEmpty()) {
                binding.etBillTargetAmount.error = "Target dana tidak boleh kosong"
                return@setOnClickListener
            }
            if (requiredAmountStr.isEmpty()) {
                binding.etBillAmount.error = "Iuran wajib per warga tidak boleh kosong"
                return@setOnClickListener
            }
            if (dueDate.isEmpty()) {
                binding.etBillDueDate.error = "Tanggal jatuh tempo tidak boleh kosong"
                return@setOnClickListener
            }

            val targetAmount = targetAmountStr.toLongOrNull()
            if (targetAmount == null || targetAmount <= 0) {
                binding.etBillTargetAmount.error = "Target dana harus lebih besar dari 0"
                return@setOnClickListener
            }

            val requiredAmount = requiredAmountStr.toLongOrNull()
            if (requiredAmount == null || requiredAmount <= 0) {
                binding.etBillAmount.error = "Iuran wajib per warga harus lebih besar dari 0"
                return@setOnClickListener
            }

            val token = sessionManager.getToken()
            if (token != null) {
                billViewModel.createCrowdfundBill(token, title, description, targetAmount, requiredAmount, dueDate)
            } else {
                Toast.makeText(this, "Sesi berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        billViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !isLoading
        }

        billViewModel.createBillSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Tagihan berhasil dibuat!", Toast.LENGTH_SHORT).show()
                billViewModel.resetCreateBillSuccess()
                finish()
            }
        }

        billViewModel.errorMessage.observe(this) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val formattedDay = String.format("%02d", selectedDay)
                binding.etBillDueDate.setText("$selectedYear-$formattedMonth-$formattedDay")
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}
