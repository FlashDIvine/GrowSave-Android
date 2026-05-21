package com.smk.growsave.model

import com.google.gson.annotations.SerializedName

/**
 * BaseResponse adalah generic data class yang digunakan untuk membungkus
 * seluruh response API dari server yang memiliki struktur seragam.
 *
 * Parameter <T> (Generic Type) memungkinkan kita untuk menggunakan kelas ini
 * dengan berbagai macam tipe data yang berbeda pada field 'data'.
 */
data class BaseResponse<T>(
    // @SerializedName memberi tahu Gson nama key yang ada di dalam JSON API.
    // Ini sangat berguna agar nama properti di Kotlin tetap bisa mengikuti aturan camelCase.
    
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: T? // Menggunakan nullable (?) karena ada kalanya data bernilai null (misal saat error atau respon kosong)
)
