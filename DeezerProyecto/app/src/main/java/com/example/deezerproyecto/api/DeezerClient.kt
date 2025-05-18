package com.example.deezerproyecto.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DeezerClient {
    private const val BASE_URL = "https://api.deezer.com/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}