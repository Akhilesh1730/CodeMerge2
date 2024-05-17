package com.FTG2024.hrms.base

data class LocationResponse(
    val code: Int,
    val data: List<Data>,
    val message: String
)