package com.example.medicoapplication.data.remote.DTO.local

import com.google.gson.annotations.SerializedName

data class EmbeddedLocaisDto(
    @SerializedName("localResponseDTOList")
    val localResponseDTOList: List<LocalResponseDto>
)
