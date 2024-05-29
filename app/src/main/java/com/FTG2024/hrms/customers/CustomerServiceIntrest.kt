package com.FTG2024.hrms.customers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.FTG2024.hrms.R
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.retrofit.RetrofitHelper
import com.example.myapplication.addCustomer.repo.addCustomerApiservice
import com.example.myapplication.addCustomer.viewmodel.ServiceViewModel
import com.example.myapplication.addCustomer.viewmodel.ServiceViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.FTG2024.hrms.assets.fragments.fragment_approved
import com.FTG2024.hrms.assets.fragments.fragment_pending
import com.FTG2024.hrms.assets.fragments.fragment_rejected
import com.FTG2024.hrms.customers.fragments.serviceIntrestApproved
import com.FTG2024.hrms.customers.fragments.serviceIntrestPending
import com.FTG2024.hrms.customers.fragments.serviceIntrestRejected


class CustomerServiceIntrest : AppCompatActivity() {

    private lateinit var viewModel: ServiceViewModel

    private lateinit var fabButton: FloatingActionButton

    private lateinit var tabOne: TextView
    private lateinit var tabTwo: TextView
    private lateinit var tabThree: TextView
    private var selectedTabNumber = 1


    companion object {
        private const val TAG = "CustomerServiceIntrest"
        const val EXTRA_CUSTOMER_ID = "CUSTOMER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer_service_intrest_list)

        fabButton = findViewById(R.id.fabaddCustomersintrest)
        tabOne = findViewById(R.id.taboneProductIntrest)
        tabTwo = findViewById(R.id.tabTwoProductIntrest)
        tabThree = findViewById(R.id.tabThreeProductIntrest)



        val toolbar: Toolbar = findViewById(R.id.toolbaradd)
        val tokenManager = TokenManagerImpl(getSharedPreferences("user_prefs", MODE_PRIVATE))
        val retrofit = RetrofitHelper.getRetrofitInstance(tokenManager)
        val apiService = retrofit.create(addCustomerApiservice::class.java)
        val factory = ServiceViewModelFactory(apiService)


        tabOne.setOnClickListener { selectTab(1) }
        tabTwo.setOnClickListener { selectTab(2) }
        tabThree.setOnClickListener { selectTab(3) }

        selectTab(selectedTabNumber)



        setSupportActionBar(toolbar)
        viewModel = ViewModelProvider(this, factory).get(ServiceViewModel::class.java)

        val customerId = intent.getIntExtra(EXTRA_CUSTOMER_ID, -1)
        if (customerId != -1) {
            viewModel.fetchServiceInterests(customerId)
        } else {
            Toast.makeText(this, "Something Went Wrong Please Try again Later!", Toast.LENGTH_LONG)
                .show()
            Log.e(TAG, "No customer ID provided")
        }


        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }



        fabButton.setOnClickListener {
            val intent = Intent(this, addServiceIntrestActivity::class.java)
            intent.putExtra("customerId", customerId)
            startActivity(intent)
        }


    }


    private fun selectTab(tabNumber: Int) {
        selectedTabNumber = tabNumber


        val whiteColor = ContextCompat.getColor(this, R.color.white)

        tabOne.setBackgroundResource(if (tabNumber == 1) R.drawable.active_tabbar_background else R.drawable.tabbar_background)
        tabOne.setTextColor(
            if (tabNumber == 1) whiteColor else ContextCompat.getColor(
                this,
                R.color.black
            )
        )

        tabTwo.setBackgroundResource(if (tabNumber == 2) R.drawable.active_tabbar_background else R.drawable.tabbar_background)
        tabTwo.setTextColor(
            if (tabNumber == 2) whiteColor else ContextCompat.getColor(
                this,
                R.color.black
            )
        )

        tabThree.setBackgroundResource(if (tabNumber == 3) R.drawable.active_tabbar_background else R.drawable.tabbar_background)
        tabThree.setTextColor(
            if (tabNumber == 3) whiteColor else ContextCompat.getColor(
                this,
                R.color.black
            )
        )

        val fragment = when (tabNumber) {
            1 -> serviceIntrestPending()
            2 -> serviceIntrestApproved()
            3 -> serviceIntrestRejected()
            else -> serviceIntrestPending()
        }

        fragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, it)
                .commit()
        }
    }

}
