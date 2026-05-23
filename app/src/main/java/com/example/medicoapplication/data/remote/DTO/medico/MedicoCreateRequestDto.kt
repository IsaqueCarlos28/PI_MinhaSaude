package com.example.medicoapplication.data.remote.DTO.medico

import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto

data class MedicoCreateRequestDto(
    val usuarioBase: PacienteCreateRequestDto,
    val crmUf: String,      // JSON key is "crmUf" — UF enum string e.g. "SP"
    val crmDigits: String   // exactly 6 digits
)
