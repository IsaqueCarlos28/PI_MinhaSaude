package com.example.medicoapplication.data.remote.DTO.paciente

// DTO para detalhes do paciente (GET /pacientes/{id})
data class PacienteDto(
    val id: Long,
    val nome: String,
    val cpf: String,
    val email: String,
    val telefone: String,
    val genero: String,
    val dataNascimento: String
)
