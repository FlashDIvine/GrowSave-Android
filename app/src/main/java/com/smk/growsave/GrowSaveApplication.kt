package com.smk.growsave

import android.app.Application

/**
 * Custom Application class untuk menyediakan Application Context secara global.
 * Dibutuhkan oleh RetrofitClient (singleton) agar dapat mengakses SessionManager
 * tanpa bergantung pada Activity/Fragment tertentu.
 */
class GrowSaveApplication : Application() {

    companion object {
        lateinit var instance: GrowSaveApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
