package com.example.medicoapplication.data.remote

import com.example.medicoapplication.data.remote.DTO.login.LoginRequestDto
import com.example.medicoapplication.data.remote.DTO.login.LoginResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<LoginResponseDto>
}