package com.smk.growsave.model

import com.google.gson.annotations.SerializedName

/**
 * Model data untuk merepresentasikan response beranggotakan ringkasan kas
 * beserta daftar riwayat transaksi dari endpoint API.
 */
data class TransactionResponse(
    @SerializedName("total_saldo")
    val totalSaldo: Double,

    @SerializedName("pemasukan_bulan_ini")
    val pemasukanBulanIni: Double,

    @SerializedName("pengeluaran_bulan_ini")
    val pengeluaranBulanIni: Double,

    @SerializedName("riwayat_transaksi")
    val riwayatTransaksi: List<Transaction>
)
