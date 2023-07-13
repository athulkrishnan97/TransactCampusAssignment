package com.example.assignment.repo

import com.example.assignment.interfaces.ImageApiService
import com.example.assignment.models.ImageModel
import com.example.assignment.singletons.RetrofitService

class ImageRepository {

    private val imageApi = RetrofitService.retrofit.create(ImageApiService::class.java)

    suspend fun getImages(): List<ImageModel> {
        return try {
            imageApi.getImages()
        } catch (exception: Exception) {
            // Handle exceptions like no internet connection here.
            // Return an empty list as a fallback.
            emptyList()
        }
    }
}


