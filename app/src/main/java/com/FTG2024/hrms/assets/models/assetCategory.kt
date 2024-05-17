package com.example.myapplication.models

import com.FTG2024.hrms.assets.models.Data

data class assetCategory(
    val code: Int,
    val count: Int,
    val `data`: List<Data>,
    val message: String,
    val pages: Int
)

/*
data class Data(
    val CREATED_MODIFIED_DATE: String,
    val ID: Int,
    val NAME: String,
    val STATUS: Int
)*/
