package com.example.myapplication.addCustomer.viewmodel

import android.util.Log
import android.widget.Toast
import com.FTG2024.hrms.customers.ServiceInterest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.addCustomer.repo.addCustomerApiservice
import kotlinx.coroutines.launch
class ServiceViewModel(private val apiService: addCustomerApiservice) : ViewModel() {

    private val _serviceInterests = MutableLiveData<List<ServiceInterest>>()
    val serviceInterests: LiveData<List<ServiceInterest>> get() = _serviceInterests

    fun fetchServiceInterests(customerId: Int) {
        viewModelScope.launch {
            val response = apiService.getServiceInterests(mapOf("CUSTOMER_ID" to customerId))
            if (response.isSuccessful) {
                Log.d("ServiceInterest", "Fetching data for customer ID: $customerId")
                val serviceInterests = response.body()?.data ?: emptyList()
                _serviceInterests.postValue(serviceInterests)
            } else {
                Log.e("ServiceInterest", "Failed to fetch service interests: ${response.message()}")
                // Show error message to user
            }
        }
    }
}
