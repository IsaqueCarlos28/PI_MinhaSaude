package com.example.medicoapplication.data.remote.DTO.medico

import com.example.medicoapplication.data.remote.DTO.paciente.PacienteEditRequestDto

data class MedicoEditRequestDto(
    val usuarioBase: PacienteEditRequestDto,
    val uf: String,     // UF enum, e.g. "SP"
    val digits: String  // exactly 6 digits
)
