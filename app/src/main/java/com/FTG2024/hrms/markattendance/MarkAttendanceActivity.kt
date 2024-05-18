package com.FTG2024.hrms.markattendance

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.Operation
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await

import com.FTG2024.hrms.application.HRMApp
import com.FTG2024.hrms.application.TokenManager
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.background.BackgroundLocationWorker
import com.FTG2024.hrms.background.HourLocationRequest
import com.FTG2024.hrms.background.NetStatusWorker
import com.FTG2024.hrms.base.BaseActivity
import com.FTG2024.hrms.base.LocationResponse
import com.FTG2024.hrms.dashboard.DashboardActivity
import com.FTG2024.hrms.databinding.ActivityMarkAttendenceBinding
import com.FTG2024.hrms.dialog.ProgressDialog
import com.FTG2024.hrms.login.model.Data
import com.FTG2024.hrms.markattendance.markattendancerepo.MarkAttendanceApiService
import com.FTG2024.hrms.markattendance.markattendancerepo.MarkAttendanceRepo
import com.FTG2024.hrms.markattendance.model.DayInRequest
import com.FTG2024.hrms.markattendance.model.DayOutRequest
import com.FTG2024.hrms.markattendance.model.MarkResponse
import com.FTG2024.hrms.markattendance.viewmodel.MarkAttendanceViewModel
import com.FTG2024.hrms.markattendance.viewmodel.MarkAttendanceViewModelFactory
import com.FTG2024.hrms.retrofit.RetrofitHelper
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

