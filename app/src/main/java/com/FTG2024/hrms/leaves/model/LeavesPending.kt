package com.FTG2024.hrms.leaves.model

data class LeavesPending(val leaveType : String,
                         val date : String,
                         val reason : String,
                         val appliedOn : String)