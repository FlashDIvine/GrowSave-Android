package com.smk.growsave.model

import com.google.gson.annotations.SerializedName

/**
 * Model data untuk merepresentasikan satu entitas transaksi keuangan.
 */
data class Transaction(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("type")
    val type: String, // "income" atau "expense"

    @SerializedName("amount")
    val amount: Long,

    @SerializedName("created_at")
    val createdAt: String
)
