package com.FTG2024.hrms.login.model

data class UserData(
    val EMAIL_ID: String,
    val EMP_ID: Int,
    val LAST_LOGIN_DATE: String,
    val NAME: String,
    val ROLE_DETAILS: List<ROLEDETAILS>
)