package com.FTG2024.hrms.assets.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.FTG2024.hrms.application.TokenManager
import com.FTG2024.hrms.assets.models.Data
import com.FTG2024.hrms.retrofit.RetrofitHelper
import com.example.myapplication.apiservice.apiInterface
import com.example.myapplication.models.Asset
import com.example.myapplication.models.AssetRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryViewModel() : ViewModel() {

    suspend fun getCategoryDataForSpinner(tokenManager: TokenManager): List<Data>? {
        return withContext(Dispatchers.IO) {
            try {
                val apiService = RetrofitHelper.getRetrofitInstance(tokenManager).create(apiInterface::class.java)
                val response = apiService.getassetCategory()
                if (response.isSuccessful) {
                    Log.d("view model print" ,response.message() )
                    response.body()?.data
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getAssetsDataForSpinner(tokenManager: TokenManager): List<Asset> {
        return withContext(Dispatchers.IO) {
            try {
                val apiService = RetrofitHelper.getRetrofitInstance(tokenManager).create(apiInterface::class.java)
                val response = apiService.getAsset()
                if (response.isSuccessful) {
                    val responseData = response.body()?.data
                    responseData ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "Error fetching asset data: ${e.message}", e)
                emptyList()
            }
        }
    }

    suspend fun addAsset(empId: Int, categoryId: Int, assetId: Int, date: String, requestedDate: String, description: String, tokenManager: TokenManager) {
        viewModelScope.launch {
            try {
                val requestBody = AssetRequestBody(
                    EMP_ID = empId,
                    categoryId = categoryId,
                    ASSET_ID = assetId,
                    DATE = date,
                    REQUESTED_DATE = requestedDate,
                    DESCRIPTION = description,
                    APPROVAL_STATUS = 'n',
                    status = 1
                )

                val apiService = RetrofitHelper.getRetrofitInstance(tokenManager).create(apiInterface::class.java)
                val response = apiService.getassetCategory()

                if (response.isSuccessful) {
                    Log.d("CategoryViewModel", "Asset added successfully")
                } else {
                    Log.e("CategoryViewModel", "Failed to add asset")
                }
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "Network Error: ${e.message}", e)
            }
        }
    }

}

