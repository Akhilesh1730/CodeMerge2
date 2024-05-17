package com.FTG2024.hrms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.FTG2024.hrms.dashboard.DashboardActivity

class NoPermissionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_permission)
        val buttonAllow = findViewById<Button>(R.id.button_allow_location)
        val buttonExit = findViewById<Button>(R.id.button_exit_app)
        buttonAllow.setOnClickListener {
            val intent = Intent(this, DashboardActivity :: class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        buttonExit.setOnClickListener {
            finish()
        }
    }
}