package com.FTG2024.hrms.customers

import CustomerAdapter
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.FTG2024.hrms.R
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.customers.dialogs.EditCustomerDialogCallback
import com.FTG2024.hrms.login.model.Data
import com.FTG2024.hrms.customers.dialogs.add_csutomer_dialog
import com.example.myapplication.addCustomer.viewmodel.CustomerViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddCustomerActivity : AppCompatActivity(), EditCustomerDialogCallback {

    private lateinit var viewModel: CustomerViewModel
    private lateinit var adapter: CustomerAdapter
    private lateinit var fabButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private val filteredDates = mutableListOf<String>()
    private lateinit var empid: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_customer)

        val toolbar: Toolbar = findViewById(R.id.toolbar_customer)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        empid = getEmployeeData()[0].UserData[0].EMP_ID.toString()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
            }
            insets
        }

        viewModel = ViewModelProvider(this).get(CustomerViewModel::class.java)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = CustomerAdapter(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.getCustomers(
            TokenManagerImpl(getSharedPreferences("user_prefs", MODE_PRIVATE)),
            emp_id = empid.toInt()
        )
        viewModel.customers.observe(this, { customers ->
            adapter.submitList(customers)
        })

        fabButton = findViewById(R.id.fab)
        fabButton.setOnClickListener {
            val customDialog =
                add_csutomer_dialog(this@AddCustomerActivity, this@AddCustomerActivity)
            customDialog.show()
        }

        viewModel.customers.observe(this, { customers ->
            adapter.submitList(customers)
            Log.d(
                "AddCustomerActivity",
                "Customer list updated in adapter: ${customers.size} items"
            )
        })
    }

    override fun onCustomerUpdated() {
        refreshPage()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_customer_toolbar, menu)
        return true
    }

    private fun filterRecyclerView() {
        (recyclerView.adapter as? CustomerAdapter)?.submitList(viewModel.customers.value?.filter { customer ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val parsedDate = if (!customer.CREATED_MODIFIED_DATE.isNullOrEmpty()) {
                dateFormat.parse(customer.CREATED_MODIFIED_DATE)
            } else {
                null
            }
            val formattedDate = if (parsedDate != null) {
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(parsedDate)
            } else {
                ""
            }
            filteredDates.contains(formattedDate)
        })
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                // Do something with the selected date
                val selectedDate = "$dayOfMonth-${monthOfYear + 1}-$year"
                Log.d("Selected Date", selectedDate)
                filteredDates.add(selectedDate)
                filterRecyclerView()
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                Log.d("Got Press ", "HII")
                showDatePickerDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getEmployeeData(): List<Data> {
        val sharedPref = getSharedPreferences("employee_detail_pref", Context.MODE_PRIVATE)
        val dataListJson = sharedPref.getString("employeeDataListKey", null)

        return if (dataListJson != null) {
            val gson = Gson()
            val dataList: List<Data> =
                gson.fromJson(dataListJson, object : TypeToken<List<Data>>() {}.type)
            Log.d("#####", "getEmployeeData: ${dataList[0].UserData.get(0).EMP_ID}")
            dataList
        } else {
            listOf()
        }
    }


    fun refreshPage() {
        Log.d(
            "hello check",
            TokenManagerImpl(getSharedPreferences("user_prefs", MODE_PRIVATE)).toString()
        )

        viewModel.getCustomers(
            TokenManagerImpl(getSharedPreferences("user_prefs", MODE_PRIVATE)), empid.toInt()
        )
    }
}
