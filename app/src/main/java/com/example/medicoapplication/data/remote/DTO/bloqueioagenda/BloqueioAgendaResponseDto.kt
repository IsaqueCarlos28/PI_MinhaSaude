package com.example.medicoapplication.data.remote.DTO.bloqueioagenda

data class BloqueioAgendaResponseDto(
    val id: Long,
    val dataInicio: String,
    val dataFim: String,
    val motivo: String,
    val medicoId: Long
)
