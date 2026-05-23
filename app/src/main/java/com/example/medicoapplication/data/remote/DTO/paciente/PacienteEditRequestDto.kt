package com.example.medicoapplication.data.remote.DTO.paciente

import com.example.medicoapplication.data.remote.DTO.Genero

data class PacienteEditRequestDto(
    val nome: String,
    val cpf: String,
    val email: String,
    val telefone: String,
    val genero: Genero,
    val dataNascimento: String   // format: "yyyy-MM-dd"
)
