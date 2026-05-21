package com.smk.growsave.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
        holder.bind(bills[position], onBillClick)
    }

    override fun getItemCount(): Int = bills.size

    class BillViewHolder(
        private val binding: ItemBillBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bill: Bill, onBillClick: (Bill) -> Unit) {
            binding.tvTitle.text = bill.title
            binding.tvDueDate.text = "Jatuh tempo: ${bill.dueDate}"
            binding.tvAmount.text = formatRupiah(bill.amount)

            val context = binding.root.context

            // Warna status berbeda (paid = hijau, unpaid = merah)
            if (bill.status.equals("paid", ignoreCase = true)) {
                binding.tvStatus.text = "PAID"
                binding.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
            } else {
                binding.tvStatus.text = "UNPAID"
                binding.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            }

            // Aksi klik item tagihan
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
