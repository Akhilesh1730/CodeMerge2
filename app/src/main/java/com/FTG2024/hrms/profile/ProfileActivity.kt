package com.FTG2024.hrms.profile

import com.FTG2024.hrms.profile.model.MyAccount
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.FTG2024.hrms.R
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.databinding.ActivityProfleBinding
import com.FTG2024.hrms.profile.fragment.ChangePasswordFragment
import com.FTG2024.hrms.login.model.Data
import com.FTG2024.hrms.profile.fragment.ProfileDetailFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfleBinding
    private var isFragmentClicked : Boolean = false
    private lateinit var myAcc : MyAccount
    private lateinit var token : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getEmployeeData()
        val tokenManager = TokenManagerImpl(getSharedPreferences("user_prefs", MODE_PRIVATE))
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFragmentClicked) {
                    binding.containerProfile.visibility = View.VISIBLE
                    binding.profileFragmentContainerView.visibility = View.INVISIBLE
                    isFragmentClicked = false
                } else {
                    finish()
                }
            }
        })
        binding.cardProfileMyacc.setOnClickListener {
            binding.containerProfile.visibility = View.INVISIBLE
            binding.profileFragmentContainerView.visibility = View.VISIBLE
            isFragmentClicked = true
            supportFragmentManager.beginTransaction().add(R.id.profile_fragmentContainerView, ProfileDetailFragment.newInstance(myAcc))
                .addToBackStack(null)
                .commit()
        }
        binding.cardProfileChangePassword.setOnClickListener {
            binding.containerProfile.visibility = View.INVISIBLE
            binding.profileFragmentContainerView.visibility = View.VISIBLE
            isFragmentClicked = true
            supportFragmentManager.beginTransaction()
                .add(R.id.profile_fragmentContainerView, ChangePasswordFragment.newInstance(
                    getEmployeeData().get(0).UserData.get(0).EMP_ID,
                    tokenManager.getToken()))
                .addToBackStack(null).commit()
        }
        binding.cardProfileLogout.setOnClickListener {

        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
    }

    private fun getEmployeeData(): List<Data> {
        val sharedPref = getSharedPreferences("employee_detail_pref", Context.MODE_PRIVATE)
        val dataListJson = sharedPref.getString("employeeDataListKey", null)

        if (dataListJson != null) {
            val gson = Gson()
            val dataList: List<Data> =
                gson.fromJson(dataListJson, object : TypeToken<List<Data>>() {}.type)
            val user = dataList.get(0).UserData[0]
            myAcc = MyAccount(user.NAME, user.EMAIL_ID, "+91-9876543210", "Sangli")
            Log.d("###", "g.etEmployeeData: ${dataList.get(0).token}")
            return dataList
        }
        return listOf()
    }
}