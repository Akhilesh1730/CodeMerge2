package com.FTG2024.hrms.leaves

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.FTG2024.hrms.R
import com.FTG2024.hrms.application.TokenManager
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.base.BaseActivity
import com.FTG2024.hrms.dashboard.DashboardActivity
import com.FTG2024.hrms.databinding.ActivityLeavesBinding
import com.FTG2024.hrms.dialog.ProgressDialog
import com.FTG2024.hrms.leaves.adapter.LeavesApprovalApprovedAdapter
import com.FTG2024.hrms.leaves.adapter.LeavesApprovalPendingAdapter
import com.FTG2024.hrms.leaves.adapter.LeavesApprovalRejectedApprovedAdapter
import com.FTG2024.hrms.leaves.adapter.LeavesApprovedAdapter
import com.FTG2024.hrms.leaves.adapter.LeavesPendingAdapter
import com.FTG2024.hrms.leaves.adapter.LeavesRejectedAdapter
import com.FTG2024.hrms.leaves.fragment.ApplyLeaveFragment
import com.FTG2024.hrms.leaves.model.LeaveApprovalRequest
import com.FTG2024.hrms.leaves.model.LeaveApprovalStatusRequest
import com.FTG2024.hrms.leaves.model.LeaveDataRequest
import com.FTG2024.hrms.leaves.model.LeavesApproval
import com.FTG2024.hrms.leaves.model.LeavesApproved
import com.FTG2024.hrms.leaves.model.LeavesDataResponse
import com.FTG2024.hrms.leaves.model.LeavesPending
import com.FTG2024.hrms.leaves.model.LeavesRejected
import com.FTG2024.hrms.leaves.model.leaveapproval.LeaveApprovalResponse
import com.FTG2024.hrms.leaves.repo.LeavesApiService
import com.FTG2024.hrms.leaves.repo.LeavesRepo
import com.FTG2024.hrms.leaves.viewmodel.LeavesViewModel
import com.FTG2024.hrms.leaves.viewmodel.LeavesViewModelFactory
import com.FTG2024.hrms.retrofit.RetrofitHelper
import com.FTG2024.hrms.uidata.Response
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LeavesActivity : BaseActivity() {
    private var isPending : Boolean = true
    private var isApproved : Boolean = true
    private var isRejected : Boolean = true
    private lateinit var binding: ActivityLeavesBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var viewModel: LeavesViewModel
    private lateinit var retrofitHelper : Retrofit
    private lateinit var tokenManager: TokenManager
    private var isFragmentClicked : Boolean = false
    private lateinit var leavesRecyclerView: RecyclerView
    private lateinit var listOfPending : MutableList<LeavesPending>
    private lateinit var listOfApproved : MutableList<LeavesApproved>
    private lateinit var listOfRejected : MutableList<LeavesRejected>
    private lateinit var listOfLeavesApproval : MutableList<LeavesApproval>
    private lateinit var listOfPendingApproval : MutableList<LeavesApproval>
    private lateinit var listOfApprovedApproval : MutableList<LeavesApproval>
    private lateinit var listOfRejectedApproval : MutableList<LeavesApproval>
    private var isReporting : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeavesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        leavesRecyclerView = binding.recyclerViewLeavesStatus
        progressDialog = ProgressDialog(this, "Fetching Leaves Data")
        progressDialog.show()
        isReporting = getReportingStatus()
        if (isReporting) {
            binding.floatingActionButtonLeavesAddLeave.visibility = View.GONE
        }
        Log.d("####", "onCreate: $isReporting")
        tokenManager = TokenManagerImpl(getSharedPreferences("user_prefs", MODE_PRIVATE))
        retrofitHelper = RetrofitHelper.getRetrofitInstance(tokenManager)
        viewModel = ViewModelProvider(this, LeavesViewModelFactory(LeavesRepo(retrofitHelper.create(
            LeavesApiService::class.java)))).get(LeavesViewModel::class.java)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFragmentClicked) {
                    binding.containerApplyLeaves.visibility = View.VISIBLE
                    binding.fragmentContainerViewLeaves.visibility = View.GONE
                    isFragmentClicked = false
                } else {
                    val intent = Intent(this@LeavesActivity, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        })

        setTabsStatus()
        setListeners()
        setObservers()
        if (isReporting) {
            viewModel.getLeavesApprovalData(LeaveApprovalRequest(getEmployeeProfile()!!.REPORTING_HEAD_ID))
        } else {
            viewModel.getLeavesData(LeaveDataRequest(getEmployeeData().get(0).UserData.get(0).EMP_ID))
        }
    }

    private fun setListeners() {
        binding.tabLeavesPending.setOnClickListener {
            setAllFalse()
            isPending = true
            setTabsStatus()
            setRecyclerViews()
        }
        binding.tabLeavesApproved.setOnClickListener {
            setAllFalse()
            isApproved = true
            setTabsStatus()
            setRecyclerViews()
        }
        binding.tabLeavesRejected.setOnClickListener {
            setAllFalse()
            isRejected = true
            setTabsStatus()
            setRecyclerViews()
        }
        binding.floatingActionButtonLeavesAddLeave.setOnClickListener {
            binding.fragmentContainerViewLeaves.visibility = View.VISIBLE
            binding.containerApplyLeaves.visibility = View.GONE
            isFragmentClicked = true
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView_leaves, ApplyLeaveFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setAllFalse() {
        isPending = false
        isRejected = false
        isApproved = false
    }
    private fun setObservers() {
        viewModel.getLeavesDataLiveData().observe(this, Observer() { event ->
            event.getContentIfNotHandled().let { response ->
                when(response) {
                    is Response.Success -> {
                        val leavesDataResponse = response.data as LeavesDataResponse
                        transformLeaveData(leavesDataResponse.data)
                        progressDialog.dismiss()
                        setRecyclerViews()
                    }
                    is Response.Exception -> {
                        progressDialog.dismiss()
                    }
                    else -> return@Observer
                }
            }
        })
        viewModel.getLeavesApprovalDataLiveData().observe(this, Observer() { event ->
            event.getContentIfNotHandled().let { response ->
                when(response) {
                    is Response.Success -> {
                        val leavesApprovalDataResponse = response.data as LeaveApprovalResponse
                        transformLeaveApprovalData(leavesApprovalDataResponse.data)
                        progressDialog.dismiss()
                        setRecyclerViews()
                    }
                    is Response.Exception -> {
                        progressDialog.dismiss()
                    }
                    else -> return@Observer
                }
            }
        })

        viewModel.getLeaveApprovalStatusLiveData().observe(this, Observer { event ->
            event.getContentIfNotHandled().let { response ->
                when (response) {
                    is Response.Success -> {
                        showToast(response.data as String)
                        progressDialog.dismiss()
                        viewModel.getLeavesApprovalData(LeaveApprovalRequest(getEmployeeProfile()!!.REPORTING_HEAD_ID))
                        progressDialog = ProgressDialog(this, "Fetching Leaves Data")
                        progressDialog.show()
                    //startActivity(Intent(requireContext(), LeavesActivity::class.java))
                    }
                    is Response.Exception -> {
                        progressDialog.dismiss()
                        showToast("Failed to apply Leave")
                    }
                    else -> return@Observer
                }
            }
        })
    }

    private fun setRecyclerViews() {
        if (isReporting) {
            if (isPending) {
                setPendingApprovalRecyclerView()
            } else if (isApproved) {
                setApprovedApprovalRecyclerView()
            } else {
                setRejectedApprovalRecyclerView()
            }
        } else {
            if (isPending) {
                setPendingRecyclerView()
            } else if (isApproved) {
                setApprovedRecyclerView()
            } else {
                setRejectedRecyclerView()
            }
        }

    }

    private fun setTabsStatus() {
        val tabsBgColor = ContextCompat.getColor(this, R.color.tabs_backg)
        val tabsTxtColor = ContextCompat.getColor(this, R.color.black)
        binding.tabLeavesPending.setBackgroundColor(tabsBgColor)
        binding.tabLeavesPending.setTextColor(tabsTxtColor)
        binding.tabLeavesApproved.setBackgroundColor(tabsBgColor)
        binding.tabLeavesApproved.setTextColor(tabsTxtColor)
        binding.tabLeavesRejected.setBackgroundColor(tabsBgColor)
        binding.tabLeavesRejected.setTextColor(tabsTxtColor)
        if (isPending) {
            binding.tabLeavesPending.background = getDrawable(R.drawable.shape_leaves_status_tab)
            binding.tabLeavesPending.setTextColor(getColor(R.color.white))
        } else if (isApproved) {
            binding.tabLeavesApproved.background = getDrawable(R.drawable.shape_leaves_status_tab)
            binding.tabLeavesApproved.setTextColor(getColor(R.color.white))
        } else {
            binding.tabLeavesRejected.background = getDrawable(R.drawable.shape_leaves_status_tab)
            binding.tabLeavesRejected.setTextColor(getColor(R.color.white))
        }
    }

    private fun transformLeaveApprovalData(data: List<com.FTG2024.hrms.leaves.model.leaveapproval.Data>) {
        Log.d("####", "transformLeaveApprovalData: ")
        listOfApprovedApproval = mutableListOf()
        listOfPendingApproval = mutableListOf()
        listOfRejectedApproval = mutableListOf()
        if (listOfApprovedApproval.isNotEmpty()) listOfLeavesApproval.clear()
        if (listOfPendingApproval.isNotEmpty()) listOfLeavesApproval.clear()
        if (listOfRejectedApproval.isNotEmpty()) listOfLeavesApproval.clear()
        for (leave in data) {
            val mode = if (leave.LEAVE_MODE == "F")  "full day" else "half day"
            val leaveType = "${leave.LEAVE_TYPE_NAME} (  $mode )"
            val date = convertDateFormat(leave.DATE)
            val appliedDate = convertDateFormat(leave.APPLIED_DATE)
            val leaveID = leave.ID
            val approvedBy = "${getEmployeeProfile()!!.ID}"
            Log.d("####", "transformLeaveApprovalData: $approvedBy")
            var remark = leave.REMARK
            if (leave.APPROVAL_STATUS.equals("P")) {
                listOfPendingApproval.add(LeavesApproval(leave.EMPLOYEE_NAME, date, leaveType, appliedDate, "", "", leaveID, approvedBy))
            } else if (leave.APPROVAL_STATUS.equals("A")) {
                listOfApprovedApproval.add(LeavesApproval(leave.EMPLOYEE_NAME, date, leaveType, appliedDate, remark.toString(), "Approved", leaveID, approvedBy))
            } else {
                listOfRejectedApproval.add(LeavesApproval(leave.EMPLOYEE_NAME, date, leaveType, appliedDate, remark.toString(), "Rejected", leaveID, approvedBy))
            }
        }
    }

    private fun transformLeaveData(data: List<com.FTG2024.hrms.leaves.model.Data>) {
        listOfPending = mutableListOf()
        listOfApproved = mutableListOf()
        listOfRejected = mutableListOf()
        Log.d("####", "transformLeaveData: ${data.size}")
        if (listOfPending.isNotEmpty()) listOfPending.clear()
        if (listOfApproved.isNotEmpty()) listOfApproved.clear()
        if (listOfRejected.isNotEmpty()) listOfRejected.clear()
        for (leave in data) {
            val mode = if (leave.LEAVE_MODE == "F")  "full day" else "half day"
            val leaveType = "${leave.LEAVE_TYPE_NAME} (  $mode )"
            val date = convertDateFormat(leave.DATE)
            val reason =  leave.REASON
            val appliedDate = convertDateFormat(leave.APPLIED_DATE)
            if (leave.APPROVAL_STATUS.equals("P")) {
                listOfPending.add(LeavesPending(leaveType,
                                                date,
                                                reason,
                                                appliedDate))
            } else if (leave.APPROVAL_STATUS.equals("A")) {
                listOfApproved.add(LeavesApproved(leaveType, date, reason, appliedDate,
                                                  leave.APPROVED_BY.toString(),
                                                  leave.APPROVED_DATE.toString(),
                                                  leave.REMARK.toString()))
            } else {
                listOfRejected.add(
                    LeavesRejected(leaveType, date, reason, appliedDate,
                        leave.APPROVED_BY.toString(),
                        leave.REJECTED_DATE.toString(),
                        leave.REMARK.toString()))
            }
        }

        Log.d("####", "transformLeaveData: ${listOfPending.size} ${listOfApproved.size}")
    }

    private fun setPendingRecyclerView() {
        val adapter = LeavesPendingAdapter(listOfPending)
        val linearLayoutManager = LinearLayoutManager(this)
        leavesRecyclerView.layoutManager = linearLayoutManager
        leavesRecyclerView.adapter = adapter
    }

    private fun setPendingApprovalRecyclerView() {
        val adapter = LeavesApprovalPendingAdapter(listOfPendingApproval)
        val linearLayoutManager = LinearLayoutManager(this)

        adapter.setOnClickListener(object  : LeavesApprovalPendingAdapter.OnClickListener {

            override fun onApproveClicked(model: LeavesApproval) {
                progressDialog = ProgressDialog(this@LeavesActivity, "Approving Leave")
                progressDialog.show()
                Log.d("####", "onApproveClicked: ${model.approvedBy}")
                viewModel.approveLeave(LeaveApprovalStatusRequest(model.approvedBy, model.leaveId, model.remark))
            }

            override fun onRejectedClicked(model: LeavesApproval) {
                progressDialog = ProgressDialog(this@LeavesActivity, "Rejecting Leave")
                progressDialog.show()
                viewModel.rejectLeave(LeaveApprovalStatusRequest(model.approvedBy, model.leaveId, model.remark))
            }

        })
        leavesRecyclerView.layoutManager = linearLayoutManager
        leavesRecyclerView.adapter = adapter
    }

    private fun setApprovedRecyclerView() {
        val adapter = LeavesApprovedAdapter(listOfApproved)
        val linearLayoutManager = LinearLayoutManager(this)
        leavesRecyclerView.layoutManager = linearLayoutManager
        leavesRecyclerView.adapter = adapter
    }

    private fun setApprovedApprovalRecyclerView() {
        val adapter = LeavesApprovalApprovedAdapter(listOfApprovedApproval)
        val linearLayoutManager = LinearLayoutManager(this)
        leavesRecyclerView.layoutManager = linearLayoutManager
        leavesRecyclerView.adapter = adapter
    }

    private fun setRejectedRecyclerView() {
        val adapter = LeavesRejectedAdapter(listOfRejected)
        val linearLayoutManager = LinearLayoutManager(this)
        leavesRecyclerView.layoutManager = linearLayoutManager
        leavesRecyclerView.adapter = adapter
    }

    private fun setRejectedApprovalRecyclerView() {
        val adapter = LeavesApprovalRejectedApprovedAdapter(listOfRejectedApproval)
        val linearLayoutManager = LinearLayoutManager(this)
        leavesRecyclerView.layoutManager = linearLayoutManager
        leavesRecyclerView.adapter = adapter
    }

    fun convertDateFormat(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)

        val date: Date? = inputFormat.parse(dateString)
        return if (date != null) {
            outputFormat.format(date)
        } else {
            ""
        }
    }

    fun getReportingStatus(): Boolean {
        return getSharedPreferences("reporting_pref", Context.MODE_PRIVATE).getBoolean("IS_REPORTING", isReporting)
    }

    fun getPosition(): Int {
        return getSharedPreferences("reporting_pref", Context.MODE_PRIVATE).getInt("selection", 0)
    }

    private fun getEmployeeData() : List<com.FTG2024.hrms.login.model.Data> {
        val sharedPref = getSharedPreferences("employee_detail_pref", Context.MODE_PRIVATE)
        val dataListJson = sharedPref.getString("employeeDataListKey", null)
        if (dataListJson != null) {
            val gson = Gson()
            val dataList: List<com.FTG2024.hrms.login.model.Data> =
                gson.fromJson(dataListJson, object : TypeToken<List<com.FTG2024.hrms.login.model.Data>>() {}.type)
            Log.d("#####", "getEmployeeData: ${dataList[0].UserData.get(0).EMP_ID}")
            return dataList
        }
        return listOf()
    }

    private fun getEmployeeProfile() : com.FTG2024.hrms.profile.model.Data? {
        val sharedPref = getSharedPreferences("employee_profile_pref", Context.MODE_PRIVATE)
        val profileJson = sharedPref.getString("employeeProfileKey", null)

        if (profileJson != null) {
            val gson = Gson()
            val profile  = gson.fromJson<com.FTG2024.hrms.profile.model.Data>(profileJson, object : TypeToken<com.FTG2024.hrms.profile.model.Data>() {}.type)
            return profile
        }
        return null
    }
}