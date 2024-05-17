package com.FTG2024.hrms.dialog

import android.app.Activity
import android.app.Dialog
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.FTG2024.hrms.R

class ProgressDialog(private val activity: Activity, private val msg : String) : Dialog(activity) {
    init {
        val params : WindowManager.LayoutParams = window!!.attributes
        params.gravity =Gravity.CENTER_HORIZONTAL
        window!!.attributes = params
        setTitle(null)
        setCancelable(false)
        setOnCancelListener(null)
        val view = layoutInflater.inflate(R.layout.progress_dialog_layout, null)
        setContentView(view)
        val textMsg = view.findViewById<TextView>(R.id.progressBar_markattendance_activity_text)
        textMsg.text = msg
        window!!.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.shape_progress_dialog)) // Replace with your drawable resource
    }

}