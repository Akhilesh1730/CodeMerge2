package com.FTG2024.hrms.target

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.FTG2024.hrms.R
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.login.model.Data
import com.FTG2024.hrms.retrofit.RetrofitHelper
import com.FTG2024.hrms.target.model.targetbodyModel
import com.FTG2024.hrms.target.repo.targetApiService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import java.time.LocalDate

class targets : AppCompatActivity() {
    private lateinit var empid: String
    private lateinit var tokenManager: TokenManagerImpl
    private lateinit var retrofit: Retrofit
    private lateinit var targetApiService: targetApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target)

        // Initialize TokenManagerImpl within onCreate
        tokenManager = TokenManagerImpl(getSharedPreferences("user_prefs", MODE_PRIVATE))

        // Initialize retrofit and targetApiService
        retrofit = RetrofitHelper.getRetrofitInstance(tokenManager)
        targetApiService = retrofit.create(targetApiService::class.java)

        // Initialize empid within onCreate
        val employeeData = getEmployeeData()
        if (employeeData != null && employeeData.isNotEmpty()) {
            empid = employeeData[0].UserData[0].EMP_ID.toString()
        } else {
            // Handle the case when employee data is null or empty
            Log.e("Error", "Employee data is null or empty")
            return
        }

        // Call fetchTargetData after initializing all required properties
        fetchTargetData()
    }

    private fun fetchTargetData() {
        val currentDate = LocalDate.now()
        val firstDayOfMonth = currentDate.withDayOfMonth(1)

        val fromDate = firstDayOfMonth.toString()
        val toDate = currentDate.toString()
        val requestBody = targetbodyModel(
            EMP_ID = listOf(empid.toInt()),
            FROM_DATE = fromDate,
            TO_DATE = toDate
        )

        Log.d("Log", requestBody.toString())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = targetApiService.getTarget(requestBody)
                if (response.isSuccessful && response.body()?.data != null) {
                    withContext(Dispatchers.Main) {
                        updateUI(response.body()!!.data)
                    }
                } else {
                    // Handle error
                    Log.e("Error", "Failed to get data: ${response.message()}")
                }
            } catch (e: Exception) {
                // Handle exception
                Log.e("Exception", "Error occurred: ${e.message}")
            }
        }
    }

    private fun getEmployeeData(): List<com.FTG2024.hrms.login.model.Data>? {
        val sharedPref = getSharedPreferences("employee_detail_pref", Context.MODE_PRIVATE)
        val dataListJson = sharedPref.getString("employeeDataListKey", null)

        return if (dataListJson != null) {
            val gson = Gson()
            val dataList: List<com.FTG2024.hrms.login.model.Data> =
                gson.fromJson(dataListJson, object : TypeToken<List<Data>>() {}.type)
            Log.d("#####", "getEmployeeData: ${dataList[0].UserData[0].EMP_ID}")
            dataList
        } else {
            null
        }
    }

    private fun updateUI(data: List<com.FTG2024.hrms.target.model.Data>) {
        if (data.isNotEmpty()) {
            findViewById<TextView>(R.id.emp_id_value).text = data[0].EMP_ID.toString()
            findViewById<TextView>(R.id.employee_name_value).text = data[0].EMPLOYEE_NAME
            findViewById<TextView>(R.id.total_amount_value).text = data[0].TOTAL_AMOUNT.toString()
            findViewById<TextView>(R.id.total_customer_created_value).text =
                data[0].TOTAL_CUSTOMER_CREATED.toString()
        }
    }
}
