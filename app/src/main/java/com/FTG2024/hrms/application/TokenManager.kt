package com.FTG2024.hrms.application

interface TokenManager {
    fun getToken(): String?
    fun setToken(token: String)
}