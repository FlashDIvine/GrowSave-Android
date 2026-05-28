package com.smk.growsave.model

/**
 * Data class untuk menyimpan statistik tagihan yang dihitung dari list Bills.
 * Digunakan oleh FinanceFragment untuk menampilkan dashboard stats.
 *
 * Semua field menggunakan naming convention yang jelas:
 * - totalBills: jumlah semua tagihan
 * - activeBills: jumlah tagihan yang masih aktif (belum lunas/ditutup)
 * - paidBills: jumlah tagihan yang sudah lunas/selesai
 * - totalUnpaidAmount: total nominal tagihan yang belum dibayar (dalam Rupiah)
 */
data class BillStats(
    val totalBills: Int = 0,
    val activeBills: Int = 0,
    val paidBills: Int = 0,
    val totalUnpaidAmount: Long = 0L
)
