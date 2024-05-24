package com.FTG2024.hrms.leaves.model

data class LeavesRejected(val leaveType : String,
                          val date : String,
                          val reason : String,
                          val appliedOn : String,
                          val rejectedBy : String,
                          val rejectedOn : String,
                          val remark : String)