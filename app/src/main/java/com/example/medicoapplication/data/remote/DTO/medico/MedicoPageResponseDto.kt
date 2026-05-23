package com.example.medicoapplication.data.remote.DTO.medico

import com.example.medicoapplication.data.remote.DTO.PageDto

data class MedicoPageResponseDto(
    val _embedded: EmbeddedMedicosDto,
    val page: PageDto
)
