package com.smk.growsave.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smk.growsave.databinding.ItemBillBinding
import com.smk.growsave.model.Bill
import java.text.NumberFormat
import java.util.Locale

/**
 * BillAdapter menghubungkan daftar tagihan ke dalam RecyclerView.
 */
class BillAdapter(
    private var bills: List<Bill> = emptyList(),
    private val isAdmin: Boolean = false,
    private val onCompleteClick: ((Bill) -> Unit)? = null,
    private val onDeleteClick: ((Bill) -> Unit)? = null,
    private val onBillClick: (Bill) -> Unit
) : RecyclerView.Adapter<BillAdapter.BillViewHolder>() {

    /**
     * Memperbarui data list tagihan di adapter.
     */
    fun submitList(newList: List<Bill>) {
        bills = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val binding = ItemBillBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        holder.bind(bills[position], isAdmin, onCompleteClick, onDeleteClick, onBillClick)
    }

    override fun getItemCount(): Int = bills.size

    class BillViewHolder(
        private val binding: ItemBillBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            bill: Bill,
            isAdmin: Boolean,
            onCompleteClick: ((Bill) -> Unit)?,
            onDeleteClick: ((Bill) -> Unit)?,
            onBillClick: (Bill) -> Unit
        ) {
            binding.tvTitle.text = bill.title
            binding.tvDueDate.text = "Jatuh tempo: ${bill.dueDate}"
            binding.tvAmount.text = formatRupiah(bill.requiredAmount)

            // Deskripsi Iuran
            if (!bill.description.isNullOrEmpty()) {
                binding.tvDescription.text = bill.description
                binding.tvDescription.visibility = View.VISIBLE
            } else {
                binding.tvDescription.visibility = View.GONE
            }

            // Progres Crowdfunding
            val percent = if (bill.targetAmount > 0) {
                ((bill.collectedAmount.toDouble() / bill.targetAmount.toDouble()) * 100).toInt()
            } else {
                0
            }
            binding.tvProgressPercent.text = "$percent%"
            binding.pbTargetProgress.max = 100
            binding.pbTargetProgress.progress = if (percent > 100) 100 else percent
            binding.tvProgressText.text = "${formatRupiah(bill.collectedAmount)} / ${formatRupiah(bill.targetAmount)}"

            // Penentuan Warna Status & Tombol Bayar berdasarkan userPaymentStatus personal
            when (bill.userPaymentStatus.lowercase()) {
                "paid" -> {
                    binding.tvStatus.text = "LUNAS"
                    binding.tvStatus.setBackgroundResource(com.smk.growsave.R.drawable.bg_badge_success)
                    binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#0D7B43"))
                    binding.btnPay.visibility = View.GONE
                }
                "pending" -> {
                    binding.tvStatus.text = "PENDING"
                    binding.tvStatus.setBackgroundResource(com.smk.growsave.R.drawable.bg_badge_warning)
                    binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#92400E"))
                    binding.btnPay.visibility = View.VISIBLE
                    binding.btnPay.text = "Selesaikan"
                }
                else -> { // unpaid
                    binding.tvStatus.setBackgroundResource(com.smk.growsave.R.drawable.bg_badge_danger)
                    binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#C81E1E"))
                    
                    if (bill.isCompleted || bill.status.equals("closed", ignoreCase = true)) {
                        binding.tvStatus.text = "DITUTUP"
                        binding.btnPay.visibility = View.GONE
                    } else {
                        binding.tvStatus.text = "BELUM BAYAR"
                        binding.btnPay.visibility = View.VISIBLE
                        binding.btnPay.text = "Bayar"
                    }
                }
            }

            // Tombol Tutup Iuran (Selesaikan) Khusus Admin jika tagihan masih aktif
            val isBillActive = !bill.isCompleted && !bill.status.equals("closed", ignoreCase = true)
            if (isAdmin && isBillActive) {
                binding.btnComplete.visibility = View.VISIBLE
                binding.btnComplete.setOnClickListener {
                    onCompleteClick?.invoke(bill)
                }
            } else {
                binding.btnComplete.visibility = View.GONE
            }

            // Tombol Hapus Khusus Admin
            if (isAdmin) {
                binding.btnDelete.visibility = View.VISIBLE
                binding.btnDelete.setOnClickListener {
                    onDeleteClick?.invoke(bill)
                }
            } else {
                binding.btnDelete.visibility = View.GONE
            }

            // Aksi klik tombol bayar / item
            binding.btnPay.setOnClickListener {
                onBillClick(bill)
            }
            binding.root.setOnClickListener {
                onBillClick(bill)
            }
        }

        private fun formatRupiah(number: Long): String {
            val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
            return format.format(number).replace("Rp", "Rp ").replace(",00", "")
        }
    }
}
