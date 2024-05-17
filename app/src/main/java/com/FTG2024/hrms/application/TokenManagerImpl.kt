package com.FTG2024.hrms.application

import android.content.SharedPreferences

class TokenManagerImpl(private val sharedPreferences: SharedPreferences) : TokenManager {

    override fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    override fun setToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString("access_token", token)
        editor.apply() // Use apply for asynchronous saving
    }
}