package com.FTG2024.hrms.leaves.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.FTG2024.hrms.R
import com.FTG2024.hrms.databinding.FragmentApplyLeaveBinding
import com.FTG2024.hrms.dialog.ProgressDialog
import com.FTG2024.hrms.leaves.LeavesActivity
import com.FTG2024.hrms.leaves.model.ApplyLeaveRequest
import com.FTG2024.hrms.leaves.model.leavetupe.LeaveTypeResponse
import com.FTG2024.hrms.leaves.viewmodel.LeavesViewModel
import com.FTG2024.hrms.login.model.Data
import com.FTG2024.hrms.uidata.Response
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ApplyLeaveFragment : Fragment() {

    private lateinit var binding : FragmentApplyLeaveBinding
    private val viewModel: LeavesViewModel by activityViewModels()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var leaveType : String
    private lateinit var leaveMode : String
    private lateinit var startDate : String
    private lateinit var endDate : String
    private lateinit var reason : String
    private lateinit var leavesTypeIdMap : MutableMap<String, Int>
    private var halfLeaveSession : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplyLeaveBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       /* progressDialog = ProgressDialog(requireActivity(), "Loading..")
        progressDialog.show()*/
        setListeners()
        setUpObservers()
        setDateTexts()
        viewModel.getLeavesType()
    }

    private fun setDateTexts() {
        binding.textviewApplyLeaveStartDate.text = getCurrentDateFormatted()
        binding.textviewApplyLeaveEndDate.text = getCurrentDateFormatted()
        startDate = getCurrentDateFormattedForServer()
        endDate = getCurrentDateFormattedForServer()
    }

    private fun setUpObservers() {
        viewModel.getLeavesTypeLivedata().observe(viewLifecycleOwner, Observer {event->
            event.getContentIfNotHandled().let {response ->
                when(response) {
                    is Response.Success -> {
                        val leaveTypeResponse = response.data as LeaveTypeResponse
                        val list = mutableListOf<String>()
                        leavesTypeIdMap = mutableMapOf<String, Int>()
                        for (type in leaveTypeResponse.data) {
                            list.add(type.NAME)
                            leavesTypeIdMap[type.NAME] = type.ID
                        }
                        val leavesTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, list)
                        //leavesTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        binding.spinnerApplyLeaveLeaveType.adapter = leavesTypeAdapter
                        binding.spinnerApplyLeaveMode.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf("Full", "Half"))
                        //progressDialog.dismiss()
                    }
                    is Response.Success -> {
                        progressDialog.dismiss()
                    }
                    else -> return@Observer
                }
            }
        })

        viewModel.getApplyLeaveLiveData().observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled().let { response ->
                when (response) {
                    is Response.Success -> {
                        showToast("Leave Applied Successfully")
                        startActivity(Intent(requireContext(), LeavesActivity::class.java))
                    }
                    is Response.Exception -> {
                        showToast("Failed to apply Leave")
                    }
                    else -> return@Observer
                }
            }
        })
    }

    @SuppressLint("ResourceAsColor")
    private fun setListeners() {
        binding.buttonApplyLeaveSubmit.setOnClickListener {
            val reasonText = binding.editTextTextMultiLine.text.toString()
            if (reasonText.isNullOrEmpty()) {
                showToast("Please enter reason")
            } else {
                reason = reasonText
                sendDataToTheServer()
                progressDialog = ProgressDialog(requireActivity(), "Applying Leave")
                progressDialog.show()
            }
        }

        binding.textviewApplyLeaveStartDate.setOnClickListener {
            showDatePicker(true)
        }

        binding.textviewApplyLeaveEndDate.setOnClickListener {
            showDatePicker(false)
        }

        binding.spinnerApplyLeaveLeaveType.onItemSelectedListener = object :  AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                leaveType = parent.getItemAtPosition(position).toString()
                Log.d("####", "onItemSelected: $leaveType")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.spinnerApplyLeaveMode.onItemSelectedListener = object :  AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                leaveMode = parent.getItemAtPosition(position).toString()
                if (leaveMode == "Half") {
                    binding.textViewApplyLeaveFirstSession.visibility = View.VISIBLE
                    binding.textViewApplyLeaveSecondSession.visibility = View.VISIBLE
                } else {
                    binding.textViewApplyLeaveFirstSession.visibility = View.GONE
                    binding.textViewApplyLeaveSecondSession.visibility = View.GONE
                }
                Log.d("####", "onItemSelected: $leaveMode")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        binding.textViewApplyLeaveFirstSession.setOnClickListener {
            binding.textViewApplyLeaveFirstSession.background = ContextCompat.getDrawable(requireContext(), R.drawable.shape_apply_leave_mode_text_selected)
            binding.textViewApplyLeaveFirstSession.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.textViewApplyLeaveSecondSession.background = ContextCompat.getDrawable(requireContext(), R.drawable.shape_apply_leave_leave_mode_text)
            binding.textViewApplyLeaveSecondSession.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            halfLeaveSession = "first"
        }
        binding.textViewApplyLeaveSecondSession.setOnClickListener {
            binding.textViewApplyLeaveSecondSession.background = ContextCompat.getDrawable(requireContext(), R.drawable.shape_apply_leave_mode_text_selected)
            binding.textViewApplyLeaveSecondSession.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.textViewApplyLeaveFirstSession.background = ContextCompat.getDrawable(requireContext(), R.drawable.shape_apply_leave_leave_mode_text)
            binding.textViewApplyLeaveFirstSession.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            halfLeaveSession = "second"
        }
    }

    private fun sendDataToTheServer() {
        val mode = if (leaveMode == "Full") "F" else "H"
        viewModel.applyLeave(
            ApplyLeaveRequest("P",
            getEmployeeData().get(0).UserData.get(0).EMP_ID, startDate, mode,
                leavesTypeIdMap[leaveType]!!, reason, endDate))
    }
    private fun showDatePicker(isStartDate : Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())
                val serverDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                if (isStartDate) {
                    binding.textviewApplyLeaveStartDate.text = dateFormat.format(selectedDate.time)
                    startDate = serverDateFormat.format(selectedDate.time)
                } else {
                    binding.textviewApplyLeaveEndDate.text = dateFormat.format(selectedDate.time)
                    endDate = serverDateFormat.format(selectedDate.time)
                }
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun showToast(msg : String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
    companion object {
        fun newInstance() =
            ApplyLeaveFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    private fun getCurrentDateFormatted(): String {
        val dateFormat = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getCurrentDateFormattedForServer(): String {
        val serverDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return serverDateFormat.format(Date())
    }

    private fun getEmployeeData() : List<com.FTG2024.hrms.login.model.Data> {
        val sharedPref = requireContext().getSharedPreferences("employee_detail_pref", Context.MODE_PRIVATE)
        val dataListJson = sharedPref.getString("employeeDataListKey", null)
        if (dataListJson != null) {
            val gson = Gson()
            val dataList: List<com.FTG2024.hrms.login.model.Data> =
                gson.fromJson(dataListJson, object : TypeToken<List<Data>>() {}.type)
            Log.d("#####", "getEmployeeData: ${dataList[0].UserData.get(0).EMP_ID}")
            return dataList
        }
        return listOf()
    }
}