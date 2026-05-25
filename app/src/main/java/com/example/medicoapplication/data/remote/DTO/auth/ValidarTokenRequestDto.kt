package com.example.medicoapplication.data.remote.DTO.auth

data class ValidarTokenRequestDto(
    val email: String?,
    val code: String    // 6-digit numeric string
)
