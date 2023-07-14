package com.example.assignment.repo

import android.util.Log
import com.example.assignment.interfaces.ImageApiService
import com.example.assignment.models.ImageModel
import com.example.assignment.singletons.NetworkUtils
import com.example.assignment.singletons.RetrofitService

class ImageRepository {

    private val imageApi = RetrofitService.retrofit.create(ImageApiService::class.java)

    suspend fun getImages(): List<ImageModel> {
        return try {
            imageApi.getImages()
        } catch (exception: Exception) {
            NetworkUtils.networkStatus.postValue(false)
            Log.e("Reposs","No Internet")
            emptyList()
        }
    }
}


