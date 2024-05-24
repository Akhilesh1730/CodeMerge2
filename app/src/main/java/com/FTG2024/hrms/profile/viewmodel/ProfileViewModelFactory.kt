package com.FTG2024.hrms.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.FTG2024.hrms.markattendance.viewmodel.MarkAttendanceViewModel
import com.FTG2024.hrms.profile.repo.ProfileRepo

class ProfileViewModelFactory(private val repository : ProfileRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}