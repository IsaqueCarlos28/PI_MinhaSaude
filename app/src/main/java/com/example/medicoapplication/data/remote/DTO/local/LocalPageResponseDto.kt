package com.example.medicoapplication.data.remote.DTO.local

import com.example.medicoapplication.data.remote.DTO.PageDto

data class LocalPageResponseDto(
    val _embedded: EmbeddedLocaisDto,
    val page: PageDto
)
