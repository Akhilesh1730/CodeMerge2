package com.FTG2024.hrms.customers.repo

import com.FTG2024.hrms.customers.model.customer_model
import com.FTG2024.hrms.customers.repo.addCustomerApiservice
import retrofit2.Response

class CustomerRepository(private val apiService: addCustomerApiservice) {
    suspend fun getCustomers(): Response<customer_model> {
        return apiService.getCustomers()
    }
}
