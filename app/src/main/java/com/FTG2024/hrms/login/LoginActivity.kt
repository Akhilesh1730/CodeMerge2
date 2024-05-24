package com.FTG2024.hrms.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.FTG2024.hrms.application.TokenManager
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.base.BaseActivity
import com.FTG2024.hrms.dashboard.DashboardActivity
import com.FTG2024.hrms.databinding.ActivityLoginBinding
import com.FTG2024.hrms.dialog.ProgressDialog
import com.FTG2024.hrms.login.model.Data
import com.FTG2024.hrms.login.model.LoginRequest
import com.FTG2024.hrms.login.model.LoginResponse
import com.FTG2024.hrms.login.repo.LoginActivityApiService
import com.FTG2024.hrms.login.repo.LoginRepository
import com.FTG2024.hrms.login.viewmodel.LoginViewModel
import com.FTG2024.hrms.login.viewmodel.LoginViewModelFactory
import com.FTG2024.hrms.profile.model.ProfileEmployeeDetailResponse
import com.FTG2024.hrms.retrofit.RetrofitHelper
import com.FTG2024.hrms.uidata.Response
import com.google.gson.Gson

class LoginActivity : BaseActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var userText : TextView
    private lateinit var passText : TextView
    private lateinit var progressDialog : ProgressDialog
    private lateinit var loginApi : LoginActivityApiService
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var tokenManager: TokenManager
    private var empID : Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userText = binding.edittextUsernameLoginActivity
        passText = binding.edittextPasswordLoginActivity
        tokenManager = TokenManagerImpl(getSharedPreferences("user_prefs", Context.MODE_PRIVATE))
        loginApi = RetrofitHelper.getRetrofitInstance(tokenManager).create(LoginActivityApiService :: class.java)
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(LoginRepository(loginApi))).get(LoginViewModel ::class.java)
        setListeners()
    }

    private fun setListeners() {
        binding.buttonSubmit.setOnClickListener {
            progressDialog = getProgressDialog("Validating user")
            progressDialog.show()
            if (validateFields()) {
                authUser(userText.text.toString(), passText.text.toString())
            } else {
                progressDialog.dismiss()
            }
        }
      /*  binding.textviewLoginForgotPass.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }*/
    }

    private fun validateFields() : Boolean{
        val email = userText.text.toString()
        val password = passText.text.toString()

        if (email.isNullOrEmpty()) {
            showToast("Please Enter Email")
            return false
        }

        if (password.isNullOrEmpty()) {
            showToast("Please Enter Password")
            return false
        }

        return true
    }

    private fun authUser(username : String, password : String) {
        loginViewModel.authUser(LoginRequest(username, password))
        loginViewModel.getLoginUserLiveData().observe(this, Observer {event ->
            event.getContentIfNotHandled().let { response ->
                when(response) {
                    is Response.Success -> {
                        val loginResponse = response.data as LoginResponse
                        if (loginResponse.code == 200) {
                            addListInSharedPref(loginResponse.data)
                            //navigateToDashBoard(loginResponse.data)
                            loginViewModel.getEmployeeProfile()
                        } else if (loginResponse.code == 304) {
                            showToast(loginResponse.message)
                        } else {
                            showToast("Unable To Reach Server. Retry")
                        }
                        progressDialog.dismiss()
                    }
                    is Response.Exception -> {
                        showToast(response.message.toString())
                        progressDialog.dismiss()
                    }
                    else-> return@Observer
                }
            }
        })

        loginViewModel.getUserLiveData().observe(this, Observer {event->
            event.getContentIfNotHandled().let { response ->
                when(response) {
                    is Response.Success -> {
                        val profileEmployeeDetailResponse = response.data as ProfileEmployeeDetailResponse
                        if (profileEmployeeDetailResponse.code == 200) {
                            addEmployeeDetailsInSharedPref(profileEmployeeDetailResponse.data)
                            navigateToDashBoard()
                        } else if (profileEmployeeDetailResponse.code == 304) {
                            showToast(profileEmployeeDetailResponse.message)
                        } else {
                            showToast("Unable To Reach Server. Retry")
                        }
                        progressDialog.dismiss()
                    }
                    is Response.Exception -> {
                        showToast(response.message.toString())
                        progressDialog.dismiss()
                    }
                    else-> return@Observer
                }
            }
        })
    }

    private fun navigateToDashBoard(/*data: List<Data>*/) {
        val intent = Intent(this, DashboardActivity :: class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("isLogin", true)
        startActivity(intent)
    }

    private fun addListInSharedPref(data: List<Data>) {
        val gson = Gson()
        val dataListJson = gson.toJson(data)
        tokenManager.setToken(data[0].token)
        Log.d("####", "addListInSharedPref: ${data[0].token}")
        empID = data.get(0).UserData.get(0).EMP_ID
        val sharedPref = getSharedPreferences("employee_detail_pref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("employeeDataListKey", dataListJson)
        editor.apply()
    }

    private fun addEmployeeDetailsInSharedPref(data: List<com.FTG2024.hrms.profile.model.Data>) {
        for (profile in data) {
            if (profile.ID == empID) {
                val gson = Gson()
                val sharedPref = getSharedPreferences("employee_profile_pref", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putString("employeeProfileKey", gson.toJson(profile))
                editor.apply()
                break
            }
        }
    }
}