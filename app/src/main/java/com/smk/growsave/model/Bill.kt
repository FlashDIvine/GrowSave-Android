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

    @SerializedName("description")
    val description: String?,

    @SerializedName("amount")
    val amount: Long,

    @SerializedName("due_date")
    val dueDate: String,

    @SerializedName("status")
    val status: String, // "active" atau "closed"

    @SerializedName("target_amount")
    val targetAmount: Long,

    @SerializedName("collected_amount")
    val collectedAmount: Long,

    @SerializedName("required_amount")
    val requiredAmount: Long,

    @SerializedName("is_completed")
    val isCompleted: Boolean,

    @SerializedName("user_payment_status")
    val userPaymentStatus: String // "paid", "pending", "unpaid"
)
