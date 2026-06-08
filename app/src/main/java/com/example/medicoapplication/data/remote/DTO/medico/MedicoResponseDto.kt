package com.example.medicoapplication.data.remote.DTO.medico

import com.example.medicoapplication.data.remote.DTO.medicoespecialidade.MedicoEspecialidadeResponseDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteResponseDto

data class MedicoResponseDto(
    val usuario: PacienteResponseDto?,
    val crmUf: String?,      // UF enum string e.g. "SP"
    val crmDigitos: String?,
    val especialidades: List<MedicoEspecialidadeResponseDto>
)
