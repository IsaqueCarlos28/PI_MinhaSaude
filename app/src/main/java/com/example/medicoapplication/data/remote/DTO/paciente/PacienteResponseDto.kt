package com.example.medicoapplication.data.remote.DTO.paciente

data class PacienteResponseDto(
    val id: Long,
    val nome: String,
    val cpf: String,
    val email: String,
    val telefone: String,
    val genero: String,
    val dataNascimento: String
)
