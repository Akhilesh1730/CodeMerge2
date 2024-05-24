package com.FTG2024.hrms.leaves.model

data class LeavesApproved(val leaveType : String,
                          val date : String,
                          val reason : String,
                          val appliedOn : String,
                          val approvedBy : String,
                          val approvedOn : String,
                          val remark : String)
