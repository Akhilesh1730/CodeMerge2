package com.example.myapplication.addCustomer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.addCustomer.repo.addCustomerApiservice

class ServiceViewModelFactory(private val apiService: addCustomerApiservice) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if ((modelClass.isAssignableFrom(ServiceViewModel::class.java))) {
            return ServiceViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
    }
