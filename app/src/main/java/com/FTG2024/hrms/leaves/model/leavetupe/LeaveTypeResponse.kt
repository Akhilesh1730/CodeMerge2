package com.FTG2024.hrms.leaves.model.leavetupe

data class LeaveTypeResponse(
    val code: Int,
    val count: Int,
    val `data`: List<Data>,
    val message: String,
    val pages: Int
)