package com.FTG2024.hrms.customers.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import com.FTG2024.hrms.R
import com.FTG2024.hrms.customers.ServiceInterest
import com.example.myapplication.addCustomer.repo.addCustomerApiservice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangeStatusDialog(
    private val context: Context,
    private val item: ServiceInterest
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(context)

        val dialogView = inflater.inflate(R.layout.dialog_change_status, null)
        setContentView(dialogView)

        window?.setBackgroundDrawableResource(R.drawable.custom_dialog_background)
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val cancelButton = dialogView.findViewById<Button>(R.id.button_cancel)
        val approvedButton = dialogView.findViewById<Button>(R.id.button_approved)
        val backButton = dialogView.findViewById<Button>(R.id.button_back)
        lateinit var apiService: addCustomerApiservice





        cancelButton.setOnClickListener {


            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = apiService.cancelServiceInterest(mapOf("ID" to item.id.toInt()))
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {

                            val intent = Intent("com.FTG2024.hrms.SERVICE_INTEREST_UPDATED")
                            context.sendBroadcast(intent)
                            Log.d("Cancle ", "Update Successful  Body: ${response.body()}")
                            Log.d("Cancle ","Update Successful: Raw  ${response.raw()}")
                            Log.d("Cancle", "Update Successful")
                            dismiss()
                        } else {
                            Log.e("Cancle", "Failed to update service interest: ${response.message()}")
                            Log.e("Cancle", "Error Body: ${response.errorBody()?.string()}"
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("EditServiceInterestDialog", "Error updating service interest", e)
                }
            }
            dismiss()
        }







        approvedButton.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = apiService.approvedServiceInterest(mapOf("ID" to item.id.toInt()))
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {

                            val intent = Intent("com.FTG2024.hrms.SERVICE_INTEREST_UPDATED")
                            context.sendBroadcast(intent)
                            Log.d("Cancle ", "Update Successful  Body: ${response.body()}")
                            Log.d("Cancle ","Update Successful: Raw  ${response.raw()}")
                            Log.d("Cancle", "Update Successful")
                            dismiss()
                        } else {
                            Log.e("Cancle", "Failed to update service interest: ${response.message()}")
                            Log.e("Cancle", "Error Body: ${response.errorBody()?.string()}"
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("EditServiceInterestDialog", "Error updating service interest", e)
                }
            }



            Log.d("ddd", item.id.toString())

            dismiss()

        }

        backButton.setOnClickListener {
            dismiss()
        }
    }
}
