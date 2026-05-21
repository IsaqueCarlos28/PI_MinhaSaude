package com.example.medicoapplication.data.remote.DTO.bloqueioagenda

data class BloqueioAgendaUpdateRequestDto(
    val dataInicio: String,
    val dataFim: String,
    val motivo: String,
    val medicoId: Long
)
