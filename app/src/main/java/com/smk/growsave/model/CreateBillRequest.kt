package com.smk.growsave.model

import com.google.gson.annotations.SerializedName

data class CreateBillRequest(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("target_amount")
    val targetAmount: Long,
    @SerializedName("required_amount")
    val requiredAmount: Long,
    @SerializedName("due_date")
    val dueDate: String
)
