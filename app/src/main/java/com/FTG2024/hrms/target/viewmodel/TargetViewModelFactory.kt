//package com.FTG2024.hrms.target
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.FTG2024.hrms.target.repo.TargetRepository
//
//class TargetViewModelFactory(private val repository: TargetRepository) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(TargetViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return TargetViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
