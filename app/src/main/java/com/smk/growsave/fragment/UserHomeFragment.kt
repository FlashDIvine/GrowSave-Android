package com.smk.growsave.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.smk.growsave.databinding.FragmentHomeBinding
import com.smk.growsave.network.RetrofitClient
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/**
 * UserHomeFragment digunakan khusus untuk dashboard role Warga/User.
 * Menampilkan ringkasan saldo kas komunitas dan daftar pengeluaran/pemasukan terakhir secara real-time.
 */
class UserHomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: TransactionViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi SessionManager & ViewModel
        sessionManager = SessionManager(requireContext())
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        setupObservers()
        fetchData()
    }



    private fun setupObservers() {
        // Mengamati total saldo kas
        viewModel.totalSaldo.observe(viewLifecycleOwner) { total ->
            binding.tvTotalSaldo.text = formatRupiah(total)
        }

        // Mengamati pemasukan bulan ini
        viewModel.pemasukanBulanIni.observe(viewLifecycleOwner) { pemasukan ->
            binding.tvPemasukan.text = formatRupiah(pemasukan)
        }

        // Mengamati pengeluaran bulan ini
        viewModel.pengeluaranBulanIni.observe(viewLifecycleOwner) { pengeluaran ->
            binding.tvPengeluaran.text = formatRupiah(pengeluaran)
        }

        // Mengamati riwayat transaksi terakhir untuk ditampilkan di layout (maksimal 3 item)
        viewModel.transactions.observe(viewLifecycleOwner) { list ->
            val recentList = list.take(3)

            // Item 1
            if (recentList.isNotEmpty()) {
                binding.layoutItem1.visibility = View.VISIBLE
                val item = recentList[0]
                binding.tvTitle1.text = item.title
                binding.tvAmount1.text = if (item.type.equals("income", ignoreCase = true)) {
                    "+ ${formatRupiah(item.amount.toDouble())}"
                } else {
                    "- ${formatRupiah(item.amount.toDouble())}"
                }
                binding.tvAmount1.setTextColor(
                    if (item.type.equals("income", ignoreCase = true)) {
                        android.graphics.Color.parseColor("#0D7B43")
                    } else {
                        android.graphics.Color.parseColor("#C81E1E")
                    }
                )
                binding.tvDate1.text = formatCleanDate(item.createdAt)
                binding.tvSub1.text = if (item.type.equals("income", ignoreCase = true)) "Pemasukan Kas" else "Pengeluaran Kas"
                
                binding.iconItem1.setImageResource(
                    if (item.type.equals("income", ignoreCase = true)) {
                        com.smk.growsave.R.drawable.ic_arrow_down
                    } else {
                        com.smk.growsave.R.drawable.ic_arrow_up
                    }
                )
                binding.iconItem1.setColorFilter(
                    if (item.type.equals("income", ignoreCase = true)) {
                        android.graphics.Color.parseColor("#0D7B43")
                    } else {
                        android.graphics.Color.parseColor("#C81E1E")
                    }
                )
            } else {
                binding.layoutItem1.visibility = View.GONE
            }

            // Item 2
            if (recentList.size > 1) {
                binding.layoutItem2.visibility = View.VISIBLE
                val item = recentList[1]
                binding.tvTitle2.text = item.title
                binding.tvAmount2.text = if (item.type.equals("income", ignoreCase = true)) {
                    "+ ${formatRupiah(item.amount.toDouble())}"
                } else {
                    "- ${formatRupiah(item.amount.toDouble())}"
                }
                binding.tvAmount2.setTextColor(
                    if (item.type.equals("income", ignoreCase = true)) {
                        android.graphics.Color.parseColor("#0D7B43")
                    } else {
                        android.graphics.Color.parseColor("#C81E1E")
                    }
                )
                binding.tvDate2.text = formatCleanDate(item.createdAt)
                binding.tvSub2.text = if (item.type.equals("income", ignoreCase = true)) "Pemasukan Kas" else "Pengeluaran Kas"
                
                binding.iconItem2.setImageResource(
                    if (item.type.equals("income", ignoreCase = true)) {
                        com.smk.growsave.R.drawable.ic_arrow_down
                    } else {
                        com.smk.growsave.R.drawable.ic_arrow_up
                    }
                )
                binding.iconItem2.setColorFilter(
                    if (item.type.equals("income", ignoreCase = true)) {
                        android.graphics.Color.parseColor("#0D7B43")
                    } else {
                        android.graphics.Color.parseColor("#C81E1E")
                    }
                )
            } else {
                binding.layoutItem2.visibility = View.GONE
            }

            // Item 3
            if (recentList.size > 2) {
                binding.layoutItem3.visibility = View.VISIBLE
                val item = recentList[2]
                binding.tvTitle3.text = item.title
                binding.tvAmount3.text = if (item.type.equals("income", ignoreCase = true)) {
                    "+ ${formatRupiah(item.amount.toDouble())}"
                } else {
                    "- ${formatRupiah(item.amount.toDouble())}"
                }
                binding.tvAmount3.setTextColor(
                    if (item.type.equals("income", ignoreCase = true)) {
                        android.graphics.Color.parseColor("#0D7B43")
                    } else {
                        android.graphics.Color.parseColor("#C81E1E")
                    }
                )
                binding.tvDate3.text = formatCleanDate(item.createdAt)
                binding.tvSub3.text = if (item.type.equals("income", ignoreCase = true)) "Pemasukan Kas" else "Pengeluaran Kas"
                
                binding.iconItem3.setImageResource(
                    if (item.type.equals("income", ignoreCase = true)) {
                        com.smk.growsave.R.drawable.ic_arrow_down
                    } else {
                        com.smk.growsave.R.drawable.ic_arrow_up
                    }
                )
                binding.iconItem3.setColorFilter(
                    if (item.type.equals("income", ignoreCase = true)) {
                        android.graphics.Color.parseColor("#0D7B43")
                    } else {
                        android.graphics.Color.parseColor("#C81E1E")
                    }
                )
            } else {
                binding.layoutItem3.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchData() {
        val token = sessionManager.getToken()
        if (token != null) {
            // Ambil data transaksi keuangan
            viewModel.fetchTransactions(token)

            // Ambil info detail room secara asinkron
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.getRoom(token)
                    val bindingObj = _binding
                    if (bindingObj != null && isAdded && view != null) {
                        if (response.success && response.data != null) {
                            bindingObj.tvRoomName.text = response.data.roomName
                        }
                    }
                } catch (e: Exception) {
                    val bindingObj = _binding
                    if (bindingObj != null && isAdded && view != null) {
                        bindingObj.tvRoomName.text = "Error Room"
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "Sesi berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatRupiah(number: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
        return format.format(number).replace("Rp", "Rp ").replace(",00", "")
    }

    private fun formatCleanDate(rawDate: String): String {
        return try {
            // Jika tanggal berupa ISO (mengandung 'T'), ambil porsi tanggal yyyy-MM-dd
            if (rawDate.contains("T")) {
                rawDate.substringBefore("T")
            } else {
                rawDate
            }
        } catch (e: Exception) {
            rawDate
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Mencegah kebocoran memori (memory leaks)
    }
}
