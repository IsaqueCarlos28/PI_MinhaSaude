package com.example.medicoapplication.data.remote.DTO.paciente

import com.example.medicoapplication.data.remote.DTO.Genero
import java.time.LocalDate

data class PacienteCreateRequestDto(
    val nome: String,
    val cpf: String,
    val email: String,
    val telefone: String,
    val genero: Genero,
    val dataNascimento: LocalDate,
    val senha: String

)
