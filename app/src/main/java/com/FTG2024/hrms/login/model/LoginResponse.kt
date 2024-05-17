package com.FTG2024.hrms.login.model

data class LoginResponse(
    val code: Int,
    val data: List<Data>,
    val message: String
)