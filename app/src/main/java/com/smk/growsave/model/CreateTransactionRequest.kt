package com.smk.growsave.model

import com.google.gson.annotations.SerializedName

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CreateTransactionRequest(
    @SerializedName("category")
    val title: String,
    @SerializedName("type")
    val type: String, // "income" or "expense"
    @SerializedName("amount")
    val amount: Long,
    @SerializedName("transaction_date")
    val transactionDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    @SerializedName("description")
    val description: String? = ""
)
