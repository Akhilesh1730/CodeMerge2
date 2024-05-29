package com.FTG2024.hrms.customers.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.FTG2024.hrms.R
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.customers.AddCustomerActivity
import com.FTG2024.hrms.login.model.Data
import com.example.myapplication.addCustomer.viewmodel.CustomerViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class add_csutomer_dialog(context: Context, private val activity: AppCompatActivity) :
    Dialog(context) {

    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var addButton: Button
    private lateinit var cancleButton: Button
    private lateinit var cancleButtonTop: ImageView
    private lateinit var switchbButton: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_customer_dialog)

        window?.setBackgroundDrawableResource(R.drawable.custom_dialog_background)

        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        customerViewModel = ViewModelProvider(activity)[CustomerViewModel::class.java]
        addButton = findViewById(R.id.addCustomerButton)
        cancleButton = findViewById(R.id.cancleButton)
        cancleButtonTop = findViewById(R.id.cancel_buttontop)


        setupDialog()
    }
    private fun setupDialog() {
        cancleButton.setOnClickListener {
            dismiss()
        }

        cancleButtonTop.setOnClickListener {
            dismiss()
        }




        addButton.setOnClickListener {






            if (validateFields()) {
                val firstName = findViewById<EditText>(R.id.Firstname).text.toString().trim()
                val lastName = findViewById<EditText>(R.id.LastNameEditText).text.toString().trim()
                val address = findViewById<EditText>(R.id.AddressEditText).text.toString().trim()
                val city = findViewById<EditText>(R.id.CityEditText).text.toString().trim()
                val pincode = findViewById<EditText>(R.id.PincodeEditText).text.toString().trim()
                val district = findViewById<EditText>(R.id.DistrictEditText).text.toString().trim()
                val state = findViewById<EditText>(R.id.StateEditText).text.toString().trim()
                val mobileNumber = findViewById<EditText>(R.id.PhoneEditText).text.toString().trim()
                val email = findViewById<EditText>(R.id.editTextTextEmailAddress2).text.toString().trim()
                val description = findViewById<EditText>(R.id.descrEdittetView).text.toString().trim()
                val switchState = if (switchbButton.isChecked) 1 else 0
                val empid  =  getEmployeeData()[0].UserData[0].EMP_ID.toString()

                val todaysDate = getCurrentDate()

                // Launch coroutine to add customer data
                CoroutineScope(Dispatchers.IO).launch {
                    customerViewModel.addCustomer(
                        empid.toInt(),
                        firstName,
                        lastName,
                        mobileNumber,
                        empid.toInt(),   // Employee ID
                        9,
                        switchState,
                        todaysDate,
                        address,
                        description,
                        city,
                        district,
                        pincode,
                        email,
                        onSuccess = {
                            dismiss()

                            (activity as AddCustomerActivity).refreshPage()
                        },
                        onFailure = {

                            Toast.makeText(
                                context,
                                "Please after some time something went wrong ",
                                Toast.LENGTH_LONG
                            ).show()

                        },

                        TokenManagerImpl(context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE))
                    )
                }
            } else {
                showValidationErrors()
            }
        }
    }

    private fun showValidationErrors() {
        val firstNameEditText = findViewById<EditText>(R.id.Firstname)
        val lastNameEditText = findViewById<EditText>(R.id.LastNameEditText)
        val addressEditText = findViewById<EditText>(R.id.AddressEditText)
        val cityEditText = findViewById<EditText>(R.id.CityEditText)
        val pincodeEditText = findViewById<EditText>(R.id.PincodeEditText)
        val districtEditText = findViewById<EditText>(R.id.DistrictEditText)
        val stateEditText = findViewById<EditText>(R.id.StateEditText)
        val mobileNumberEditText = findViewById<EditText>(R.id.PhoneEditText)
        val emailEditText = findViewById<EditText>(R.id.editTextTextEmailAddress2)
        val descriptionEditText = findViewById<EditText>(R.id.descrEdittetView)

        if (firstNameEditText.text.toString().trim().isEmpty()) {
            firstNameEditText.error = "First name cannot be empty"
        }

        if (lastNameEditText.text.toString().trim().isEmpty()) {
            lastNameEditText.error = "Last name cannot be empty"
        }

        if (addressEditText.text.toString().trim().isEmpty()) {
            addressEditText.error = "Address cannot be empty"
        }

        if (cityEditText.text.toString().trim().isEmpty()) {
            cityEditText.error = "City cannot be empty"
        }

        if (pincodeEditText.text.toString().trim().isEmpty()) {
            pincodeEditText.error = "Pincode cannot be empty"
        }

        if (districtEditText.text.toString().trim().isEmpty()) {
            districtEditText.error = "District cannot be empty"
        }

        if (stateEditText.text.toString().trim().isEmpty()) {
            stateEditText.error = "State cannot be empty"
        }

        if (mobileNumberEditText.text.toString().trim().isEmpty()) {
            mobileNumberEditText.error = "Mobile number cannot be empty"
        }

        if (emailEditText.text.toString().trim().isEmpty()) {
            emailEditText.error = "Email cannot be empty"
        }

        if (descriptionEditText.text.toString().trim().isEmpty()) {
            descriptionEditText.error = "Description cannot be empty"
        }
    }




    private fun validateFields(): Boolean {
        val firstName = findViewById<EditText>(R.id.Firstname).text.toString().trim()
        val lastName = findViewById<EditText>(R.id.LastNameEditText).text.toString().trim()
        val address = findViewById<EditText>(R.id.AddressEditText).text.toString().trim()
        val city = findViewById<EditText>(R.id.CityEditText).text.toString().trim()
        val pincode = findViewById<EditText>(R.id.PincodeEditText).text.toString().trim()
        val district = findViewById<EditText>(R.id.DistrictEditText).text.toString().trim()
        val state = findViewById<EditText>(R.id.StateEditText).text.toString().trim()
        val mobileNumber = findViewById<EditText>(R.id.PhoneEditText).text.toString().trim()
        val email = findViewById<EditText>(R.id.editTextTextEmailAddress2).text.toString().trim()
        val description = findViewById<EditText>(R.id.descrEdittetView).text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() ||
            city.isEmpty() || pincode.isEmpty() || district.isEmpty() ||
            state.isEmpty() || mobileNumber.isEmpty() || email.isEmpty() ||
            description.isEmpty()) {
            Log.d("validation Check"  ,"false")
            return false
        }
        Log.d("validation Check"  ,"True")

        return true
    }


    private fun getCurrentDate(): String {
        val today = Date()
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(today)
    }


    private fun getEmployeeData() : List<Data> {
        val sharedPref = context.getSharedPreferences("employee_detail_pref", Context.MODE_PRIVATE)
        val dataListJson = sharedPref.getString("employeeDataListKey", null)

        if (dataListJson != null) {
            val gson = Gson()
            val dataList: List<Data> =
                gson.fromJson(dataListJson, object : TypeToken<List<Data>>() {}.type)
            Log.d("#####", "getEmployeeData: ${dataList[0].UserData.get(0).EMP_ID}")
            return dataList
        }
        return listOf()
    }

}
