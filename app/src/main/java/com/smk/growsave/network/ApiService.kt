package com.smk.growsave.network

import com.smk.growsave.model.BaseResponse
import com.smk.growsave.model.PaymentRequest
import com.smk.growsave.model.PaymentResponse
import com.smk.growsave.model.auth.LoginRequest
import com.smk.growsave.model.auth.LoginResponse
import com.smk.growsave.model.auth.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Data class untuk merepresentasikan data Post dari API JsonPlaceholder.
 * Serialisasi JSON ke Object Kotlin akan dilakukan secara otomatis oleh Gson.
 */
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

/**
 * ApiService mendefinisikan endpoint API yang akan dipanggil.
 * Kita menggunakan keyword 'suspend' agar fungsi ini mendukung Kotlin Coroutines,
 * sehingga request jaringan berjalan di background thread secara non-blocking.
 */
interface ApiService {

    // Endpoint untuk mendapatkan daftar semua post (https://jsonplaceholder.typicode.com/posts)
    @GET("posts")
    suspend fun getPosts(): List<Post>

    // Endpoint untuk mendapatkan detail post berdasarkan ID (https://jsonplaceholder.typicode.com/posts/{id})
    @GET("posts/{id}")
    suspend fun getPostById(
        @Path("id") postId: Int
    ): Post

    /**
     * Endpoint untuk melakukan login pengguna.
     * Mengirimkan email dan password dalam request body (@Body),
     * serta mengembalikan respon berupa BaseResponse yang membungkus LoginResponse.
     */
    @POST("api/login")
    suspend fun login(
        @Body request: LoginRequest
    ): BaseResponse<LoginResponse>

    /**
     * Endpoint untuk mendaftarkan pengguna baru.
     * Mengirimkan data registrasi dalam request body.
     * Response identik dengan login (berisi token dan user).
     */
    @POST("api/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): BaseResponse<LoginResponse>

    /**
     * Endpoint untuk mendapatkan daftar transaksi keuangan.
     * Membutuhkan token JWT sebagai Bearer token di Authorization header.
     */
    @GET("api/transactions")
    suspend fun getTransactions(
        @Header("Authorization") token: String
    ): BaseResponse<List<com.smk.growsave.model.Transaction>>

    /**
     * Endpoint untuk mendapatkan daftar pengumuman.
     * Membutuhkan token JWT sebagai Bearer token di Authorization header.
     */
    @GET("api/announcements")
    suspend fun getAnnouncements(
        @Header("Authorization") token: String
    ): BaseResponse<List<com.smk.growsave.model.Announcement>>

    /**
     * Endpoint untuk mendapatkan daftar tagihan (bills).
     * Membutuhkan token JWT sebagai Bearer token di Authorization header.
     */
    @GET("api/bills")
    suspend fun getBills(
        @Header("Authorization") token: String
    ): BaseResponse<List<com.smk.growsave.model.Bill>>

    /**
     * Endpoint untuk membuat token transaksi pembayaran (Midtrans Snap).
     * Membutuhkan token JWT sebagai Bearer token di Authorization header.
     */
    @POST("api/payments")
    suspend fun createPayment(
        @Header("Authorization") token: String,
        @Body request: PaymentRequest
    ): BaseResponse<PaymentResponse>
}