class MarkAttendanceActivity : BaseActivity() {
    private lateinit var binding: ActivityMarkAttendenceBinding
    private lateinit var selfieImageUri : Uri
    private lateinit var imgUri : Uri
    private lateinit var selfieImageView: ImageView
    private lateinit var buttonUpload : Button
    private lateinit var buttonMarkEntry : Button
    private lateinit var tokenId : String
    private lateinit var empid : String
    private lateinit var serverImageName : String
    private lateinit var serverCoordinates : String
    private lateinit var remark : String
    private var isDayIn : Boolean = true
    private var servLat : Double = 0.0
    private var servLong : Double = 0.0
    private var deviceLat : Double = 0.0
    private var deviceLong : Double = 0.0
    private var requiredDistance : Int = 0
    private var isRemarkRequired : Boolean = false
    private lateinit var tokenManager: TokenManager
    //private var deviceID : String = getDeviceID()
    private lateinit var retrofitClient : Retrofit
    private lateinit var apiService : MarkAttendanceApiService
    private lateinit var progressDialog : ProgressDialog
    private lateinit var viewModel : MarkAttendanceViewModel
    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        hideCameraButtonAndTakeSelfieText()
        showRetakeUploadButton()
        binding.imageViewMarkattendanceSelfie.setImageURI(null)
        binding.imageViewMarkattendanceSelfie.setImageURI(selfieImageUri)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarkAttendenceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //(application as HRMApp).startLocationUpdates()
        tokenManager = TokenManagerImpl(getSharedPreferences("user_prefs", MODE_PRIVATE))
        retrofitClient = RetrofitHelper.getRetrofitInstance(tokenManager)
        apiService = retrofitClient.create(MarkAttendanceApiService::class.java)
        viewModel = ViewModelProvider(this, MarkAttendanceViewModelFactory(MarkAttendanceRepo(apiService)))[MarkAttendanceViewModel::class.java]
        tokenId = ""
        checkBundle()
        init()
        setSelfieClickListener()
        setRetakeUpLoadClickListener()
        setMarkEntryClickListener()
        setObservers()
        getEmpID()
        setServerImageUrlName()
    }

    private fun setServerImageUrlName() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Month is 0-based, so add 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val empIdInt = empid.toIntOrNull()
        serverImageName = String.format("%d%02d%02d_%d", year, month, day, empIdInt)
    }

    private fun getEmpID() {
        empid = getEmployeeData()[0].UserData[0].EMP_ID.toString()
    }

    private fun checkBundle() {
        val bundle = intent.extras
        val entryType: String? = bundle?.getString("entrytype")
        deviceLat = bundle!!.getDouble("lat")
        deviceLong = bundle!!.getDouble("long")
        serverCoordinates = decimalToDMS(deviceLat, true) + decimalToDMS(deviceLong, true)
        remark = bundle.getString("remark")!!
        requiredDistance = bundle.getInt("distance")
        isDayIn = bundle.getBoolean("entrytype")
    }

    private fun setObservers() {
        viewModel.getMarkInEntryLiveData().observe(this, Observer { event ->
            event.getContentIfNotHandled().let { response ->
                Log.d("####", "setObservers: $response")

                when(response) {
                    is com.FTG2024.hrms.uidata.Response.Success -> {
                        val dayInResponse = response.data as MarkResponse
                        if (dayInResponse.code == 200) {
                            //(application as HRMApp).startLocationUpdates()
                            startLocationUpdates()
                            showToast("Entry Recorded Successfully")
                        } else {
                            showToast(dayInResponse.message)
                            navigateToDashBoard()
                        }
                    }

                    is com.FTG2024.hrms.uidata.Response.Exception -> {
                        showToast("Failed to mark attendance")
                        navigateToDashBoard()
                    }

                    else -> return@Observer
                }
            }
        })

        viewModel.getMarkOutEntryLiveData().observe(this, Observer { event ->
            event.getContentIfNotHandled().let { response ->
                when(response) {
                    is com.FTG2024.hrms.uidata.Response.Success -> {
                        val dayOutResponse = response.data as MarkResponse
                        if (dayOutResponse.code == 200) {
                            showToast("Entry Recorded Successfully")
                        } else {
                            showToast(dayOutResponse.message)
                        }
                        stopLocationUpdates()
                        stopNetUpdates()
                        navigateToDashBoard()
                    }
                    is com.FTG2024.hrms.uidata.Response.Exception -> {
                        progressDialog.dismiss()
                        showToast("Failed to mark attendance")
                        navigateToDashBoard()
                }
                    else -> return@Observer
                }
            }
        })

        viewModel.getLocationLiveData().observe(this, Observer { event ->
            event.getContentIfNotHandled().let { response ->
                when(response) {
                    is com.FTG2024.hrms.uidata.Response.Success -> {
                        val location = response.data as LocationResponse
                        servLat = getDecimalValueOfLocation(location.data[0].LATITUDE)
                        servLong = getDecimalValueOfLocation(location.data[0].LONGITUDE)
                        progressDialog.dismiss()
                    }
                    is  com.FTG2024.hrms.uidata.Response.Exception -> {
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

        viewModel.getSelfieUploadedLiveData().observe(this, Observer {
            it.getContentIfNotHandled().let {response ->
                when(response) {
                    is com.FTG2024.hrms.uidata.Response.Success -> {
                        progressDialog.dismiss()
                        hideRetakeUploadButton()
                        showInfoLabelAndMarkEntry()
                    }
                    is com.FTG2024.hrms.uidata.Response.Exception -> {
                        progressDialog.dismiss()
                        showToast(response.message.toString())
                        navigateToDashBoard()
                    }
                    else -> return@Observer
                }
            }
        })
    }

    private fun navigateToDashBoard() {
        val intent = Intent(this, DashboardActivity :: class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
    private fun getDecimalValueOfLocation(dms : String): Double {
        val parts = dms.split("[^\\d.]".toRegex())
            .filter { it.isNotBlank() }
            .map { it.toDouble() }

        val degrees = parts[0]
        val minutes = parts[1]
        val seconds = parts[2]

        val decimalDegrees = degrees + minutes / 60.0 + seconds / 3600.0
        return String.format("%.2f", decimalDegrees).toDouble()
    }
    private fun init() {
        selfieImageView = binding.imageViewSelfie
        buttonUpload = binding.buttonMarkAttendanceUpload
        buttonMarkEntry = binding.buttonMarkAttendanceMarkEntry
        selfieImageUri = createImageUri()
    }
    private fun setSelfieClickListener() {
        binding.imageViewSelfie.setOnClickListener {
            if (isNetworkEnabled(this) && isGpsEnabled(this)) {
                contract.launch(selfieImageUri)
            } else if (!isNetworkEnabled(this)) {
                showToast("Check Your Connectivity")
            } else if(!isGpsEnabled(this)) {
                showToast("Please Turn On Your GPS")
            }
        }
    }

    private fun setRetakeUpLoadClickListener() {
        buttonUpload.setOnClickListener {
            progressDialog = ProgressDialog(this, "Uploading Image")
            progressDialog.show()
            uploadImage()
        }
    }

    private fun setMarkEntryClickListener() {
        buttonMarkEntry.setOnClickListener {
            progressDialog = ProgressDialog(this, "Marking Entry")
            progressDialog.show()
            postAttendanceDetail()
            Log.d("@@@@", "setMarkEntryClickListener: $requiredDistance, $serverCoordinates $remark")
        }
    }

    private fun uploadImage() {
        val filesDir = applicationContext.filesDir
        val file = File(filesDir, "$serverImageName.jpg")
        val inputStream = contentResolver.openInputStream(selfieImageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        var outputStream: FileOutputStream
        var quality = 100
        var fileSize: Long

        do {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()
            fileSize = file.length()
            quality -= 5
        } while (fileSize > 400 * 1024 && quality > 5)

        if (fileSize < 200 * 1024) {
            // If the image is too small, increase quality
            do {
                quality += 5
                outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                outputStream.flush()
                outputStream.close()
                fileSize = file.length()
            } while (fileSize < 200 * 1024 && quality < 100)
        }

        val fileNameWithExtension = "$serverImageName.jpg"
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val body = MultipartBody.Part.createFormData("Image", fileNameWithExtension, requestFile)

        Log.d("####", "uploadImage: File size after compression: ${file.length() / 1024} KB")
        if (isDayIn) {
            viewModel.uploadDayInSelfie(body)
        } else {
            viewModel.uploadDayOutSelfie(body)
        }
    }


    private fun postAttendanceDetail() {
        if (isDayIn) {
            val request = DayInRequest(empid.toInt(), serverCoordinates, requiredDistance.toString() + "M",  getDeviceID(), "$serverImageName.jpg",remark)
            Log.d("###", "postAttendanceDetail: $tokenId")
            viewModel.markDayInEntry(request, tokenId)
            progressDialog = ProgressDialog(this, "Marking Attendance")
            progressDialog.show()
        } else {
            val request = DayOutRequest(empid.toInt(), serverCoordinates, requiredDistance.toString() + "M",  getDeviceID(), "$serverImageName.jpg",remark)
            viewModel.markDayOutEntry(request, tokenId)
            progressDialog = ProgressDialog(this, "Marking Attendance")
            progressDialog.show()
        }

    }

    private fun getResizedBitmap(uri: Uri): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true // Only get image dimensions without loading full bitmap

        try {
            BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var inSampleSize = 1
        val originalWidth = options.outWidth
        val originalHeight = options.outHeight

        // Calculate approximate max allowed dimensions based on target size range (200KB-400KB)
        val maxBytes = 400 * 1024 // Assuming upper limit of target range (adjust as needed)
        val maxAllowedWidth = Math.sqrt(maxBytes.toDouble() * (originalWidth / originalHeight)).toInt()
        val maxAllowedHeight = Math.sqrt(maxBytes.toDouble() / (originalWidth / originalHeight)).toInt()

        // Determine the inSampleSize that scales the image while staying within allowed dimensions
        while (originalWidth / inSampleSize > maxAllowedWidth || originalHeight / inSampleSize > maxAllowedHeight) {
            inSampleSize *= 2
        }

        options.inJustDecodeBounds = false // Decode full bitmap with calculated inSampleSize
        options.inSampleSize = inSampleSize

        return BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options)
    }
    private fun getLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Log.d("####", "getLocation: 1")
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            val latitude = location.latitude
                            val longitude = location.longitude
                            Log.d("####", "getLocation: $latitude $longitude")
                            serverCoordinates = decimalToDMS(latitude, true) + decimalToDMS(longitude, false)
                            progressDialog.dismiss()
                            getDistance(latitude, longitude)
                        } else {
                            Log.d("####", "getLocation: else")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.d("####", "getLocation: failed")
                    }
        Log.d("####", "getLocation: 3")
    }

    private fun getDistance(latitude : Double, longitude : Double) {
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

    private fun getDeviceID() : String {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }
    private fun createImageUri(): Uri {
        val image = File(applicationContext.filesDir, "selfie1.png")
        return FileProvider.getUriForFile(applicationContext,
            "com.emsapp.selfie.fileprovider",
            image)
    }

    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "emp_1_10_May_2024_selfie"
        return File(applicationContext.filesDir, "${imageFileName}.jpg")
    }

    private fun hideCameraButtonAndTakeSelfieText() {
        binding.labelMarkAttendanceTakeSelfie.visibility = View.GONE
        selfieImageView.visibility = View.GONE
    }

    private fun showRetakeUploadButton() {
        buttonUpload.visibility = View.VISIBLE
    }

    private fun hideRetakeUploadButton() {
        buttonUpload.visibility = View.GONE
    }

    private fun showInfoLabelAndMarkEntry() {
        //binding.labelMarkAttendanceMarkConfirmation.visibility = View.VISIBLE
        buttonMarkEntry.visibility = View.VISIBLE
    }


        private fun getEmployeeData() : List<Data> {
            val sharedPref = getSharedPreferences("employee_detail_pref", Context.MODE_PRIVATE)
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

     private fun startLocationUpdates() {
         val workRequest =
             PeriodicWorkRequestBuilder<BackgroundLocationWorker>(1, TimeUnit.HOURS)
                 .build()
         val workManager = WorkManager.getInstance(this)
         workManager.getWorkInfoByIdLiveData(workRequest.id)
             .observe(this) { workInfo ->
                 Log.d("####", "startLocationUpdates: ")
                 if(workInfo?.state == WorkInfo.State.ENQUEUED) {
                     startNetStatus()
                 }
             }
         workManager.enqueueUniquePeriodicWork("startLocID", ExistingPeriodicWorkPolicy.KEEP,
                 workRequest)
    }

    private fun stopLocationUpdates() {
        WorkManager.getInstance(applicationContext).cancelUniqueWork("startLocID")
    }

    private fun startNetStatus() {
        val workNetRequest =
            PeriodicWorkRequestBuilder<NetStatusWorker>(35, TimeUnit.MINUTES)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
        val workManager =  WorkManager.getInstance(applicationContext)
        workManager.getWorkInfoByIdLiveData(workNetRequest.id)
            .observe(this) { workInfo ->
                Log.d("####", "startLocationUpdates: ")
                if(workInfo?.state == WorkInfo.State.ENQUEUED) {
                    navigateToDashBoard()
                }
            }
        workManager.enqueueUniquePeriodicWork("startNetID", ExistingPeriodicWorkPolicy.KEEP, workNetRequest)
    }

    private fun stopNetUpdates() {
        WorkManager.getInstance(applicationContext).cancelUniqueWork("startNetID")
    }

}