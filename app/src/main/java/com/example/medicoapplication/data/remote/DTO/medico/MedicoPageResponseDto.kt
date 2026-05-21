package com.example.medicoapplication.data.remote.DTO.medico

import com.example.medicoapplication.data.remote.DTO.PageDto

data class MedicoPageResponseDto(
    val embedded: EmbeddedMedicosDto,
    val page: PageDto
)
