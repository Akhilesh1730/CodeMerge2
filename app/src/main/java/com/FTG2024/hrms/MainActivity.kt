package com.FTG2024.hrms

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.FTG2024.hrms.application.TokenManager
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.dashboard.DashboardActivity
import com.FTG2024.hrms.dialog.ProgressDialog
import com.FTG2024.hrms.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var tokenManager: TokenManager
    private lateinit var intent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        splashScreen.setKeepOnScreenCondition { true }
        ProgressDialog(this, "Loading").show()
        tokenManager = TokenManagerImpl(getSharedPreferences("user_prefs", Context.MODE_PRIVATE))
        if (!tokenManager.getToken().isNullOrEmpty()) {
            intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        } else {
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}