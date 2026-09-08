package com.acidsepp.rain

import android.app.Application
import android.content.Intent
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RainApplication : Application() {

    private lateinit var serviceIntent: Intent

    override fun onCreate() {
        super.onCreate()
        serviceIntent = Intent(this, RainService::class.java)
        startService(serviceIntent)
    }

    override fun onTerminate() {
        stopService(serviceIntent)
        super.onTerminate()
    }

}