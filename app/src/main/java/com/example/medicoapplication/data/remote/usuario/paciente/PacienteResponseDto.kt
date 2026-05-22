package com.example.medicoapplication.data.remote.usuario.paciente

import com.example.medicoapplication.data.remote.DTO.Genero
import java.time.LocalDate

data class PacienteResponseDto(
    val id: Long,
    val nome: String,
    val cpf: String,
    val email: String,
    val telefone: String,
    val genero: Genero,
    val dataNascimento: LocalDate
)
