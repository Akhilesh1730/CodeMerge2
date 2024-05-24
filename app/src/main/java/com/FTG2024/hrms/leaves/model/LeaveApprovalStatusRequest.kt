package com.FTG2024.hrms.leaves.model

data class LeaveApprovalStatusRequest(
    val APPROVED_BY: String,
    val LEAVE_ID: Int,
    val REMARK: String
)