package com.example.medicoapplication.data.remote.DTO.consultaofertada

data class ConsultaOfertadaUpdateRequestDto(
    val medicoId: Long,
    val localId: Long,
    val dataHoraInicio: String,
    val dataHoraFim: String,
    val valor: Double
)
