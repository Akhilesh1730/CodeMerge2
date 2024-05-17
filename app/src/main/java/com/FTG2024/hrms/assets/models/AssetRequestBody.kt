package com.example.myapplication.models

data class AssetRequestBody(

    val EMP_ID: Int,
    val categoryId: Int,
    val ASSET_ID: Int,
    val DATE: String,
    val REQUESTED_DATE: String,
    val DESCRIPTION: String,
    val APPROVAL_STATUS: Char,
    val status: Int
)
