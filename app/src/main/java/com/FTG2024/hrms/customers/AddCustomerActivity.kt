package com.FTG2024.hrms.customers

import CustomerAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.FTG2024.hrms.R
import com.FTG2024.hrms.application.TokenManagerImpl
import com.example.myapplication.addCustomer.add_csutomer_dialog
import com.example.myapplication.addCustomer.viewmodel.CustomerViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddCustomerActivity : AppCompatActivity() {

    private lateinit var viewModel: CustomerViewModel
    private lateinit var adapter: CustomerAdapter
    private lateinit var fabButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_customer)

        viewModel = ViewModelProvider(this).get(CustomerViewModel::class.java)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = CustomerAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.getCustomers( TokenManagerImpl(getSharedPreferences("user_prefs", MODE_PRIVATE)))
        viewModel.customers.observe(this, { customers ->
            adapter.submitList(customers)
        })



        fabButton = findViewById(R.id.fab)
        fabButton.setOnClickListener{
            val customDialog = add_csutomer_dialog(this@AddCustomerActivity, this@AddCustomerActivity)
            customDialog.show()
        }
    }
}
