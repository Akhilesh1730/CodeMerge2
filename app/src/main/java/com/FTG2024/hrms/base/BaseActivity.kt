package com.FTG2024.hrms.base

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.Operation
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager

import com.FTG2024.hrms.R
import com.FTG2024.hrms.background.BackgroundLocationWorker
import com.FTG2024.hrms.background.NetStatusWorker
import com.FTG2024.hrms.dialog.ProgressDialog
import java.util.concurrent.TimeUnit

open class BaseActivity : AppCompatActivity() {
    lateinit var baseViewModel: BaseViewModel
    private  var isAsked : Boolean = false
    private var isFirst : Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseViewModel = ViewModelProvider(this).get(BaseViewModel::class.java)
        setContentView(R.layout.activity_base)
    }

    private val LOCATION_PERMISSION_REQUEST_CODE = 2020
    private val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 2021

    protected fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    protected fun hasBackgroundLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    protected fun requestLocationPermission() {
        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!hasBackgroundLocationPermission()) {
                    requestBackgroundPermission()
                } else {
                    baseViewModel.setLocationPermissionLiveData(true)
                }
            } else {
                baseViewModel.setLocationPermissionLiveData(true)
            }
        }
        /*if (!hasLocationPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Request "Always Allow" on Android 11 and above

            } else {
                // Request "While using this app" on older versions
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }*/
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    baseViewModel.setLocationPermissionLiveData(false)
                } else if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.d("###", "onRequestPermissionsResult: ")
                    if (isAsked && !isFirst) {
                        Log.d("###", "onRequestPermissionsResult: $isAsked and $isFirst")
                        baseViewModel.setLocationPermissionLiveData(false)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION),
                            LOCATION_PERMISSION_REQUEST_CODE
                        )
                        isAsked = true
                        isFirst = false
                    }
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("###", "onRequestPermissionsResult: BG")
                    requestBackgroundPermission()
                } else {
                    baseViewModel.setLocationPermissionLiveData(false)
                }
            }
          /*  if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }*/
        } else if (requestCode == BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                baseViewModel.setLocationPermissionLiveData(true)
            } else {
                baseViewModel.setLocationPermissionLiveData(false)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun requestBackgroundPermission() {
        Log.d("###", "requestBackgroundPermission: Bg")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Location Permission Needed")
        builder.setMessage("This app needs to monitor your location in the background for attendance purpose. Please enable \"${packageManager.backgroundPermissionOptionLabel}\" permission in the settings.")
        builder.setPositiveButton("Allow All Time") { dialog, which ->
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
            )
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            baseViewModel.setLocationPermissionLiveData(false)
            dialog.dismiss()
        }
        builder.show()
    }
    fun isGpsEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isGpsEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            locationManager.isLocationEnabled
        } else {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

        return isGpsEnabled
    }

    fun isNetworkEnabled(context: Context) : Boolean {
        val service = Context.CONNECTIVITY_SERVICE
        val manager = context.getSystemService(service) as ConnectivityManager?
        val network = manager?.activeNetwork
        return (network != null)
    }

    protected fun showToast(msg : String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    protected fun getProgressDialog(msg : String): ProgressDialog {
        return ProgressDialog(this, msg)
    }

    protected  fun isDevModeOn() : Boolean {
        val devoption = Settings.Secure.getInt(this.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0)
        return devoption == 1
    }


}