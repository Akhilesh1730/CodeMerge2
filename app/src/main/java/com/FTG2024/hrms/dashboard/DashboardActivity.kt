package com.FTG2024.hrms.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.FTG2024.hrms.NoPermissionActivity
import com.FTG2024.hrms.R
import com.FTG2024.hrms.application.HRMApp
import com.FTG2024.hrms.application.TokenManager
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.assets.AssetsActivity
import com.FTG2024.hrms.attendancecalendar.AttendanceCalendarActivity
import com.FTG2024.hrms.background.BackgroundLocationWorker
import com.FTG2024.hrms.background.NetStatusWorker
import com.FTG2024.hrms.base.BaseActivity
import com.FTG2024.hrms.base.EmpIdRequest
import com.FTG2024.hrms.base.LocationResponse
import com.FTG2024.hrms.customers.AddCustomerActivity
import com.FTG2024.hrms.dashboard.dashBoardViewModel.DashBoardViewModel
import com.FTG2024.hrms.dashboard.dashBoardViewModel.DashBoardViewModelFactory
import com.FTG2024.hrms.dashboard.fragment.LocationBottomSheetFragment
import com.FTG2024.hrms.dashboard.model.DashBoardResponse
import com.FTG2024.hrms.dashboard.repo.DashBoardRepo
import com.FTG2024.hrms.dashboard.repo.DashboardApiService
import com.FTG2024.hrms.databinding.ActivityDashboardBinding
import com.FTG2024.hrms.dialog.ProgressDialog
import com.FTG2024.hrms.leaves.LeavesActivity
import com.FTG2024.hrms.login.model.Data
import com.FTG2024.hrms.markattendance.MarkAttendanceActivity
import com.FTG2024.hrms.profile.ProfileActivity
import com.FTG2024.hrms.retrofit.RetrofitHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStates
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class DashboardActivity : BaseActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            if (location != null) {
                deviceLat = location.latitude
                deviceLong = location.longitude
                // ... rest of your logic using the location data
            } else {
                // Handle case where location is still null
                Log.d("####", "getLocation: onLocationResult: location null")
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    private var position: Int = 0
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var dayInButton: TextView
    private lateinit var dayOutButton: TextView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var viewModel: DashBoardViewModel
    private lateinit var tokenManager: TokenManager
    private lateinit var retrofitHelper: Retrofit
    private lateinit var empID: String
    private lateinit var empName: String
    private lateinit var empOfM: String
    private var isDayin: Boolean = true
    private var isDayInAllowed: Boolean = true
    private var isDayOutAllowed: Boolean = false
    private var servLat: Double = 0.0
    private var servLong: Double = 0.0
    private var deviceLat: Double = 0.0
    private var deviceLong: Double = 0.0
    private var requiredDistance: Int = 0
    private var isRemarkRequired: Boolean = false
    private var isRefreshClicked: Boolean = false
    private lateinit var locationFragment: LocationBottomSheetFragment
    private lateinit var drawable: Drawable
    private  var isReporting : Boolean = false
    /*private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var isLocationRequested: Boolean = false*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tokenManager = TokenManagerImpl(getSharedPreferences("user_prefs", MODE_PRIVATE))
        retrofitHelper = RetrofitHelper.getRetrofitInstance(tokenManager)
        viewModel = ViewModelProvider(
            this,
            DashBoardViewModelFactory(DashBoardRepo(retrofitHelper.create(DashboardApiService::class.java)))
        ).get(DashBoardViewModel::class.java)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,100)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(2000)
            .setMaxUpdateDelayMillis(100).build()
        LocationServices.getFusedLocationProviderClient(applicationContext)
            .requestLocationUpdates(locationRequest, locationCallback, null)

        setCardsListeners()
        dayInButtonListener()
        binding.spinnerDashboard.onItemSelectedListener = object :  AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                isReporting = parent.getItemAtPosition(pos).toString().equals("HR Manager")
                position = pos
                setReportingSharedPref()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        setUpObserver()
        getEmployeeData()
        //(application as HRMApp).startLocationUpdates()
        Log.d("###", "onCreate: ")
        var isLoggedIn = intent.extras?.getBoolean("isLogin")
        if (isLoggedIn == true) {
            requestLocationPermission()
            baseViewModel.locationPermissionLiveData.observe(this, Observer { event ->
                Log.d("###", "onCreate: observe")
                event.getContentIfNotHandled().let {
                    if (it == true) {
                        isLoggedIn = false
                        setUp()
                    } else {
                        val intent = Intent(this, NoPermissionActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            })
        } else {
            setUp()
        }
    }

    override fun onResume() {
        super.onResume()
    }
    fun getEmployeeDetails() {

    }
    private fun setCardsListeners() {
        binding.cardviewAssetsDashboard.setOnClickListener {
            startActivity(Intent(this, AssetsActivity::class.java))
        }
        binding.cardviewAttendanceDashboard.setOnClickListener {
            startActivity(Intent(this, AttendanceCalendarActivity::class.java))
        }
        binding.cardviewCustomerDashboard.setOnClickListener {
            startActivity(Intent(this, AddCustomerActivity::class.java))
        }
        binding.imageViewProfileDashboard.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.cardviewLeavesDashboard.setOnClickListener {
            val intent = Intent(this, LeavesActivity::class.java)
            intent.putExtra("isReporting", isReporting)
            startActivity(intent)
        }
    }

    private fun setUp() {
        if (!isGpsEnabled(this)) {
            //showGPSSettingsDialog()
          /*  val locationRequest = LocationRequest.Builder()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .build()*/
            val locationRequest = LocationRequest.Builder(LocationRequest.PRIORITY_HIGH_ACCURACY, 1000L)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000L)
                .setMaxUpdateDelayMillis(10000L)
                .build()
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest( locationRequest)

            val client: SettingsClient = LocationServices.getSettingsClient(this)

            client.checkLocationSettings(builder.build())
                .addOnCompleteListener { task ->
                    try {
                        Log.d("###", "setUp:client ")
                        val response: LocationSettingsResponse = task.getResult(ApiException::class.java)
                      CoroutineScope(Dispatchers.Main).launch {
                          Log.d("###", "setUp:  ${response.locationSettingsStates}")
                      }


                    } catch (exception: ApiException) {
                        when (exception.statusCode) {
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                                // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                                    val resolvableApiException = exception as ResolvableApiException
                                    resolvableApiException.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                                } catch (e: IntentSender.SendIntentException) {
                                    // Ignore the error.
                                }
                            }
                            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                                // Location settings are not satisfied, and no way to fix it.
                            }
                        }
                    }
                }

        } else {

            progressDialog = getProgressDialog("Fetching Data")
            progressDialog.show()
            /* CoroutineScope(Dispatchers.IO).launch {
                 //setUpProfileImage()
             }*/
            Glide.with(this)
                .load("https://hrm.brothers.net.in/static/employeeProfile/20240522.jpg")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(binding.imageViewProfileDashboard)
            viewModel.getDashBoardData(EmpIdRequest(empID.toInt()))
        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            val states = LocationSettingsStates.fromIntent(data ?: return)

            when (resultCode) {
                Activity.RESULT_OK -> {
                    progressDialog = getProgressDialog("Fetching Data")
                       progressDialog.show()
                        CoroutineScope(Dispatchers.IO).launch {
                             //setUpProfileImage()
                         }
                        Glide.with(this)
                            .load("https://hrm.brothers.net.in/static/employeeProfile/20240522.jpg")
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(binding.imageViewProfileDashboard)
                        viewModel.getDashBoardData(EmpIdRequest(empID.toInt()))
                }
                Activity.RESULT_CANCELED -> {
                    // The user was asked to change settings, but chose not to
                    Log.d("###", "User chose not to make required location settings changes.")
                    // Handle the case where user did not enable the location settings
                }
                else -> {
                    Log.d("###", "Unhandled result code: $resultCode")
                }
            }
        }
    }



    /*private suspend fun setUpProfileImage() {
        val job = CoroutineScope(Dispatchers.IO).launch {
            drawable =
        }

        job.join()
    }*/

    private fun setUpObserver() {
        viewModel.getDashBoardLiveData().observe(this, Observer { event ->
            event.getContentIfNotHandled().let { response ->
                when (response) {
                    is com.FTG2024.hrms.uidata.Response.Success -> {
                        val dashResponse = response.data as DashBoardResponse
                        Log.d("####", "setUpObserver: ")
                        extractData(dashResponse)
                        progressDialog.dismiss()
                    }

                    is com.FTG2024.hrms.uidata.Response.Exception -> {
                        Log.d("###", "setUpObserver: ${response.message}")
                        progressDialog.dismiss()
                    }

                    else -> return@Observer
                }
            }
        })

        viewModel.getLocationLiveData().observe(this, Observer { event ->
            event.getContentIfNotHandled().let { response ->
                Log.d("####", "getLocationLiveData: $response")
                when (response) {
                    is com.FTG2024.hrms.uidata.Response.Success -> {
                        val location = response.data as LocationResponse
                        servLat = getDecimalValueOfLocation(location.data[0].LATITUDE)
                        servLong = getDecimalValueOfLocation(location.data[0].LONGITUDE)
                        getDistance(deviceLat, deviceLong)
                    }

                    is com.FTG2024.hrms.uidata.Response.Exception -> {
                        progressDialog.dismiss()
                        showToast(response.message.toString())
                    }

                    else -> {
                        progressDialog.dismiss()
                        return@Observer
                    }
                }

            }
        })
    }


    private fun getDecimalValueOfLocation(dms: String): Double {
        val parts = dms.split("[^\\d.]".toRegex())
            .filter { it.isNotBlank() }
            .map { it.toDouble() }
        Log.d("###", "getDecimalValueOfLocation: $dms")
        val degrees = parts[0]
        val minutes = parts[1]
        val seconds = parts[2]

        val decimalDegrees = degrees + minutes / 60.0 + seconds / 3600.0
        return decimalDegrees
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun extractData(dashResponse: DashBoardResponse) {
        Log.d("####", "extractData: ")
        //binding.imageViewProfileDashboard.setImageDrawable(drawable)
        binding.scrollviewDash.visibility = View.VISIBLE
        binding.viewDash.visibility = View.INVISIBLE
        if (dashResponse.attendenceData.isNullOrEmpty()) {
            binding.textviewDayinContainerInout.visibility = View.VISIBLE
            binding.textviewDayinContainerInout.setTextColor(resources.getColor(R.color.white))
            binding.textviewDayinContainerInout.setBackgroundResource(R.drawable.shape_dayin_dayout)
            binding.textviewDayoutContainerInout.visibility = View.INVISIBLE
        } else if (dashResponse.attendenceData.get(0).ATTENDENCE_STATUS == "S") {
            Log.d("####", "extractData: ")
            binding.textviewDayoutContainerInout.visibility = View.VISIBLE
            binding.textviewDayinContainerInout.visibility = View.VISIBLE
            binding.textviewDayinContainerInout.setTextColor(resources.getColor(R.color.orange))
            binding.textviewDayinContainerInout.text = dashResponse.attendenceData.get(0).DAYIN_TIME
            binding.textviewDayoutContainerInout.setBackgroundResource(R.drawable.shape_dayin_dayout)
            binding.textviewDayinContainerInout.setOnClickListener(null)
        } else if (dashResponse.attendenceData.get(0).ATTENDENCE_STATUS == "E") {
            binding.textviewDayoutContainerInout.visibility = View.VISIBLE
            binding.textviewDayinContainerInout.visibility = View.VISIBLE
            binding.textviewDayinContainerInout.setTextColor(resources.getColor(R.color.orange))
            binding.textviewDayoutContainerInout.setTextColor(resources.getColor(R.color.orange))
            binding.textviewDayinContainerInout.text = dashResponse.attendenceData.get(0).DAYIN_TIME
            binding.textviewDayoutContainerInout.text =
                dashResponse.attendenceData.get(0).DAYOUT_TIME
            binding.textviewDayoutContainerInout.setBackgroundResource(R.drawable.shape_dayin_dayout_both)
            binding.textviewDayinContainerInout.setBackgroundResource(R.drawable.shape_dayin_dayout_both)
            binding.textviewDayinContainerInout.setOnClickListener(null)
            binding.textviewDayoutContainerInout.setOnClickListener(null)
        } else {
            binding.textviewDayinContainerInout.visibility = View.VISIBLE
            binding.textviewDayinContainerInout.setTextColor(resources.getColor(R.color.white))
            binding.textviewDayinContainerInout.setBackgroundResource(R.drawable.shape_dayin_dayout)
            binding.textviewDayoutContainerInout.visibility = View.INVISIBLE
        }
    }

    private fun dayInButtonListener() {
        binding.textviewDayinContainerInout.setOnClickListener {
           /* if (isDevModeOn()) {
                showToast("Turn off Developer Options")
            } else {*/
                progressDialog = ProgressDialog(this, "Fetching location")
                progressDialog.show()
                isDayin = true
                getLocation()
            //}

        }

        binding.textviewDayoutContainerInout.setOnClickListener {
            Log.d("$$$$", "dayOutButtonListener: ")
           /* if (isDevModeOn()) {
                showToast("Turn off Developer Options")
            } else {*/
                progressDialog = ProgressDialog(this, "Fetching location")
                progressDialog.show()
                isDayin = false
                getLocation()
            //}
        }
    }

    private fun getLocation() {


        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            startActivity(Intent(this, NoPermissionActivity::class.java))
            return
        } else {

            Log.d("####", "getLocation: ${fusedLocationClient.locationAvailability.isSuccessful}")
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        deviceLat = location.latitude
                        deviceLong = location.longitude
                        Log.d("####", "getLocation: $deviceLat $deviceLong")
                        if (isRefreshClicked) {
                            getDistance(deviceLat, deviceLong)
                        } else {
                            Log.d(
                                "####",
                                "getLocation: ${getEmployeeData().get(0).UserData.get(0).EMP_ID}"
                            )
                            viewModel.getWorkLocation(
                                EmpIdRequest(getEmployeeData().get(0).UserData.get(0).EMP_ID),
                                tokenManager.getToken()!!
                            )
                        }
                    } else {
                        progressDialog.dismiss()
                        Log.d("####", "getLocation: else")
                    }
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Log.d("####", "getLocation: failed $e")
                }
        }
    }


 /*   private fun getLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            startActivity(Intent(this, NoPermissionActivity::class.java))
            return
        } else {

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }*/



    private fun getDistance(latitude: Double, longitude: Double) {
        val lat1 = Math.toRadians(latitude)
        val lon1 = Math.toRadians(longitude)

        val lat2 = Math.toRadians(servLat)
        val lon2 = Math.toRadians(servLong)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        val earthRadiusMeters = 6371000.0
        Log.d("$$$$", "getDistance: ${earthRadiusMeters * c}")
        requiredDistance = (earthRadiusMeters * c).toInt()
        Log.d("$$$$", "getDistance: $requiredDistance")
        isRemarkRequired = requiredDistance > 50
        showLocationFragment(isRemarkRequired)
        progressDialog.dismiss()
    }

    private fun showLocationFragment(isRemarkRequired: Boolean) {
        locationFragment = LocationBottomSheetFragment.newInstance(
            deviceLat,
            deviceLong,
            requiredDistance,
            isRemarkRequired,
            object : LocationBottomSheetFragment.OnLocationClickListener {
                override fun onRefreshClicked() {
                    if (isDevModeOn()) {
                        showToast("Turn off Developer Options")
                    } else {
                        requestNewLocation()
                    }
                }
                override fun onSubmitClicked(remark: String) {
                    navigateToMarkAttendance(remark)
                }
            })
        locationFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetTheme)
        locationFragment.show(supportFragmentManager, "Location Fragment")
    }

    private fun navigateToMarkAttendance(remark: String) {
        val intent = Intent(this, MarkAttendanceActivity::class.java)
        Log.d("####", "navigateToMarkAttendance: $isDayin")
        intent.putExtra("entrytype", isDayin)
        intent.putExtra("lat", deviceLat)
        intent.putExtra("long", deviceLong)
        intent.putExtra("distance", requiredDistance)
        intent.putExtra("remark", remark)
        startActivity(intent)
    }

    private fun requestNewLocation() {
        isRefreshClicked = true
        locationFragment.dismiss()
        progressDialog = ProgressDialog(this, "Refreshing Location")
        progressDialog.show()
        getLocation()
    }

    fun setReportingSharedPref() {
        val pref = getSharedPreferences("reporting_pref", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("IS_REPORTING", isReporting)
        editor.putInt("selection", position)
        editor.apply()
    }

    fun getReportingStatus(): Boolean {
        return getSharedPreferences("reporting_pref", Context.MODE_PRIVATE).getBoolean("IS_REPORTING", isReporting)
    }

    fun getPosition(): Int {
        return getSharedPreferences("reporting_pref", Context.MODE_PRIVATE).getInt("selection", position)
    }

    private fun getEmployeeData(): List<Data> {
        val sharedPref = getSharedPreferences("employee_detail_pref", Context.MODE_PRIVATE)
        val dataListJson = sharedPref.getString("employeeDataListKey", null)

        if (dataListJson != null) {
            val gson = Gson()
            val dataList: List<Data> =
                gson.fromJson(dataListJson, object : TypeToken<List<Data>>() {}.type)
            val user = dataList.get(0).UserData[0]
            if (user.ROLE_DETAILS.size == 1) {
                binding.textViewJobTypeDashboard.visibility = View.VISIBLE
                binding.spinnerDashboard.visibility = View.GONE
            } else {
                binding.textViewJobTypeDashboard.visibility = View.INVISIBLE
                val list = mutableListOf<String>()
                for (role in user.ROLE_DETAILS) {
                    list.add(role.ROLE_NAME)
                }
                val adapter = ArrayAdapter(this, R.layout.item_dashboard_spinner, list)
                adapter.setDropDownViewResource(R.layout.item_dashboard_spinner)
                binding.spinnerDashboard.adapter = adapter
                binding.spinnerDashboard.setSelection(getPosition())
            }

            empID = user.EMP_ID.toString()
            empName = user.NAME
            binding.textViewNameDashboard.text = empName
            binding.textViewJobTypeDashboard.text = user.ROLE_DETAILS.get(0).ROLE_NAME
            Log.d("###", "g.etEmployeeData: ${dataList.get(0).UserData.get(0).EMP_ID}")
            return dataList
        }
        return listOf()
    }

    private fun getEmployeeProfile() : com.FTG2024.hrms.profile.model.Data? {
        val sharedPref = getSharedPreferences("employee_profile_pref", Context.MODE_PRIVATE)
        val profileJson = sharedPref.getString("employeeProfileKey", null)

        if (profileJson != null) {
            val gson = Gson()
            val profile  = gson.fromJson<com.FTG2024.hrms.profile.model.Data>(profileJson, object : TypeToken<com.FTG2024.hrms.profile.model.Data>() {}.type)
            return profile
        }
        return null
    }
}