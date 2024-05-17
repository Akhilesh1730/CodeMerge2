package com.FTG2024.hrms.assets

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.FTG2024.hrms.R
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.assets.models.Data
import com.FTG2024.hrms.assets.viewmodel.CategoryViewModel
import com.example.myapplication.models.Asset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CustomDialog
    (context: Context, private val activity: AppCompatActivity) : Dialog(context) {
    private lateinit var spinnerCat: Spinner
    private lateinit var spinnerAsset: Spinner
    private lateinit var cancelButton: Button
    private lateinit var addButton: Button
    private lateinit var selectDateTextView: TextView
    private lateinit var descriptionEditText: EditText
    private lateinit var categoryViewModel: CategoryViewModel
    private var selectedAssetId: Int = -1
    private var selectedCatId: Int = -5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialgue_box)

        spinnerCat = findViewById(R.id.dropdownCat)
        spinnerAsset = findViewById(R.id.dropdownAssets)
        cancelButton = findViewById(R.id.cancleButton)
        addButton = findViewById(R.id.addButton)
        selectDateTextView = findViewById(R.id.selectdate)
        descriptionEditText = findViewById(R.id.descrEdittetView)
        categoryViewModel = ViewModelProvider(activity)[CategoryViewModel::class.java]
        setupDialog()
    }

    override fun onStart() {
        super.onStart()
        fetchCategoryData()
        fetchAssetData()
        window?.setBackgroundDrawableResource(R.drawable.custom_dialog_background)
    }


    private fun setupDialog() {
        val currentDate = getCurrentDate()
        selectDateTextView.text = currentDate
        cancelButton.setOnClickListener {
            dismiss()
        }
        addButton.setOnClickListener {
            val description = descriptionEditText.text.toString().trim()
            val currentDate = getCurrentDate()

            if (description.isEmpty()) {
                descriptionEditText.error = "Description cannot be empty"
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    categoryViewModel.addAsset(
                        1,
                        selectedCatId,
                        selectedAssetId,
                        currentDate,
                        currentDate,
                        description,
                        TokenManagerImpl(context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE))
                    )
                }


            }
        }
        selectDateTextView.setOnClickListener {
            showDatePickerDialog(selectDateTextView)
        }
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun fetchCategoryData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val categoryData = categoryViewModel.getCategoryDataForSpinner(TokenManagerImpl(context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)))
                if (categoryData != null) {
                    withContext(Dispatchers.Main) {
                        populateCategorySpinner(categoryData)
                    }
                } else {
                    Log.e("CustomDialog", "Failed to fetch category data")
                }
            } catch (e: Exception) {
                Log.e("CustomDialog", "Network Error: ${e.message}", e)
            }
        }
    }

    private fun fetchAssetData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val AssetData = categoryViewModel.getAssetsDataForSpinner(TokenManagerImpl(context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)))
                if (AssetData != null) {
                    withContext(Dispatchers.Main) {
                        populateAssetSpinner(AssetData)
                    }
                } else {
                    Log.e("CustomDialog", "Failed to fetch category data")
                }
            } catch (e: Exception) {
                Log.e("CustomDialog", "Network Error: ${e.message}", e)
            }
        }
    }

    private fun populateCategorySpinner(categories: List<Data>) {
        val categoryNames = categories.map { it.NAME }
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAsset.adapter = adapter

        spinnerAsset.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedAssetId = categories[position].ID
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun populateAssetSpinner(assets: List<Asset>) {
        val assetNames = assets.map { it.ASSET_NAME }
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, assetNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCat.adapter = adapter

        spinnerCat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCatId = assets[position].ID
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun showDatePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = formatDate(dayOfMonth, monthOfYear, year)
                textView.text = selectedDate
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }


    private fun formatDate(day: Int, month: Int, year: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
