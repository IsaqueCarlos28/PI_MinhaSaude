package com.example.medicoapplication.data.remote.DTO.consulta

data class ConsultaUpdateRequestDto(
    val idConsultaOfertada: Long,
    val idConvenio: Long?,
    val data: String,
    val horaInicio: String
)
