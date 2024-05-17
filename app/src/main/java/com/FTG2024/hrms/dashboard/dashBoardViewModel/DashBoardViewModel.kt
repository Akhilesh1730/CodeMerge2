package com.FTG2024.hrms.dashboard.dashBoardViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.FTG2024.hrms.base.EmpIdRequest
import com.FTG2024.hrms.dashboard.repo.DashBoardRepo
import com.FTG2024.hrms.uidata.Event
import com.FTG2024.hrms.uidata.Response
import kotlinx.coroutines.launch

class DashBoardViewModel(private val repo: DashBoardRepo) : ViewModel() {
    fun getDashBoardData(request: EmpIdRequest) {
        viewModelScope.launch {
            repo.getDashBoardData(request)
        }
    }

    fun getWorkLocation(empIdRequest: EmpIdRequest, token : String) {
        viewModelScope.launch {
            repo.getWorkLocation(empIdRequest, token)
        }
    }

    fun getLocationLiveData(): LiveData<Event<Response>> {
        return repo.locationLiveData
    }

    fun getDashBoardLiveData(): LiveData<Event<Response>> {
        return repo.dashBoardMutableLiveData
    }


}