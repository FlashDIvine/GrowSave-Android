package com.smk.growsave.network

import com.smk.growsave.model.BaseResponse
import com.smk.growsave.model.PaymentRequest
import com.smk.growsave.model.PaymentResponse
import com.smk.growsave.model.auth.LoginRequest
import com.smk.growsave.model.auth.LoginResponse
import com.smk.growsave.model.auth.RegisterRequest
import com.smk.growsave.model.Bill
import com.smk.growsave.model.Transaction
import com.smk.growsave.model.Announcement
import com.smk.growsave.model.RoomRequest
import com.smk.growsave.model.RoomMember
import com.smk.growsave.model.CreateBillRequest
import com.smk.growsave.model.CreateTransactionRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.DELETE

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
    ): BaseResponse<com.smk.growsave.model.TransactionResponse>

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

    @POST("api/bills")
    suspend fun createBill(
        @Header("Authorization") token: String,
        @Body request: CreateBillRequest
    ): BaseResponse<Bill>

    @POST("api/bills/{id}/complete")
    suspend fun completeBill(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): BaseResponse<Bill>

    @POST("api/transactions")
    suspend fun createTransaction(
        @Header("Authorization") token: String,
        @Body request: CreateTransactionRequest
    ): BaseResponse<Transaction>

    @Multipart
    @POST("api/announcements")
    suspend fun createAnnouncement(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part("category") category: RequestBody?,
        @Part image: MultipartBody.Part?
    ): BaseResponse<Announcement>

    @GET("api/room/requests")
    suspend fun getRoomRequests(
        @Header("Authorization") token: String
    ): BaseResponse<List<RoomRequest>>

    @GET("api/room")
    suspend fun getRoom(
        @Header("Authorization") token: String
    ): BaseResponse<com.smk.growsave.model.RoomDetailResponse>

    @GET("api/room/residents")
    suspend fun getRoomResidents(
        @Header("Authorization") token: String
    ): BaseResponse<List<RoomMember>>

    @POST("api/room/approve/{id}")
    suspend fun approveRoom(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): BaseResponse<Unit>

    @POST("api/room/reject/{id}")
    suspend fun rejectRoom(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): BaseResponse<Unit>

    @DELETE("api/announcements/{id}")
    suspend fun deleteAnnouncement(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): BaseResponse<Unit>

    @DELETE("api/bills/{id}")
    suspend fun deleteBill(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): BaseResponse<Unit>
}