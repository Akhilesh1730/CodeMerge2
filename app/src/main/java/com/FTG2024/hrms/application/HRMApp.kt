package com.FTG2024.hrms.application

import android.app.Application
import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.FTG2024.hrms.background.BackgroundLocationWorker
import java.util.concurrent.TimeUnit

class HRMApp() : Application() {

        override fun onCreate() {
            super.onCreate()
            tokenManager = TokenManagerImpl(getSharedPreferences("user_prefs", Context.MODE_PRIVATE))
        }
     fun startLocationUpdates() {
        val constraint = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val workRequest = PeriodicWorkRequest
            .Builder(BackgroundLocationWorker ::class.java, 15, TimeUnit.MINUTES)
            .setConstraints(constraint)
            .build()
        WorkManager.getInstance(this).enqueue(workRequest)
       /* val repeatingRequest = PeriodicWorkRequestBuilder<BackgroundLocationWorker>(1, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "location_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )*/
    }

    companion object {
        lateinit var tokenManager: TokenManager
    }
}