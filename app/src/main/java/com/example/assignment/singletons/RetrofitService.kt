package com.example.assignment.singletons

import com.example.assignment.interfaces.ImageApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    private const val BASE_URL = "https://picsum.photos/"
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val imageApi: ImageApiService by lazy {
        retrofit.create(ImageApiService::class.java)
    }
}
