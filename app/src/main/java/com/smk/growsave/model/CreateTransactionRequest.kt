package com.smk.growsave.model

import com.google.gson.annotations.SerializedName

data class CreateTransactionRequest(
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String, // "income" or "expense"
    @SerializedName("amount")
    val amount: Long
)
