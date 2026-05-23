package com.example.medicoapplication.data.remote.DTO.medico

data class MedicoCreateRequestDto(
    val nome: String,
    val crm: String,
    val email: String,
    val telefone: String,
    val senha: String,
    val especialidade: String,
    val uf: String   // ex: "SP"
)
