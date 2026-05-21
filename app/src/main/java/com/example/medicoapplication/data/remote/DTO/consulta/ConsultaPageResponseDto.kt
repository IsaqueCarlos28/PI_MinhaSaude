package com.example.medicoapplication.data.remote.DTO.consulta

import com.example.medicoapplication.data.remote.DTO.PageDto

data class ConsultaPageResponseDto(
    val _embedded: EmbeddedConsultasDto,
    val page: PageDto
)
