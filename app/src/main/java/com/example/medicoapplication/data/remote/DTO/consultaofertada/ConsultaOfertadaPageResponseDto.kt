package com.example.medicoapplication.data.remote.DTO.consultaofertada

import com.example.medicoapplication.data.remote.DTO.PageDto

data class ConsultaOfertadaPageResponseDto(
    val _embedded: EmbeddedConsultasOfertadasDto,
    val page: PageDto
)
