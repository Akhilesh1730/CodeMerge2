package com.FTG2024.hrms.profile.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.databinding.FragmentChangePasswordBinding
import com.FTG2024.hrms.login.model.Constant
import com.FTG2024.hrms.profile.ProfileActivity
import com.FTG2024.hrms.profile.model.ChangePasswordData
import com.FTG2024.hrms.profile.model.ChangePasswordResponse
import com.FTG2024.hrms.profile.repo.ChangePasswordRepo
import com.FTG2024.hrms.profile.repo.ChangePasswordService
import com.FTG2024.hrms.profile.viewmodel.ChangePasswordViewModel
import com.FTG2024.hrms.profile.viewmodel.ChangePasswordViewModelFactory
import com.FTG2024.hrms.retrofit.RetrofitHelper
import com.FTG2024.hrms.uidata.Response


private const val ARGTOKEN : String = "profile_token"
private const val ARGID : String = "profile_id"
class ChangePasswordFragment : Fragment() {
    private lateinit var bind: FragmentChangePasswordBinding
    private lateinit var changePasswordViewModel: ChangePasswordViewModel
    private lateinit var changePasswordService: ChangePasswordService
    private  var empID :Int = 0
    private lateinit var token : String


    override fun onCreate(savedInstanceState: Bundle?) {
        val tokenManager = TokenManagerImpl(requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE))
        changePasswordService = RetrofitHelper.getRetrofitInstance(tokenManager).create(
            ChangePasswordService::class.java)
        changePasswordViewModel = ViewModelProvider(this, ChangePasswordViewModelFactory(
            ChangePasswordRepo(changePasswordService)
        )
        )[ChangePasswordViewModel ::class.java]
        arguments?.let {
            empID = it.getInt(ARGID)
            token = it.getString(ARGTOKEN).toString()
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = FragmentChangePasswordBinding.inflate(inflater, container, false)
        setOnclickListeners()
        return bind.root
    }

    private fun setOnclickListeners() {
        val currentPasswordEditText: EditText = bind.etCurrentPassword
        val newPasswordEditText: EditText = bind.etNewPassword
        val confirmNewPasswordEditText: EditText = bind.etConfirmNewPassword
        val changePasswordButton: Button = bind.btnChangePassword

        changePasswordButton.setOnClickListener {

            if (validateFields()) {
                changePasswordViewModel.changePass(ChangePasswordData(empID,newPasswordEditText.text.toString(),currentPasswordEditText.text.toString()))
                changePasswordViewModel.getChangePassLiveData().observe(viewLifecycleOwner, Observer {event ->
                    event.getContentIfNotHandled().let { response ->
                        when(response) {
                            is Response.Success -> {
                                val changePass = response.data as ChangePasswordResponse
                                Log.d("Message",response.data.toString())
                                if (changePass.code == 200) {
                                    Toast.makeText(requireContext(), "Password Changed Successfully", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(requireContext(), ProfileActivity::class.java)
                                    startActivity(intent)
                                    requireActivity().finish()
                                } else if (changePass.code==304){
                                    Toast.makeText(requireContext(), changePass.message, Toast.LENGTH_SHORT).show()
                                    Log.d("Missing",response.data.message)
                                }
                            }
                            is Response.Exception -> {
                                Toast.makeText(requireContext(),response.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                            else-> return@let
                        }
                    }
                })
            }
        }
    }

    private fun validateFields() : Boolean {
        val passText = bind.etCurrentPassword.text.toString()
        val newPassText = bind.etNewPassword.text.toString()
        val confirmPassText = bind.etConfirmNewPassword.text.toString()
        Log.d("####", "validateFields: $newPassText $confirmPassText")
        if (passText.isNullOrEmpty()) {
            showToast("Please Enter Current Password")
            return false
        } else if (newPassText.isNullOrEmpty()) {
            showToast("Please Enter New Password")
            return false
        } else if (confirmPassText.isNullOrEmpty()) {
            showToast("Please Enter Confirm Password")
            return false
        } else if (newPassText != confirmPassText) {
            showToast("New and Confirm Passwords do not match")
            return false
        } else if (newPassText == passText) {
            showToast("New Password should not be the same as Current Password")
            return false
        }
        return true
    }

    private fun showToast(msg : String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
    companion object {
        @JvmStatic
        fun newInstance(empID: Int, token: String?) =
            ChangePasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARGTOKEN, token)
                    putInt(ARGID, empID)
                }
            }
    }
}