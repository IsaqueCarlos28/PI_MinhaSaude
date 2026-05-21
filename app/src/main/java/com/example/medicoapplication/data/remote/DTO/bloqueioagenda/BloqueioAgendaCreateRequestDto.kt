package com.example.medicoapplication.data.remote.DTO.bloqueioagenda

data class BloqueioAgendaCreateRequestDto(
    val dataInicio: String,
    val dataFim: String,
    val motivo: String,
    val medicoId: Long
)
