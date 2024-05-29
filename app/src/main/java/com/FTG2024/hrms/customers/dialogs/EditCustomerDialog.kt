package com.FTG2024.hrms.customers.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.FTG2024.hrms.R
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.customers.model.Data
import com.example.myapplication.addCustomer.viewmodel.EditCustomerViewModel
import com.example.myapplication.addCustomer.viewmodel.EditCustomerViewModelFactory

class EditCustomerDialog(
    private val context: Context,
    private val customer: Data,
    private val callback: EditCustomerDialogCallback
) {

    fun show() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_customer, null)
        val dialogBuilder = AlertDialog.Builder(context).setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.show()

        val tokenManager = TokenManagerImpl(
            context.getSharedPreferences(
                "user_prefs",
                AppCompatActivity.MODE_PRIVATE
            )
        )
        val viewModelFactory = EditCustomerViewModelFactory(tokenManager)
        val viewModel = ViewModelProvider(context as AppCompatActivity, viewModelFactory).get(
            EditCustomerViewModel::class.java
        )
        viewModel.setCustomer(customer)

        val editCustomerName = dialogView.findViewById<EditText>(R.id.Firstname)
        val editCustomersirname = dialogView.findViewById<EditText>(R.id.LastNameEditText)
        val editCustomerPhone = dialogView.findViewById<EditText>(R.id.PhoneEditText)
        val editCustomerAddress = dialogView.findViewById<EditText>(R.id.AddressEditText)
        val editCustomerCity = dialogView.findViewById<EditText>(R.id.CityEditText)
        val editCustomerPinCode = dialogView.findViewById<EditText>(R.id.PincodeEditText)
        val editCustomerDistrict = dialogView.findViewById<EditText>(R.id.DistrictEditText)
        val editCustomerState = dialogView.findViewById<EditText>(R.id.StateEditText)
        val editCustomerEmail = dialogView.findViewById<EditText>(R.id.EmailEditText)
        val editCustomerDscription = dialogView.findViewById<EditText>(R.id.descrEdittetView)
        val btnEdit = dialogView.findViewById<Button>(R.id.EditCustomerButton)
        val btnCancle = dialogView.findViewById<Button>(R.id.cancleButton)

        editCustomerName.setText(customer.FIRST_NAME)
        editCustomersirname.setText(customer.LAST_NAME)
        editCustomerCity.setText(customer.CITY)
        editCustomerDscription.setText(customer.DESCRIPTION.toString() ?: "")
        editCustomerPinCode.setText(customer.PINCODE)
        editCustomerPhone.setText(customer.MOBILE_NO)
        editCustomerAddress.setText(customer.ADDRESS)
        editCustomerDistrict.setText(customer.DISTRICT)
        editCustomerState.setText(customer.STATE_ID.toString())
        editCustomerEmail.setText(customer.EMAIL_ID)

        btnEdit.setOnClickListener {
            val updatedCustomer = customer.copy(
                FIRST_NAME = editCustomerName.text.toString(),
                LAST_NAME = editCustomersirname.text.toString(),
                CITY = editCustomerCity.text.toString(),
                DESCRIPTION = editCustomerDscription.text.toString(),
                PINCODE = editCustomerPinCode.text.toString(),
                MOBILE_NO = editCustomerPhone.text.toString(),
                ADDRESS = editCustomerAddress.text.toString(),
                DISTRICT = editCustomerDistrict.text.toString(),
                STATE_ID = editCustomerState.text.toString().toInt(),
                EMAIL_ID = editCustomerEmail.text.toString()
            )
            viewModel.updateCustomer(updatedCustomer)
        }

        btnCancle.setOnClickListener { dialog.dismiss() }

        viewModel.updateStatus.observe(context as AppCompatActivity) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "Customer updated successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                callback.onCustomerUpdated()
            } else {
                Toast.makeText(context, "Failed to update customer", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.error.observe(context as AppCompatActivity) { errorMsg ->
            Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
        }
    }
}


interface EditCustomerDialogCallback {
    fun onCustomerUpdated()
}
