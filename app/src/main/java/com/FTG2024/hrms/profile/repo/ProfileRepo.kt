package com.FTG2024.hrms.profile.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.FTG2024.hrms.uidata.Event
import com.FTG2024.hrms.uidata.Response
import okhttp3.MultipartBody

class ProfileRepo(private val apiService:ProfileServiceApi) {
    private var _imageUploadedMutableLiveData: MutableLiveData<Event<Response>> = MutableLiveData()
    val imageUploadedMutableLiveData : LiveData<Event<Response>>
        get() = _imageUploadedMutableLiveData

    suspend fun getProfileImage() {
        val response = apiService.getProfilePic()
    }

    suspend fun getProfileDetails() {
       /* val response = apiService.getProfileDetails()
        Log.d("####", "getProfileDetails: $response")
        if (response.isSuccessful) {
            val profileDetailsResponse = response.body()
            Log.d("####", "getProfileDetails: $profileDetailsResponse")
            if (profileDetailsResponse!!.code == 200) {
                _imageUploadedMutableLiveData.postValue(Event(Response.Success(profileDetailsResponse)))
            } else {
                val msg = "Failed to Upload " + profileDetailsResponse.message
                _imageUploadedMutableLiveData.postValue(Event(Response.Exception(msg)))
            }
        } else {
            Log.d("####", "uploadSelfie: ${response.body()}")
            _imageUploadedMutableLiveData.postValue(Event(Response.Exception("Failed to Upload")))
        }*/
    }

    suspend fun setProfileImage(image: MultipartBody.Part) {
        val response = apiService.uploadProfileImage(image)
        Log.d("####", "getProfileImage: $response")
        if (response.isSuccessful) {
            val uploadImageResponse = response.body()
            Log.d("####", "getProfileImage: $uploadImageResponse")
            if (uploadImageResponse!!.code == 200) {
                _imageUploadedMutableLiveData.postValue(Event(Response.Success(uploadImageResponse)))
            } else {
                val msg = "Failed to Upload " + uploadImageResponse.message
                _imageUploadedMutableLiveData.postValue(Event(Response.Exception(msg)))
            }
        } else {
            Log.d("####", "uploadSelfie: ${response.body()}")
            _imageUploadedMutableLiveData.postValue(Event(Response.Exception("Failed to Upload")))
        }
    }
}