package com.FTG2024.hrms.customers.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.FTG2024.hrms.R
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.customers.ServiceInterest
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

class EditServiceInterestDialog(
    private val context: Context,
    private val serviceInterest: ServiceInterest,
) : Dialog(context) {

    private lateinit var serviceNameSpinner: Spinner
    private lateinit var apiService: addCustomerApiservice
    private lateinit var amountEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var statusSwitch: Switch
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private var selectedServiceId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_service_intrest)

        window?.setBackgroundDrawableResource(R.drawable.custom_dialog_background)
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        serviceNameSpinner = findViewById(R.id.dropdownCat)
        amountEditText = findViewById(R.id.AMOUNT)
        descriptionEditText = findViewById(R.id.descrEdittetView)
        statusSwitch = findViewById(R.id.cutomerStatus)
        saveButton = findViewById(R.id.editButton)
        cancelButton = findViewById(R.id.cancleButton)

        amountEditText.setText(serviceInterest.amount.toString())
        descriptionEditText.setText(serviceInterest.description.toString())
        statusSwitch.isChecked = serviceInterest.status != 0

        val tokenManager = TokenManagerImpl(
            context.getSharedPreferences(
                "user_prefs",
                AppCompatActivity.MODE_PRIVATE
            )
        )
        apiService = RetrofitHelper.getRetrofitInstance(tokenManager)
            .create(addCustomerApiservice::class.java)

        cancelButton.setOnClickListener { dismiss() }

        fetchServices()

        saveButton.setOnClickListener {
            val amount = amountEditText.text.toString().toDouble().toInt()
            val description = descriptionEditText.text.toString()
            val status = if (statusSwitch.isChecked) 1 else 0

            Log.d("EditServiceInterestDialog", "Amount: $amount")
            Log.d("EditServiceInterestDialog", "Description: $description")

            val updatedServiceInterest = ServiceInterestRequest(
                ID = serviceInterest.id!!.toInt(),
                CUSTOMER_ID = serviceInterest.customerId!!.toInt(),
                SERVICE_ID = selectedServiceId,
                DESCRIPTION = description,
                STATUS = status,
                AMOUNT = amount.toInt(),


                )

            Log.d("EditServiceInterestDialog", "Request: $updatedServiceInterest")

            updateServiceInterest(updatedServiceInterest)
        }
    }

    private fun populateSpinner(services: List<Service>) {
        val serviceNames = services.map { it.NAME }
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, serviceNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        serviceNameSpinner.adapter = adapter

        serviceNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedServiceId = services[position].ID
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val position = services.indexOfFirst { it.ID == serviceInterest.serviceId }
        if (position >= 0) {
            serviceNameSpinner.setSelection(position)
        }
    }

    private fun fetchServices() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<GetServiceResponse> = apiService.getServicedrop()
                if (response.isSuccessful) {
                    val services = response.body()?.data ?: emptyList()
                    Log.d("EditServiceInterestDialog", "Services fetched: ${services.size}")

                    withContext(Dispatchers.Main) {
                        populateSpinner(services)
                    }
                } else {
                    Log.e(
                        "EditServiceInterestDialog",
                        "Failed to fetch services: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("EditServiceInterestDialog", "Error fetching services", e)
            }
        }
    }

    private fun updateServiceInterest(serviceInterestRequest: ServiceInterestRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("EditServiceInterestDialog passing", "${serviceInterestRequest}")

                val response = apiService.updateServiceInterest(serviceInterestRequest)
                withContext(Dispatchers.Main) {
                    Log.d("EditServiceInterestDialog", "Request: $serviceInterestRequest")
                    if (response.isSuccessful) {

                        val intent = Intent("com.FTG2024.hrms.SERVICE_INTEREST_UPDATED")
                        context.sendBroadcast(intent)

                        Log.d(
                            "EditServiceInterestDialog",
                            "Update Successful  Body: ${response.body()}"
                        )
                        Log.d(
                            "EditServiceInterestDialog",
                            "Update Successful: Raw  ${response.raw()}"
                        )
                        Log.d("EditServiceInterestDialog", "Update Successful")
                        dismiss()
                    } else {
                        Log.e(
                            "EditServiceInterestDialog",
                            "Failed to update service interest: ${response.message()}"
                        )
                        Log.e(
                            "EditServiceInterestDialog",
                            "Error Body: ${response.errorBody()?.string()}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("EditServiceInterestDialog", "Error updating service interest", e)
            }
        }
    }
}
