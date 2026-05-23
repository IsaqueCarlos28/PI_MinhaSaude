package com.example.medicoapplication.data.remote.DTO.agenda

data class AgendaResponseDto(
    val id: Long,
    val diaSemana: DiaSemana,
    val horaInicio: String,   // format: "HH:mm"
    val horaFim: String       // format: "HH:mm"
)
