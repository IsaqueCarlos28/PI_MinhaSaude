package com.example.medicoapplication.data.remote

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://minhasaudeapi.onrender.com/"


    // Interceptor para logs completos
    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("API_REQUEST", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Retrofit
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}