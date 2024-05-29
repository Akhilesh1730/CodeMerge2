package com.example.myapplication.addCustomer.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.FTG2024.hrms.application.TokenManager
import com.FTG2024.hrms.customers.model.Data
import com.FTG2024.hrms.customers.model.createcutomers
import com.FTG2024.hrms.retrofit.RetrofitHelper
import com.example.myapplication.addCustomer.model.GetID
import com.example.myapplication.addCustomer.repo.addCustomerApiservice
import com.google.android.gms.common.api.Response
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch


class CustomerViewModel : ViewModel() {


    private val _customers = MutableLiveData<List<Data>>()
    val customers: LiveData<List<Data>> get() = _customers


    private lateinit var application: Application

    fun init(application: Application) {
        this.application = application
    }


    suspend fun addCustomer(
        ID: Int,
        FIRST_NAME: String,
        LAST_NAME: String,
        MOBILE_NO: String,
        CREATED_EMP_ID: Int,
        STATE_ID: Int,
        IS_WORKING: Int,
        date: String,
        ADDRESS: String,
        description: String,
        CITY: String,
        DISTRICT: String,
        PINCODE: String,
        Email: String,

        onSuccess: () -> Unit,
        onFailure: () -> Unit,

        tokenManager: TokenManager,
    ) {
        viewModelScope.launch {
            try {
                val requestBody = createcutomers(
                    ID = ID,
                    ADDRESS = ADDRESS,
                    CITY = CITY,
                    CREATED_EMP_ID = CREATED_EMP_ID,
                    DESCRIPTION = description,
                    DISTRICT = DISTRICT,
                    EMAIL_ID = Email,
                    FIRST_NAME = FIRST_NAME,
                    LAST_NAME = LAST_NAME,
                    MOBILE_NO = MOBILE_NO,
                    PINCODE = PINCODE,
                    STATE_ID = STATE_ID,
                    IS_WORKING = IS_WORKING,

                    )

                val retrofit = RetrofitHelper.getRetrofitInstance(tokenManager)
                val response =
                    retrofit.create(addCustomerApiservice::class.java).PostCustomers(requestBody)
                if (response.isSuccessful) {


                    Log.d("CustomerViewModel", "Asset added successfully")

                    Log.d("CustomerViewModel", response.message())
                    Log.d("CustomerViewModel", response.body().toString())
                    Log.d("CustomerViewModel", response.code().toString())

                    if (response.code() == 200) {
                        onSuccess()
                    } else {

                        onFailure()
                    }


                } else {
                    Log.e("CustomerViewModel", "Failed to add asset")
                }
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Network Error: ${e.message}", e)
            }
        }
    }


    fun getCustomers(tokenManager: TokenManager, emp_id: Int) {
        viewModelScope.launch {
            try {
                val retrofit = RetrofitHelper.getRetrofitInstance(tokenManager)
                val response = retrofit.create(addCustomerApiservice::class.java)
                    .getCustomers(requestBody = GetID(emp_id))
                if (response.isSuccessful) {
                    val customerResponse = response.body()
                    customerResponse?.let {
                        _customers.value = it.data
                        Log.d("CustomerViewModel", "Customers fetched successfully")
                        Log.d("CustomerViewModel", response.raw().toString())
                        Log.d("CustomerViewModel", response.message())
                        Log.d("CustomerViewModel", response.body().toString())
                        Log.d("CustomerViewModel", response.code().toString())
                    }
                } else {
                    Log.e("CustomerViewModel", "Failed to fetch customers: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Network Error: ${e.message}", e)
            }
        }
    }


    private fun getEmployeeData(): List<com.FTG2024.hrms.login.model.Data> {
        val sharedPref =
            application.getSharedPreferences("employee_detail_pref", Context.MODE_PRIVATE)
        val dataListJson = sharedPref.getString("employeeDataListKey", null)

        if (dataListJson != null) {
            val gson = Gson()
            val dataList: List<com.FTG2024.hrms.login.model.Data> =
                gson.fromJson(
                    dataListJson,
                    object : TypeToken<List<com.FTG2024.hrms.login.model.Data>>() {}.type
                )
            Log.d("#####", "getEmployeeData: ${dataList[0].UserData.get(0).EMP_ID}")
            return dataList
        }
        return listOf()
    }
}