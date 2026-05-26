package com.smk.growsave.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.smk.growsave.GrowSaveApplication
import com.smk.growsave.utils.SessionManager
import com.smk.growsave.utils.Event
import java.util.concurrent.TimeUnit

/**
 * RetrofitClient adalah sebuah singleton object.
 * Singleton memastikan hanya ada satu instance Retrofit dan OkHttpClient yang dibuat
 * selama aplikasi berjalan untuk menghemat memori dan resource koneksi.
 */
object RetrofitClient {

    // Base URL dari API yang akan dihubungi. Pastikan selalu diakhiri dengan tanda slash '/'
    private const val BASE_URL = "https://doorway-mortality-overload.ngrok-free.dev/"// Contoh URL Mock API

    /**
     * Logging Interceptor digunakan untuk mencetak log dari request dan response HTTP.
     * Sangat membantu developer untuk melihat data apa saja yang dikirim dan diterima di Logcat.
     */
    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    /**
     * Auth Interceptor bertugas:
     * 1. Menambahkan/memformat Authorization header secara otomatis (Bearer prefix).
     * 2. Mendeteksi response 401 Unauthorized, membersihkan sesi, dan memicu event
     *    isSessionExpired agar UI layer (MainActivity) melakukan redirect ke LoginActivity.
     *
     * PENTING: Interceptor ini TIDAK melakukan navigasi Activity atau menampilkan Toast.
     *          Hal tersebut ditangani sepenuhnya oleh UI layer (decoupled).
     */
    private val authInterceptor: Interceptor by lazy {
        Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()

            // Cek apakah Authorization header sudah ada di request
            val existingAuth = originalRequest.header("Authorization")

            if (existingAuth != null && existingAuth.isNotEmpty()) {
                // Header ada tapi belum berformat "Bearer ...", tambahkan prefix
                if (!existingAuth.startsWith("Bearer ")) {
                    requestBuilder.header("Authorization", "Bearer $existingAuth")
                }
                // Jika sudah berformat "Bearer ...", biarkan apa adanya
            } else {
                // Tidak ada header Authorization, coba ambil token dari SessionManager
                try {
                    val sessionManager = SessionManager(GrowSaveApplication.instance)
                    val token = sessionManager.getToken()
                    if (!token.isNullOrEmpty()) {
                        requestBuilder.header("Authorization", "Bearer $token")
                    }
                } catch (e: Exception) {
                    Log.w("RetrofitClient", "Gagal mengambil token dari SessionManager: ${e.message}")
                }
            }

            val response: Response = chain.proceed(requestBuilder.build())

            // Deteksi 401 Unauthorized secara global
            if (response.code == 401) {
                try {
                    val sessionManager = SessionManager(GrowSaveApplication.instance)
                    sessionManager.clearSession()
                    SessionManager.isSessionExpired.postValue(Event(true))
                    Log.w("RetrofitClient", "HTTP 401 terdeteksi. Sesi dihapus dan event isSessionExpired dipicu.")
                } catch (e: Exception) {
                    Log.e("RetrofitClient", "Gagal menghapus sesi setelah 401: ${e.message}")
                }
            }

            response
        }
    }

    /**
     * OkHttpClient menangani urusan koneksi level rendah, seperti timeout dan logging.
     * Menggunakan lazy initialization agar instance client hanya dibuat saat pertama kali dipanggil.
     */
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)    // Interceptor auth (Bearer + 401 handling)
            .addInterceptor(loggingInterceptor) // Tambahkan logging interceptor ke OkHttpClient
            .connectTimeout(30, TimeUnit.SECONDS) // Waktu tunggu maksimal untuk terhubung ke server
            .readTimeout(30, TimeUnit.SECONDS)    // Waktu tunggu maksimal untuk membaca data dari server
            .writeTimeout(30, TimeUnit.SECONDS)   // Waktu tunggu maksimal untuk mengirim data ke server
            .build()
    }

    /**
     * Retrofit Instance utama yang mengonfigurasikan base URL, converter, dan client.
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Menentukan URL utama API
            .client(okHttpClient) // Mengaitkan dengan OkHttpClient yang dikonfigurasi di atas
            .addConverterFactory(GsonConverterFactory.create()) // Converter untuk mengubah JSON menjadi Object Kotlin
            .build()
    }

    /**
     * ApiService yang siap digunakan oleh komponen lain (seperti Repository) untuk memanggil API.
     * Dibuat secara lazy agar instansiasi hanya terjadi saat dibutuhkan.
     */
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}