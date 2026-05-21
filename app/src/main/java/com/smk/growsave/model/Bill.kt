package com.smk.growsave.model

import com.google.gson.annotations.SerializedName

/**
 * Model data untuk merepresentasikan satu tagihan (bill).
 */
data class Bill(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("amount")
    val amount: Long,

    @SerializedName("due_date")
    val dueDate: String,

    @SerializedName("status")
    val status: String // "paid" atau "unpaid"
)
