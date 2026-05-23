package com.example.medicoapplication.data.remote.DTO.agenda

data class AgendaRequestDTO(
    val diaSemana: DiaSemana,
    val horaInicio: String,   // format: "HH:mm"
    val horaFim: String       // format: "HH:mm"
)
