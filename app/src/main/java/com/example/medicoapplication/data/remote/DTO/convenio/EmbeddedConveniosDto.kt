package com.example.medicoapplication.data.remote.DTO.convenio

import com.google.gson.annotations.SerializedName

data class EmbeddedConveniosDto(
    @SerializedName("convenioResponseDTOList")
    val convenioResponseDTOList: List<ConvenioResponseDto>
)
