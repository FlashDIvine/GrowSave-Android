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
            binding.tvType.text = transaction.type.uppercase(Locale.getDefault())
            binding.tvDate.text = transaction.createdAt

            // Format angka menjadi mata uang rupiah (contoh: Rp 50.000)
            val formattedAmount = formatRupiah(transaction.amount)
            val context = binding.root.context

            // Pembedaan visual untuk pemasukan (income) dan pengeluaran (expense)
            if (transaction.type.equals("income", ignoreCase = true)) {
                binding.tvAmount.text = "+ $formattedAmount"
                binding.tvAmount.setTextColor(android.graphics.Color.parseColor("#0D7B43"))
                binding.tvType.setTextColor(android.graphics.Color.parseColor("#0D7B43"))
                binding.tvType.setBackgroundResource(com.smk.growsave.R.drawable.bg_badge_success)
                binding.cvIconContainer.setCardBackgroundColor(android.graphics.Color.parseColor("#E6F4EA"))
                binding.ivTransactionIcon.setImageResource(com.smk.growsave.R.drawable.ic_arrow_down)
                binding.ivTransactionIcon.setColorFilter(android.graphics.Color.parseColor("#0D7B43"))
            } else {
                binding.tvAmount.text = "- $formattedAmount"
                binding.tvAmount.setTextColor(android.graphics.Color.parseColor("#C81E1E"))
                binding.tvType.setTextColor(android.graphics.Color.parseColor("#C81E1E"))
                binding.tvType.setBackgroundResource(com.smk.growsave.R.drawable.bg_badge_danger)
                binding.cvIconContainer.setCardBackgroundColor(android.graphics.Color.parseColor("#FDE8E8"))
                binding.ivTransactionIcon.setImageResource(com.smk.growsave.R.drawable.ic_arrow_up)
                binding.ivTransactionIcon.setColorFilter(android.graphics.Color.parseColor("#C81E1E"))
            }
        }

        private fun formatRupiah(number: Long): String {
            val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
            // Sederhanakan format nominal rupiah
            return format.format(number).replace("Rp", "Rp ").replace(",00", "")
        }
    }
}
