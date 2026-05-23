package com.example.medicoapplication.data.remote.DTO.medicoespecialidade

import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeResponseDto

data class MedicoEspecialidadeResponseDto(
    val id: Long,
    val especialidade: EspecialidadeResponseDto?,
    val rqe: String?
)
