package com.example.medicoapplication.data.remote.DTO.bloqueioagenda

import com.example.medicoapplication.data.remote.DTO.PageDto

data class BloqueioAgendaPageResponseDto(
    val _embedded: EmbeddedBloqueiosAgendaDto,
    val page: PageDto
)
