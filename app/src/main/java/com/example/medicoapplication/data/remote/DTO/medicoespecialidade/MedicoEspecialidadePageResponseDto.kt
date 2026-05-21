package com.example.medicoapplication.data.remote.DTO.medicoespecialidade

import com.example.medicoapplication.data.remote.DTO.PageDto

data class MedicoEspecialidadePageResponseDto(
    val _embedded: EmbeddedMedicoEspecialidadesDto,
    val page: PageDto
)
