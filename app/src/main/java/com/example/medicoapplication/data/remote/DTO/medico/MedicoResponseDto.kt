package com.example.medicoapplication.data.remote.DTO.medico

data class MedicoResponseDto(
    val id: Long,
    val nome: String,
    val crm: String,
    val especialidade: String,
    val email: String,
    val telefone: String
)
