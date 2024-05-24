package com.FTG2024.hrms.leaves.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.FTG2024.hrms.leaves.model.ApplyLeaveRequest
import com.FTG2024.hrms.leaves.model.LeaveApprovalRequest
import com.FTG2024.hrms.leaves.model.LeaveApprovalStatusRequest
import com.FTG2024.hrms.leaves.model.LeaveDataRequest
import com.FTG2024.hrms.leaves.repo.LeavesRepo
import com.FTG2024.hrms.uidata.Event
import com.FTG2024.hrms.uidata.Response
import kotlinx.coroutines.launch
import retrofit2.http.Body

class LeavesViewModel(private val repo: LeavesRepo) : ViewModel() {

    fun getLeavesType() {
        viewModelScope.launch {
            repo.getLeavesType()
        }
    }

    fun getLeavesData(request : LeaveDataRequest) {
        viewModelScope.launch {
            repo.getLeavesData(request)
        }
    }

    fun getLeavesApprovalData(request : LeaveApprovalRequest) {
        viewModelScope.launch {
            repo.getLeavesApprovalData(request)
        }
    }

    fun applyLeave(request: ApplyLeaveRequest) {
        viewModelScope.launch {
            repo.applyLeave(request)
        }
    }

    fun approveLeave(request: LeaveApprovalStatusRequest) {
        viewModelScope.launch {
            repo.approveLeave(request)
        }
    }

    fun rejectLeave(request: LeaveApprovalStatusRequest) {
        viewModelScope.launch {
            repo.rejectLeave(request)
        }
    }

    fun getLeavesDataLiveData(): LiveData<Event<Response>> {
        return repo.leavesDataMutableLiveData
    }

    fun getLeavesApprovalDataLiveData(): LiveData<Event<Response>> {
        return repo.leavesApprovalDataMutableLiveData
    }

    fun getApplyLeaveLiveData(): LiveData<Event<Response>> {
        return repo.applyLeaveMutableLiveData
    }

    fun getLeaveApprovalStatusLiveData(): LiveData<Event<Response>> {
        return repo.leaveStatusMutableLiveData
    }


    fun getLeavesTypeLivedata(): LiveData<Event<Response>> {
        return repo.leavesTypeMutableLiveData
    }
}