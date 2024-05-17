package com.example.myapplication.addCustomer.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.FTG2024.hrms.application.TokenManager
import com.FTG2024.hrms.customers.model.Data
import com.FTG2024.hrms.customers.model.createcutomers
import com.FTG2024.hrms.customers.repo.addCustomerApiservice
import com.FTG2024.hrms.retrofit.RetrofitHelper
import kotlinx.coroutines.launch


class CustomerViewModel : ViewModel() {



    private val _customers = MutableLiveData<List<Data>>()
    val customers: LiveData<List<Data>> get() = _customers



    suspend fun addCustomer(
        ID: Int, FIRST_NAME: String, LAST_NAME: String, MOBILE_NO: String, CREATED_EMP_ID: Int, STATE_ID: Int, Status: Int, date: String, ADDRESS: String, description: String, CITY: String, DISTRICT: String, PINCODE: String, Email: String,
        tokenManager: TokenManager
        ) {
        viewModelScope.launch {
            try {
                val requestBody = createcutomers(
                    ID = ID,
                    ADDRESS = ADDRESS,
                    CITY = CITY,
                    CREATED_EMP_ID = CREATED_EMP_ID,
                    CREATED_MODIFIED_DATE = date,
                    DESCRIPTION = description,
                    DISTRICT = DISTRICT,
                    EMAIL_ID = Email,
                    FIRST_NAME = FIRST_NAME,
                    LAST_NAME = LAST_NAME,
                    MOBILE_NO = MOBILE_NO,
                    PINCODE = PINCODE,
                    STATE_ID = STATE_ID,
                    STATUS = Status
                )

                val retrofit = RetrofitHelper.getRetrofitInstance(tokenManager)
                val response = retrofit.create(addCustomerApiservice::class.java).PostCustomers(requestBody)
                if (response.isSuccessful) {
                    Log.d("CustomerViewModel", "Asset added successfully")
                    Log.d("CustomerViewModel",response.message() )
                    Log.d("CustomerViewModel",response.body().toString() )
                    Log.d("CustomerViewModel",response.code().toString() )
                } else {
                    Log.e("CustomerViewModel", "Failed to add asset")
                }
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Network Error: ${e.message}", e)
            }
        }
    }



    fun getCustomers(tokenManager: TokenManager) {
        viewModelScope.launch {
            try {
                val retrofit = RetrofitHelper.getRetrofitInstance(tokenManager)
                val response = retrofit.create(addCustomerApiservice::class.java).getCustomers()
                if (response.isSuccessful) {
                    val customerResponse = response.body()
                    customerResponse?.let {
                        _customers.value = it.data
                        Log.d("CustomerViewModel", "Customers fetched successfully")
                    }
                } else {
                    Log.e("CustomerViewModel", "Failed to fetch customers: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Network Error: ${e.message}", e)
            }
        }
    }


}