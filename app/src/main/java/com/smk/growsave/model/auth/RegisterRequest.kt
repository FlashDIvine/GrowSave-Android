package com.smk.growsave.model.auth

import com.google.gson.annotations.SerializedName

/**
 * Model request untuk pendaftaran pengguna baru (Register).
 */
data class RegisterRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("role")
    val role: String = "user",

    @SerializedName("room_code")
    val roomCode: String? = null
)
