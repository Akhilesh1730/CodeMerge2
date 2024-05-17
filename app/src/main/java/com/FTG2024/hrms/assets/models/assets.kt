package com.example.myapplication.models

data class assets(
    val code: Int,
    val count: Int,
    val `data`: List<Asset>,
    val message: String,
    val pages: Int
)



data class Asset(
    val ASSET_NAME: String,
    val CATEGORY_ID: Int,
    val CREATED_MODIFIED_DATE: String,
    val DESCRIPTION: String,
    val ID: Int,
    val QTY: Int,
    val STATUS: Int
)