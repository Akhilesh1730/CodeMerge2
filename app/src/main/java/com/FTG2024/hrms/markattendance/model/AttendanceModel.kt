package com.FTG2024.hrms.markattendance.model

data class AttendanceModel( val empId: Int,
                            val dayInLocation: String,
                            val dayInDistance: String,
                            val dayInDeviceId: String,
                            val dayInImgUrl: String,
                            val dayInRemark: String)
