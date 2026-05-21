package com.smk.growsave.model.auth

import com.google.gson.annotations.SerializedName
import com.smk.growsave.model.User

/**
 * Data class yang menampung isi dari field 'data' ketika login berhasil.
 */
data class LoginResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("user")
    val user: User
)
