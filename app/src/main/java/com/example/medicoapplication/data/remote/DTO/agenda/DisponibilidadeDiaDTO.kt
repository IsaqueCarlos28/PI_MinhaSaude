package com.example.medicoapplication.data.remote.DTO.agenda

import java.time.LocalDate
import java.time.LocalTime

data class DisponibilidadeDiaDTO(
    val data: LocalDate,
    val horarios: List<LocalTime>
)
