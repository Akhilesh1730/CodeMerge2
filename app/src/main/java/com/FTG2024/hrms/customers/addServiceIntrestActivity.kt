package com.FTG2024.hrms.customers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.FTG2024.hrms.R
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.customers.model.GetServiceResponse
import com.FTG2024.hrms.customers.model.Service
import com.FTG2024.hrms.customers.model.ServiceInterestRequest
import com.FTG2024.hrms.retrofit.RetrofitHelper
import com.example.myapplication.addCustomer.repo.addCustomerApiservice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit

class addServiceIntrestActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var apiService: addCustomerApiservice
    private lateinit var dropdownCat: Spinner
    private lateinit var status: Switch
    private var selectedServiceId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_service_intrest)

        val dropdownLabelCat: TextView = findViewById(R.id.dropdownLabelCat)
        dropdownCat = findViewById(R.id.dropdownCat)
        val amountEditText: EditText = findViewById(R.id.AMOUNT)
        val descrEditText: EditText = findViewById(R.id.descrEdittetView)
        val cancelButton: Button = findViewById(R.id.cancleButton)
        val addButton: Button = findViewById(R.id.addButton)
        status = findViewById(R.id.cutomerStatus)
        cancelButton.setOnClickListener {
            finish()
        }

        retrofit = RetrofitHelper.getRetrofitInstance(
            TokenManagerImpl(
                getSharedPreferences("user_prefs", MODE_PRIVATE)
            )
        )
        apiService = retrofit.create(addCustomerApiservice::class.java)

        val customerId = intent.getIntExtra("customerId", -1)
        Log.d("AddServiceInterestActivity", "Received customerId: $customerId")

        if (customerId == -1) {
            Toast.makeText(this, "Something Went Wrong Please Try again Later!", Toast.LENGTH_LONG)
                .show()
            Log.e("AddServiceInterestActivity", "No customer ID provided")
        }

        fetchServices()

        addButton.setOnClickListener {
            val amount = amountEditText.text.toString()
            val description = descrEditText.text.toString()

            if (validateInput(amount, description)) {
                val statusValue = if (status.isChecked) 1 else 0
                val request = ServiceInterestRequest(
                    7777, customerId, selectedServiceId, description, statusValue,
                    amount.toInt()

                )

                Log.d("request is", request.toString())

                addCustomerInterest(request)
            }
        }
    }

    private fun validateInput(amount: String, description: String): Boolean {
        if (amount.isBlank()) {
            Toast.makeText(this, "Amount cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        val amountValue = amount.toIntOrNull()
        if (amountValue == null || amountValue <= 0) {
            Toast.makeText(this, "Amount must be a positive number", Toast.LENGTH_SHORT).show()
            return false
        }
        if (description.isBlank()) {
            Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun addCustomerInterest(request: ServiceInterestRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<ServiceInterestRequest> =
                    apiService.createServiceInterests(request)

                Log.e("AddServiceInterestActivity code ", " ${response.code()}")

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Log.e("AddServiceInterestActivity code ", " ${response.code()}")
                        Log.e("AddServiceInterestActivity raw", " ${response.raw()}")
                        Log.e("AddServiceInterestActivity message", " ${response.message()}")
                        Log.e("AddServiceInterestActivity body", " ${response.body()}")
                        Toast.makeText(
                            this@addServiceIntrestActivity,
                            "Service interest added successfully!",
                            Toast.LENGTH_LONG
                        ).show()



                        finish()

                        val intent = Intent("com.FTG2024.hrms.SERVICE_INTEREST_UPDATED")
                        sendBroadcast(intent)
                    }
                } else {
                    Log.e(
                        "AddServiceInterestActivity",
                        "Failed to add service interest: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("AddServiceInterestActivity", "Error adding service interest", e)
            }
        }
    }

    private fun fetchServices() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<GetServiceResponse> = apiService.getServicedrop()
                if (response.isSuccessful) {
                    val services = response.body()?.data ?: emptyList()
                    withContext(Dispatchers.Main) {
                        populateSpinner(services)
                    }
                } else {
                    Log.e(
                        "AddServiceInterestActivity",
                        "Failed to fetch services: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("AddServiceInterestActivity", "Error fetching services", e)
            }
        }
    }

    private fun populateSpinner(services: List<Service>) {
        val serviceNames = services.map { it.NAME }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, serviceNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dropdownCat.adapter = adapter

        dropdownCat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedServiceId = services[position].ID
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }
}
