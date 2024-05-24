package com.FTG2024.hrms.leaves.model.leaveapproval

data class Data(
    val APPLIED_DATE: String,
    val APPROVAL_STATUS: String,
    val APPROVED_BY: Any,
    val APPROVED_DATE: Any,
    val CREATED_MODIFIED_DATE: String,
    val DATE: String,
    val EMPLOYEE_NAME: String,
    val EMP_ID: Int,
    val HALF_DAY_SESSION: String,
    val ID: Int,
    val LEAVE_MODE: String,
    val LEAVE_TYPE_ID: Int,
    val LEAVE_TYPE_NAME: String,
    val REASON: String,
    val REJECTED_DATE: Any,
    val REMARK: Any,
    val REPORTING_HEAD_ID: Int
)