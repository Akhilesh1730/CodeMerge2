package com.FTG2024.hrms.profile.model

data class ChangePasswordData(
    val EMP_ID: Int,
    val NEW_PASSWORD: String,
    val OLD_PASSWORD: String
)


