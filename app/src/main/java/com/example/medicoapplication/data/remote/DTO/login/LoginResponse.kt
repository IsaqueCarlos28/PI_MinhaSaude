package com.example.medicoapplication.data.remote.DTO.login

data class LoginResponseDto(
    val id: Long,
    val email: String,
    val role: Role
)
