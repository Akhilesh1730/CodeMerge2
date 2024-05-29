package com.FTG2024.hrms.leaves.repo

import com.FTG2024.hrms.leaves.model.ApplyLeaveRequest
import com.FTG2024.hrms.leaves.model.ApplyLeaveResponse
import com.FTG2024.hrms.leaves.model.FilterRequest
import com.FTG2024.hrms.leaves.model.LeaveApprovalRequest
import com.FTG2024.hrms.leaves.model.LeaveApprovalStatusRequest
import com.FTG2024.hrms.leaves.model.LeaveApprovalStatusResponse
import com.FTG2024.hrms.leaves.model.LeaveDataRequest
import com.FTG2024.hrms.leaves.model.LeavesApproval
import com.FTG2024.hrms.leaves.model.LeavesDataResponse
import com.FTG2024.hrms.leaves.model.leaveapproval.LeaveApprovalResponse
import com.FTG2024.hrms.leaves.model.leavetupe.LeaveTypeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LeavesApiService {

    @POST("/api/leave/get")
    suspend fun getLeavesData(@Body request : LeaveDataRequest) : Response<LeavesDataResponse>

    @POST("/api/leave/get")
    suspend fun getLeavesApprovalData(@Body request : LeaveApprovalRequest) : Response<LeaveApprovalResponse>

    @POST("/api/leave/approveLeave")
    suspend fun approveLeave(@Body request: LeaveApprovalStatusRequest) : Response<LeaveApprovalStatusResponse>

    @POST("/api/leave/rejectLeave")
    suspend fun rejectLeave(@Body request: LeaveApprovalStatusRequest) : Response<LeaveApprovalStatusResponse>

    @POST("/api/leave/createLeave")
    suspend fun applyLeave(@Body request: ApplyLeaveRequest) : Response<ApplyLeaveResponse>

    @POST("/api/leaveType/get")
    suspend fun getLeaveType(@Body filterRequest: FilterRequest): Response<LeaveTypeResponse>
}