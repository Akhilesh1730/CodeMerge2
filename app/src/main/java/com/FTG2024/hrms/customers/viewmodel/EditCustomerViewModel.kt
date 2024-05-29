package com.example.myapplication.addCustomer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.FTG2024.hrms.application.TokenManager
import com.FTG2024.hrms.customers.model.Data
import com.FTG2024.hrms.retrofit.RetrofitHelper
import com.example.myapplication.addCustomer.repo.addCustomerApiservice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditCustomerViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val _customer = MutableLiveData<Data>()
    val customer: LiveData<Data> get() = _customer

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> get() = _updateStatus

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun setCustomer(customer: Data) {
        _customer.value = customer
    }

    fun updateCustomer(customer: Data) {
        val retrofit = RetrofitHelper.getRetrofitInstance(tokenManager)
        val service = retrofit.create(addCustomerApiservice::class.java)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.updateCustomer(customer)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _updateStatus.value = true
                    } else {
                        _updateStatus.value = false
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _error.value = e.message
                }
            }
        }
    }
}
