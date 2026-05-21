package com.example.medicoapplication.data.remote.DTO.especialidades

import com.example.medicoapplication.data.remote.DTO.PageDto

data class EspecialidadePageResponseDto(
    val _embedded: EmbeddedEspecialidadesDto,
    val page: PageDto
)
