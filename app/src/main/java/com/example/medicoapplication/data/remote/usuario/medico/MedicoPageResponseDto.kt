package com.example.medicoapplication.data.remote.usuario.medico

import com.example.medicoapplication.data.remote.DTO.PageDto

data class MedicoPageResponseDto(
    val embedded: EmbeddedMedicosDto,
    val page: PageDto
)
