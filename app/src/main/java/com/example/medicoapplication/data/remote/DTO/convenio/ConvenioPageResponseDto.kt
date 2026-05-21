package com.example.medicoapplication.data.remote.DTO.convenio

import com.example.medicoapplication.data.remote.DTO.PageDto

data class ConvenioPageResponseDto(
    val _embedded: EmbeddedConveniosDto,
    val page: PageDto
)
