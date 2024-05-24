package com.FTG2024.hrms.leaves.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.FTG2024.hrms.leaves.model.ApplyLeaveRequest
import com.FTG2024.hrms.leaves.model.LeaveApprovalRequest
import com.FTG2024.hrms.leaves.model.LeaveApprovalStatusRequest
import com.FTG2024.hrms.leaves.model.LeaveDataRequest
import com.FTG2024.hrms.uidata.Event
import com.FTG2024.hrms.uidata.Response
import retrofit2.http.Body

class LeavesRepo(private val apiService: LeavesApiService) {
    private var _leavesDataMutableLiveData: MutableLiveData<Event<Response>> = MutableLiveData()
    val leavesDataMutableLiveData : LiveData<Event<Response>>
        get() = _leavesDataMutableLiveData

    private var _leavesApprovalDataMutableLiveData: MutableLiveData<Event<Response>> = MutableLiveData()
    val leavesApprovalDataMutableLiveData : LiveData<Event<Response>>
        get() = _leavesApprovalDataMutableLiveData

    private var _applyLeaveMutableLiveData: MutableLiveData<Event<Response>> = MutableLiveData()
    val applyLeaveMutableLiveData : LiveData<Event<Response>>
        get() = _applyLeaveMutableLiveData

    private var _leaveStatusMutableLiveData: MutableLiveData<Event<Response>> = MutableLiveData()
    val leaveStatusMutableLiveData : LiveData<Event<Response>>
        get() = _leaveStatusMutableLiveData

    private var _leavesTypeMutableLiveData: MutableLiveData<Event<Response>> = MutableLiveData()
    val leavesTypeMutableLiveData : LiveData<Event<Response>>
        get() = _leavesTypeMutableLiveData


    suspend fun getLeavesType() {
        val response = apiService.getLeaveType()
        Log.d("####", "getLeavesData: $response")
        if (response.isSuccessful) {
            val leaveTypeResponse = response.body()
            if (leaveTypeResponse != null && leaveTypeResponse.code == 200) {
                _leavesTypeMutableLiveData.postValue(Event(Response.Success(leaveTypeResponse)))
            } else {
                _leavesTypeMutableLiveData.postValue(Event(Response.Exception("Something went wrong retry")))
            }
        } else {
            _leavesTypeMutableLiveData.postValue(Event(Response.Exception("Something went wrong retry")))
        }
    }

    suspend fun getLeavesData(request : LeaveDataRequest) {
        val response = apiService.getLeavesData(request)
        Log.d("####", "getLeavesData: $response")
        if (response.isSuccessful) {
            val leavesDataResponse = response.body()
            if (leavesDataResponse != null && leavesDataResponse.code == 200) {
                _leavesDataMutableLiveData.postValue(Event(Response.Success(leavesDataResponse)))
            } else {
                _leavesDataMutableLiveData.postValue(Event(Response.Exception("Something went wrong retry")))
            }
        } else {
            _leavesDataMutableLiveData.postValue(Event(Response.Exception("Something went wrong retry")))
        }
    }

    suspend fun getLeavesApprovalData(request : LeaveApprovalRequest) {
        val response = apiService.getLeavesApprovalData(request)
        Log.d("####", "getLeavesData: $response")
        if (response.isSuccessful) {
            val leaveApprovalResponse = response.body()
            if (leaveApprovalResponse != null && leaveApprovalResponse.code == 200) {
                _leavesApprovalDataMutableLiveData.postValue(Event(Response.Success(leaveApprovalResponse)))
            } else {
                _leavesApprovalDataMutableLiveData.postValue(Event(Response.Exception("Something went wrong retry")))
            }
        } else {
            _leavesApprovalDataMutableLiveData.postValue(Event(Response.Exception("Something went wrong retry")))
        }
    }

    suspend fun applyLeave(request: ApplyLeaveRequest) {
        val response = apiService.applyLeave(request)
        Log.d("####", "getLeavesData: $response")
        if (response.isSuccessful) {
            val applyLeaveResponse = response.body()
            if (applyLeaveResponse != null && applyLeaveResponse.code == 200) {
                _applyLeaveMutableLiveData.postValue(Event(Response.Success("")))
            } else {
                _applyLeaveMutableLiveData.postValue(Event(Response.Exception("Something went wrong retry")))
            }
        } else {
            _applyLeaveMutableLiveData.postValue(Event(Response.Exception("Something went wrong retry")))
        }
    }

    suspend fun approveLeave(request: LeaveApprovalStatusRequest) {
        val response = apiService.approveLeave(request)
        Log.d("####", "getLeavesData: $response")
        if (response.isSuccessful) {
            val leaveApprovalStatusResponse = response.body()
            if (leaveApprovalStatusResponse != null && leaveApprovalStatusResponse.code == 200) {
                _leaveStatusMutableLiveData.postValue(Event(Response.Success("Leave Approved Successfully")))
            } else {
                _leaveStatusMutableLiveData.postValue(Event(Response.Exception("Something went wrong retry")))
            }
        } else {
            _leaveStatusMutableLiveData.postValue(Event(Response.Exception("Something went wrong retry")))
        }
    }

    suspend fun rejectLeave(request: LeaveApprovalStatusRequest) {
        val response = apiService.rejectLeave(request)
        Log.d("####", "getLeavesData: $response")
        if (response.isSuccessful) {
            val leaveApprovalStatusResponse = response.body()
            if (leaveApprovalStatusResponse != null && leaveApprovalStatusResponse.code == 200) {
                _leaveStatusMutableLiveData.postValue(Event(Response.Success("Leave Rejected Successfully")))
            } else {
                _leaveStatusMutableLiveData.postValue(Event(Response.Exception("Something went wrong retry")))
            }
        } else {
            _leaveStatusMutableLiveData.postValue(Event(Response.Exception("Something went wrong retry")))
        }
    }
}