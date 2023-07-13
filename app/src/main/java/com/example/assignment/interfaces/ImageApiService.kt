package com.example.assignment.interfaces

import com.example.assignment.models.ImageModel
import retrofit2.http.GET

interface ImageApiService {
    @GET("v2/list")
    suspend fun getImages(): List<ImageModel>
}