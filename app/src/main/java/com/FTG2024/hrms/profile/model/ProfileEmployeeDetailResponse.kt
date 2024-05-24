package com.FTG2024.hrms.profile.model

data class ProfileEmployeeDetailResponse(
    val code: Int,
    val count: Int,
    val `data`: List<Data>,
    val message: String,
    val pages: Int
)