package com.FTG2024.hrms.uidata

sealed class Response {
    data class Success(val data: Any?) : Response()
    data class Exception(val message : Any) : Response()
}
