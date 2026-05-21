package com.example.medicoapplication.data.remote.DTO.paciente

import com.example.medicoapplication.data.remote.DTO.PageDto

data class PacientePageResponseDto(
    val _embedded: EmbeddedPacientesDto,
    val page: PageDto
)
