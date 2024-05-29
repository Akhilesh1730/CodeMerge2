//package com.FTG2024.hrms.target
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.FTG2024.hrms.target.model.targetbodyModel
//import com.FTG2024.hrms.target.model.targetbodyModel.Data
//import com.FTG2024.hrms.target.repo.TargetRepository
//import kotlinx.coroutines.launch
//
//class TargetViewModel(private val repository: TargetRepository) : ViewModel() {
//    private val _targetData = MutableLiveData<List<Data>>()
//    val targetData: LiveData<List<Data>> = _targetData
//
//    fun getTargetData(requestBody: targetbodyModel) {
//        viewModelScope.launch {
//            val response = repository.getTargetData(requestBody)
//            if (response.code == 200) {
//                _targetData.postValue(response.data)
//            } else {
//                // Handle error
//            }
//        }
//    }
//}
