package com.smk.growsave.model

import com.google.gson.annotations.SerializedName

/**
 * Data class untuk merepresentasikan entitas Pengguna (User).
 * Model ini bersifat umum sehingga diletakkan langsung di dalam package 'model'.
 */
data class User(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("house_block")
    val houseBlock: String? = null,

    @SerializedName("house_number")
    val houseNumber: String? = null,

    // Menggunakan Enum UserRole agar pengecekan role di aplikasi bersifat type-safe
    @SerializedName("role")
    val role: UserRole
)

/**
 * Enum class untuk mendefinisikan Role User yang valid dari server.
 * Menghindari penggunaan string manual yang rawan typo.
 */
enum class UserRole {
    @SerializedName("admin")
    ADMIN,

    @SerializedName("user")
    USER
}
