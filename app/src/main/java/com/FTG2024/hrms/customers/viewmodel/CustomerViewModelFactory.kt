//package com.example.myapplication.addCustomer.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewmodel.CreationExtras
//import com.FTG2024.hrms.customers.repo.CustomerRepository
//
//class CustomerViewModelFactory(private val repository: CustomerRepository) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(CustomerViewModel::class.java)) {
//            return CustomerViewModel() as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//
//}