package com.FTG2024.hrms.profile

import com.FTG2024.hrms.profile.model.MyAccount
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.lifecycle.ViewModelProvider
import com.FTG2024.hrms.R
import com.FTG2024.hrms.profile.fragment.UploadProfilePhotoFragment
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.base.BaseActivity
import com.FTG2024.hrms.dashboard.DashboardActivity
import com.FTG2024.hrms.databinding.ActivityProfleBinding
import com.FTG2024.hrms.dialog.ProgressDialog
import com.FTG2024.hrms.login.LoginActivity
import com.FTG2024.hrms.profile.fragment.ChangePasswordFragment
import com.FTG2024.hrms.login.model.Data
import com.FTG2024.hrms.profile.fragment.ProfileDetailFragment
import com.FTG2024.hrms.profile.repo.ProfileRepo
import com.FTG2024.hrms.profile.repo.ProfileServiceApi
import com.FTG2024.hrms.profile.viewmodel.ProfileViewModel
import com.FTG2024.hrms.profile.viewmodel.ProfileViewModelFactory
import com.FTG2024.hrms.retrofit.RetrofitHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProfileActivity : BaseActivity() {
    private lateinit var binding : ActivityProfleBinding
    private var isFragmentClicked : Boolean = false
    private lateinit var myAcc : MyAccount
    private lateinit var token : String
    private lateinit var viewModel : ProfileViewModel
    private lateinit var apiService : ProfileServiceApi
    private lateinit var progressDialog : ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getEmployeeProfile()
        val tokenManager = TokenManagerImpl(getSharedPreferences("user_prefs", MODE_PRIVATE))
        apiService = RetrofitHelper.getRetrofitInstance(tokenManager).create(ProfileServiceApi::class.java)
        viewModel = ViewModelProvider(this, ProfileViewModelFactory(ProfileRepo(apiService)))[ProfileViewModel::class.java]
        progressDialog = ProgressDialog(this, "Fetching Data")
        progressDialog.show()
        setImage()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFragmentClicked) {
                    binding.containerProfile.visibility = View.VISIBLE
                    binding.profileFragmentContainerView.visibility = View.INVISIBLE
                    isFragmentClicked = false
                } else {
                    val intent = Intent(this@ProfileActivity, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        })
        binding.cardProfileMyacc.setOnClickListener {
            binding.containerProfile.visibility = View.INVISIBLE
            binding.profileFragmentContainerView.visibility = View.VISIBLE
            isFragmentClicked = true
            supportFragmentManager.beginTransaction().replace(R.id.profile_fragmentContainerView, ProfileDetailFragment.newInstance(myAcc))
                .addToBackStack(null)
                .commit()
        }
        binding.cardProfileChangePassword.setOnClickListener {
            binding.containerProfile.visibility = View.INVISIBLE
            binding.profileFragmentContainerView.visibility = View.VISIBLE
            isFragmentClicked = true
            supportFragmentManager.beginTransaction()
                .replace(R.id.profile_fragmentContainerView, ChangePasswordFragment.newInstance(
                    getEmployeeProfile()!!.ID,
                    tokenManager.getToken()))
                .addToBackStack(null)
                .commit()
        }
        binding.cardProfileLogout.setOnClickListener {
            clearSharedPref()
            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        binding.imageViewProfileEditPhoto.setOnClickListener {
            binding.containerProfile.visibility = View.INVISIBLE
            binding.profileFragmentContainerView.visibility = View.VISIBLE
            isFragmentClicked = true
            supportFragmentManager.beginTransaction()
                .replace(R.id.profile_fragmentContainerView, UploadProfilePhotoFragment.newInstance("",""))
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setImage() {
        Glide.with(this)
            .load("https://hrm.brothers.net.in/static/employeeProfile/20240522.jpg")
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("####", "onLoadFailed: $e")
                    showToast("Failed to Load Image")
                    progressDialog.dismiss()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    progressDialog.dismiss()
                    return false
                }

            })
            .into(binding.profileUserImage)
    }

   /* private fun hideLabel() {
        binding.reportFragment
    }*/

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
    }


    private fun getEmployeeProfile() : com.FTG2024.hrms.profile.model.Data? {
        val sharedPref = getSharedPreferences("employee_profile_pref", Context.MODE_PRIVATE)
        val profileJson = sharedPref.getString("employeeProfileKey", null)
        Log.d("###", "getEmployeeProfile: $profileJson")
        if (profileJson != null) {
            val gson = Gson()
            val profile  = gson.fromJson<com.FTG2024.hrms.profile.model.Data>(profileJson, object : TypeToken<com.FTG2024.hrms.profile.model.Data>() {}.type)
            myAcc = MyAccount(profile.FIRST_NAME + profile.LAST_NAME, profile.EMAIL_ID, profile.MOBILE_NO, profile.ADDRESS)
            binding.textViewProfileEmpName.text = profile.FIRST_NAME + profile.LAST_NAME
            binding.textViewProfileEmpDesig.text = profile.DESIGNATION
            return profile
        }
        return null
    }

    private fun clearSharedPref() {
        val empProfSharedPref = getSharedPreferences("employee_profile_pref", Context.MODE_PRIVATE)
        val empDetailSharedPref = getSharedPreferences("employee_detail_pref", Context.MODE_PRIVATE)
        empDetailSharedPref.edit().clear().apply()
        empProfSharedPref.edit().clear().apply()
        getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit().clear().apply()
    }
}