package com.FTG2024.hrms.markattendance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.FTG2024.hrms.markattendance.markattendancerepo.MarkAttendanceRepo

class MarkAttendanceViewModelFactory(private val repository : MarkAttendanceRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarkAttendanceViewModel::class.java)) {
            return MarkAttendanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}