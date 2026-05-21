package com.smk.growsave.model.auth

import com.google.gson.annotations.SerializedName

/**
 * Data class untuk menampung data input login dari pengguna.
 * Dikirim sebagai Request Body pada endpoint Login API.
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)
