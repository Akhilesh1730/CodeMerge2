package com.FTG2024.hrms.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.databinding.ActivityForgotPasswordBinding
import com.FTG2024.hrms.profile.model.ChangePasswordData
import com.FTG2024.hrms.profile.model.ChangePasswordResponse
import com.FTG2024.hrms.login.model.Constant
import com.FTG2024.hrms.profile.repo.ChangePasswordRepo
import com.FTG2024.hrms.profile.repo.ChangePasswordService
import com.FTG2024.hrms.profile.viewmodel.ChangePasswordViewModel
import com.FTG2024.hrms.retrofit.RetrofitHelper
import com.FTG2024.hrms.uidata.Response
import com.FTG2024.hrms.profile.viewmodel.ChangePasswordViewModelFactory

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var bind: ActivityForgotPasswordBinding
    private lateinit var changePasswordViewModel: ChangePasswordViewModel
    private lateinit var changePasswordService: ChangePasswordService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(bind.root)
        val tokenManager = TokenManagerImpl(getSharedPreferences("user_prefs", MODE_PRIVATE))
        changePasswordService = RetrofitHelper.getRetrofitInstance(tokenManager).create(
            ChangePasswordService::class.java)
        changePasswordViewModel = ViewModelProvider(this, ChangePasswordViewModelFactory(
            ChangePasswordRepo(changePasswordService)
        )
        )[ChangePasswordViewModel ::class.java]
        setOnclickListeners()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun setOnclickListeners() {
        val currentPasswordEditText: EditText = bind.etCurrentPassword
        val newPasswordEditText: EditText = bind.etNewPassword
        val confirmNewPasswordEditText: EditText = bind.etConfirmNewPassword
        val changePasswordButton: Button = bind.btnChangePassword

        changePasswordButton.setOnClickListener {
            if (confirmNewPasswordEditText.text.toString() == newPasswordEditText.text.toString()){
                changePasswordViewModel.changePass(ChangePasswordData(Constant.EMP_ID,newPasswordEditText.text.toString(),currentPasswordEditText.text.toString()))
                changePasswordViewModel.getChangePassLiveData().observe(this, Observer {event ->
                    event.getContentIfNotHandled().let { response ->
                        when(response) {
                            is Response.Success -> {
                                val changePass = response.data as ChangePasswordResponse
                                Log.d("Message",response.data.toString())
                                if (changePass.code == 200) {
                                    Toast.makeText(this, changePass.message, Toast.LENGTH_SHORT).show()
                                }else if (changePass.code==304){
                                    Toast.makeText(this, changePass.message, Toast.LENGTH_SHORT).show()
                                    Log.d("Missing",response.data.message)
                                }
                            }
                            is Response.Exception -> {
                                Toast.makeText(this,response.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                            else-> return@let
                        }
                    }
                })
            }else{
                Toast.makeText(this,"Confirm Password Doesn't match", Toast.LENGTH_SHORT).show()
            }
        }
    }
}