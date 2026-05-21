package com.smk.growsave.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * RetrofitClient adalah sebuah singleton object.
 * Singleton memastikan hanya ada satu instance Retrofit dan OkHttpClient yang dibuat
 * selama aplikasi berjalan untuk menghemat memori dan resource koneksi.
 */
object RetrofitClient {

    // Base URL dari API yang akan dihubungi. Pastikan selalu diakhiri dengan tanda slash '/'
    private const val BASE_URL = "http://10.0.2.2:8000/"// Contoh URL Mock API

    /**z
     * Logging Interceptor digunakan untuk mencetak log dari request dan response HTTP.
     * Sangat membantu developer untuk melihat data apa saja yang dikirim dan diterima di Logcat.
     */
    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    /**
     * OkHttpClient menangani urusan koneksi level rendah, seperti timeout dan logging.
     * Menggunakan lazy initialization agar instance client hanya dibuat saat pertama kali dipanggil.
     */
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
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