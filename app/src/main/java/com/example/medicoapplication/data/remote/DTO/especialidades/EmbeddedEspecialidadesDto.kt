package com.example.medicoapplication.data.remote.DTO.especialidades

import com.google.gson.annotations.SerializedName

data class EmbeddedEspecialidadesDto(
    @SerializedName("especialidadeResponseDTOList")
    val especialidadeResponseDTOList: List<EspecialidadeResponseDto>
)
