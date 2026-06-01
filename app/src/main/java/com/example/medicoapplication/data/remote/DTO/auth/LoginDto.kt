package com.example.medicoapplication.data.remote.DTO.auth

// NOTE: LoginRequestDto and LoginResponseDto live in DTO.auth (this file)
// but the canonical versions used by ApiService are in DTO.login package.
// Keep this file only if other code already imports from DTO.auth.
// Otherwise prefer DTO.login.LoginRequestDto and DTO.login.LoginResponseDto.

import com.example.medicoapplication.data.remote.DTO.auth.Role

/** Corpo da requisição de login. */
data class LoginRequestDto(
    val email: String,
    val senha: String
)

/** Resposta da API após login.
 *  FIX: role was String — must be typed Role enum to match DTO.login.LoginResponseDto
 *  and so that LoginActivity's enum comparison compiles. */
data class LoginResponseDto(
    val id: Long,
    val email: String,
    val role: Role
)
