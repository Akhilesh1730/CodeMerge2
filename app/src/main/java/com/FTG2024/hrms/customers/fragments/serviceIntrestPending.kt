package com.FTG2024.hrms.customers.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.FTG2024.hrms.R
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.customers.CustomerServiceInterestAdapter
import com.FTG2024.hrms.customers.CustomerServiceIntrest
import com.FTG2024.hrms.retrofit.RetrofitHelper
import com.example.myapplication.addCustomer.repo.addCustomerApiservice
import com.example.myapplication.addCustomer.viewmodel.ServiceViewModel
import com.example.myapplication.addCustomer.viewmodel.ServiceViewModelFactory

class serviceIntrestPending : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomerServiceInterestAdapter
    private lateinit var viewModel: ServiceViewModel
    private lateinit var serviceInterestUpdateReceiver: BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_service_intrest_pending, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CustomerServiceInterestAdapter(emptyList())
        recyclerView.adapter = adapter

        val tokenManager = TokenManagerImpl(requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE))
        val retrofit = RetrofitHelper.getRetrofitInstance(tokenManager)
        val apiService = retrofit.create(addCustomerApiservice::class.java)
        val factory = ServiceViewModelFactory(apiService)

        viewModel = ViewModelProvider(this, factory).get(ServiceViewModel::class.java)
        viewModel.serviceInterests.observe(viewLifecycleOwner) { serviceInterests ->
            adapter.setItems(serviceInterests)
        }

        val customerId = requireActivity().intent.getIntExtra(CustomerServiceIntrest.EXTRA_CUSTOMER_ID, -1)
        if (customerId != -1) {
            viewModel.fetchServiceInterests(customerId)
        } else {
            Toast.makeText(requireContext(), "Something Went Wrong. Please Try again Later!", Toast.LENGTH_LONG).show()
            Log.e("ServiceInterestPending", "No customer ID provided")
        }

        serviceInterestUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("ServiceInterestPending", "Received update broadcast, refreshing data")
                if (customerId != -1) {
                    viewModel.fetchServiceInterests(customerId)
                }
            }
        }

        requireContext().registerReceiver(
            serviceInterestUpdateReceiver,
            IntentFilter("com.FTG2024.hrms.SERVICE_INTEREST_UPDATED")
        )

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(serviceInterestUpdateReceiver)
    }
}