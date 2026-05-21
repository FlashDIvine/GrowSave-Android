package com.smk.growsave.model

import com.google.gson.annotations.SerializedName

/**
 * Model request untuk membuat token transaksi pembayaran.
 */
data class PaymentRequest(
    @SerializedName("bill_id")
    val billId: Int
)
