package com.FTG2024.hrms.background

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.FTG2024.hrms.application.TokenManager
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.retrofit.RetrofitHelper
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalTime

class NetStatusWorker(private val context: Context, private val workerParams: WorkerParameters) :  Worker(context, workerParams) {
    private lateinit var sharedPref : SharedPreferences
    private lateinit var tokenManager : TokenManager
    private lateinit var apiService : BackgroundApiService
    override fun doWork(): Result {
        Log.d("####", "doWork netStatus: called ${LocalTime.now()}")
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        tokenManager = TokenManagerImpl(context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE))
        apiService = RetrofitHelper.getRetrofitInstance(tokenManager).create(BackgroundApiService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            sendLocationToServer(getObjFromSharedPref())
        }

        return Result.success()
    }

    private fun getObjFromSharedPref() : HourLocationRequest? {
        val list = mutableListOf<HourLocationRequest>()
        Log.d("###", "getObjFromSharedPref: $sharedPref")
        if (sharedPref != null) {
            val locJson = sharedPref.getString("loc_obj", null)
            if (locJson != null) {
                val gson = Gson()
                return gson.fromJson(locJson, HourLocationRequest::class.java)
            }
        }
        return null
    }

    private suspend fun sendLocationToServer(request: HourLocationRequest?)  {
       val response = CoroutineScope(Dispatchers.IO).async {
            apiService.sendLocation(request)
        }.await()
        if (response.isSuccessful) {
                val editor = sharedPref.edit()
                editor.remove("loc_obj") // Remove the location data object from SharedPreferences
                editor.apply()
                Log.d("###", "sendLocationToServer: Shared preferences cleared")
            } else {
                Log.d("###", "sendLocationToServer: Failed")
            }
    }
}
