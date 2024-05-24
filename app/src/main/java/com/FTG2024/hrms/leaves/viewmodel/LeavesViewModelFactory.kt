package com.FTG2024.hrms.leaves.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.FTG2024.hrms.attendancecalendar.viewmodel.AttendanceViewModel
import com.FTG2024.hrms.leaves.repo.LeavesRepo

class LeavesViewModelFactory(private val repo: LeavesRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeavesViewModel::class.java)) {
            return LeavesViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}