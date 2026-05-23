package com.example.medicoapplication.data.remote.DTO.consulta

data class ConsultaCreateRequestDto(
    val idConsultaOfertada: Long,
    val idConvenio: Long?,
    val data: String,
    val horaInicio: String
)
