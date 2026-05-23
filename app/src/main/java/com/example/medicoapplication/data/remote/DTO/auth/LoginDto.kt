package com.example.medicoapplication.data.remote.DTO.auth

// =====================================================================
// DTOs para autenticação do paciente.
// Endpoints:
//   POST /auth/login
// =====================================================================

/** Corpo da requisição de login (LoginRequestDTO.java). */
data class LoginRequestDto(
    val email: String,
    val senha: String
)

/** Resposta da API após login (LoginResponseDTO.java). */
data class LoginResponseDto(
    val id: Long,
    val email: String,
    val role: String   // "PACIENTE" | "MEDICO" | "ADMIN"
)
