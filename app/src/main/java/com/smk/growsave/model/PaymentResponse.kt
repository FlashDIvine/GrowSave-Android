package com.smk.growsave.model

import com.google.gson.annotations.SerializedName

/**
 * Model response untuk menampung snap token pembayaran dari server.
 */
data class PaymentResponse(
    @SerializedName("snap_token")
    val snapToken: String
)
