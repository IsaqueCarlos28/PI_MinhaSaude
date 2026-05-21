package com.example.medicoapplication.data.remote.DTO.consultaofertada

data class ConsultaOfertadaResponseDto(
    val id: Long,
    val medicoId: Long,
    val localId: Long,
    val dataHoraInicio: String,
    val dataHoraFim: String,
    val valor: Double
)
