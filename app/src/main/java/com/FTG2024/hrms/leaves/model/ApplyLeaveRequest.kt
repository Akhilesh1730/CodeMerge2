package com.FTG2024.hrms.leaves.model

data class ApplyLeaveRequest(
    val APPROVAL_STATUS: String,
    val EMP_ID: Int,
    val FROM_DATE: String,
    val LEAVE_MODE: String,
    val LEAVE_TYPE_ID: Int,
    val REASON: String,
    val TO_DATE: String
)