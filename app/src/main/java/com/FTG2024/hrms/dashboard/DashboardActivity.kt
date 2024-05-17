package com.FTG2024.hrms.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
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
import com.FTG2024.hrms.login.model.Data
import com.FTG2024.hrms.markattendance.MarkAttendanceActivity
import com.FTG2024.hrms.retrofit.RetrofitHelper
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class DashboardActivity : BaseActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tokenManager = TokenManagerImpl(getSharedPreferences("user_prefs", MODE_PRIVATE))
        retrofitHelper = RetrofitHelper.getRetrofitInstance(tokenManager)
        viewModel = ViewModelProvider(
            this,
            DashBoardViewModelFactory(DashBoardRepo(retrofitHelper.create(DashboardApiService::class.java)))
        )
            .get(DashBoardViewModel::class.java)
        setCardsListeners()
        dayInButtonListener()
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
                        startActivity(Intent(this, NoPermissionActivity::class.java))
                    }
                }
            })
        } else {
            setUp()
        }
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
    }

    private fun setUp() {
        progressDialog = getProgressDialog("Fetching Data")
        progressDialog.show()
        viewModel.getDashBoardData(EmpIdRequest(empID.toInt()))
    }

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

        val degrees = parts[0]
        val minutes = parts[1]
        val seconds = parts[2]

        val decimalDegrees = degrees + minutes / 60.0 + seconds / 3600.0
        return String.format("%.2f", decimalDegrees).toDouble()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun extractData(dashResponse: DashBoardResponse) {
        Log.d("####", "extractData: ")
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
            //binding.textviewDayinContainerInout.setOnClickListener(null)
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
            //binding.textviewDayinContainerInout.setOnClickListener(null)
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
            Log.d("###", "dayInButtonListener: ")
            progressDialog = ProgressDialog(this, "Fetching location")
            progressDialog.show()
            isDayin = true
            getLocation()
            /* if (isDevModeOn()) {
               showToast("Please Turn off developer mode")
           } else {

           }*/
        }

        binding.textviewDayoutContainerInout.setOnClickListener {
            Log.d("$$$$", "dayOutButtonListener: ")
            progressDialog = ProgressDialog(this, "Fetching location")
            progressDialog.show()
            isDayin = false
            getLocation()
            /* if (isDevModeOn()) {
                 showToast("Please Turn off developer mode")
             } else {

             }*/
        }
    }

    private fun getLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
        requiredDistance = (earthRadiusMeters * c).toInt()

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
                    requestNewLocation()
                }

                override fun onSubmitClicked(remark: String) {
                    navigateToMarkAttendance(remark)
                }
            })
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

    private fun getEmployeeData(): List<Data> {
        val sharedPref = getSharedPreferences("employee_detail_pref", Context.MODE_PRIVATE)
        val dataListJson = sharedPref.getString("employeeDataListKey", null)

        if (dataListJson != null) {
            val gson = Gson()
            val dataList: List<Data> =
                gson.fromJson(dataListJson, object : TypeToken<List<Data>>() {}.type)
            val user = dataList.get(0).UserData[0]
            empID = user.EMP_ID.toString()
            empName = user.NAME
            binding.textViewNameDashboard.text = empName

            Log.d("###", "g.etEmployeeData: ${dataList.get(0).UserData.get(0).EMP_ID}")
            return dataList
        }
        return listOf()
    }

    /*fun startLocationUpdates() {
        val constraint = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val workRequest = PeriodicWorkRequest
            .Builder(BackgroundLocationWorker ::class.java, 15, TimeUnit.MINUTES)
            .setConstraints(constraint)
            .build()
        WorkManager.getInstance(this).enqueue(workRequest)
        *//* val repeatingRequest = PeriodicWorkRequestBuilder<BackgroundLocationWorker>(1, TimeUnit.MINUTES)
             .build()

         WorkManager.getInstance(this).enqueueUniquePeriodicWork(
             "location_worker",
             ExistingPeriodicWorkPolicy.KEEP,
             repeatingRequest
         )*//*
    }*/

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}