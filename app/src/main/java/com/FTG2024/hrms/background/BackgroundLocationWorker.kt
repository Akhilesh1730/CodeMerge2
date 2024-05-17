package com.FTG2024.hrms.background

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.FTG2024.hrms.application.TokenManager
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.login.model.Data
import com.FTG2024.hrms.retrofit.RetrofitHelper
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class BackgroundLocationWorker(private val context: Context, private val workerParams: WorkerParameters) :  Worker(context, workerParams) {
  private lateinit var sharedPref : SharedPreferences
    private  var latitude: Double = 0.0
    private  var longitude: Double = 0.0
    private lateinit var tokenManager : TokenManager
    private lateinit var apiService : BackgroundApiService
    override fun doWork(): Result {
       Log.d("###", "doWork: work called ${LocalTime.now()}")
        sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        tokenManager = TokenManagerImpl(context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE))
        apiService = RetrofitHelper.getRetrofitInstance(tokenManager).create(BackgroundApiService::class.java)
       val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                        storeLocation(latitude, longitude)
                        Log.d("####", "doWork: Location Called")
                    } else {
                        Log.d("####", "doWork: $location")
                    }
                }
                .addOnFailureListener { e ->

                }
            Log.d("####", "doWork: Not Granted")
        }

        return Result.success()
    }


    private fun storeLocation(latitude: Double, longitude: Double) {
        val newLocObj = LocationData(formattedDate(),
            getEmployeeData().get(0).UserData.get(0).EMP_ID,
            decimalToDMS(latitude, true),
            decimalToDMS(longitude, true),
            formattedTime())
        var request = HourLocationRequest(listOf(newLocObj))
        val locObj = getObjFromSharedPref()
        if (locObj != null) {
            val list = locObj.locationData as MutableList
            list.add(newLocObj)
            request = HourLocationRequest(list)
            addObJToSharedPref(request)
        } else {
            addObJToSharedPref(request)
        }
        if (isConnectedToNetwork()) {
            Log.d("####", "storeLocation: isCOnnected true ")
            CoroutineScope(Dispatchers.IO).async {
                sendLocationToServer(request)
            }
        }  else {
            Log.d("####", "storeLocation: isCOnnected false ")
        }
    }

    private fun addObJToSharedPref(request: HourLocationRequest) {
        val gson = Gson()
        val locJson = gson.toJson(request)
        val editor = sharedPref.edit()
        editor.putString("loc_obj", locJson)
        editor.apply()
    }

    private fun getObjFromSharedPref() : HourLocationRequest? {
        val list = mutableListOf<HourLocationRequest>()
        if (sharedPref != null) {
            val locJson = sharedPref.getString("loc_obj", null)
            if (locJson != null) {
                val gson = Gson()
                return gson.fromJson(locJson, HourLocationRequest::class.java)
            }
        }
        return null
    }

    private suspend fun sendLocationToServer(request: HourLocationRequest) {
        Log.d("###", "sendLocationToServer: ${isConnectedToNetwork()}")
            val job = CoroutineScope(Dispatchers.Main).async {
                apiService.sendLocation(request)
            }
        val response = job.await()
        Log.d("###", "sendLocationToServer: $response")
        if (response.isSuccessful) {
            val editor = sharedPref.edit()
            editor.remove("loc_obj") // Remove the location data object from SharedPreferences
            editor.apply()
            Log.d("###", "sendLocationToServer: Shared preferences cleared")
        } else {
            Log.d("###", "sendLocationToServer: Failed")
        }
    }

    private fun isConnectedToNetwork(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    fun decimalToDMS(decimal: Double, isLatitude: Boolean): String {
        val degrees = decimal.toInt()
        val minutes = ((decimal - degrees) * 60).toInt()
        val seconds = ((decimal - degrees - minutes / 60.0) * 3600).toFloat()

        val direction = if (isLatitude) {
            if (degrees >= 0) "N" else "S"
        } else {
            if (degrees >= 0) "E" else "W"
        }

        return String.format("%d°%02d'%05.2f\"%s", degrees, minutes, seconds, direction)
    }

    fun formattedDate(): String? {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }

    fun formattedTime(): String? {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return currentTime.format(formatter)
    }
    private fun getEmployeeData() : List<Data> {
        val sharedPref = context.getSharedPreferences("employee_detail_pref", Context.MODE_PRIVATE)
        val dataListJson = sharedPref.getString("employeeDataListKey", null)
        if (dataListJson != null) {
            val gson = Gson()
            val dataList: List<Data> =
                gson.fromJson(dataListJson, object : TypeToken<List<Data>>() {}.type)
            Log.d("#####", "getEmployeeData: ${dataList[0].UserData.get(0).EMP_ID}")
            return dataList
        }
        return listOf()
    }
}