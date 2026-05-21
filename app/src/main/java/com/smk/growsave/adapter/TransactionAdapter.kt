package com.smk.growsave.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.smk.growsave.databinding.ItemTransactionBinding
import com.smk.growsave.model.Transaction
import java.text.NumberFormat
import java.util.Locale

/**
 * TransactionAdapter menghubungkan data daftar transaksi ke dalam RecyclerView.
 */
class TransactionAdapter(
    private var transactions: List<Transaction> = emptyList()
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    /**
     * Memperbarui data list transaksi di adapter.
     */
    fun submitList(newList: List<Transaction>) {
        transactions = newList
        notifyDataSetChanged() // Memberitahukan RecyclerView bahwa data telah berubah
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size

    class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.tvTitle.text = transaction.title
            binding.tvType.text = transaction.type.lowercase(Locale.getDefault())
            binding.tvDate.text = transaction.createdAt

            // Format angka menjadi mata uang rupiah (contoh: Rp 50.000)
            val formattedAmount = formatRupiah(transaction.amount)
            val context = binding.root.context

            // Pembedaan visual untuk pemasukan (income) dan pengeluaran (expense)
            if (transaction.type.equals("income", ignoreCase = true)) {
                binding.tvAmount.text = "+ $formattedAmount"
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
                binding.tvType.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
            } else {
                binding.tvAmount.text = "- $formattedAmount"
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                binding.tvType.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            }
        }

        private fun formatRupiah(number: Long): String {
            val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
            // Sederhanakan format nominal rupiah
            return format.format(number).replace("Rp", "Rp ").replace(",00", "")
        }
    }
}
