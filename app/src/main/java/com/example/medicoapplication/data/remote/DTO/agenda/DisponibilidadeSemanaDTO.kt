package com.example.medicoapplication.data.remote.DTO.agenda

import java.time.LocalDate

data class DisponibilidadeSemanaDTO(
    val inicioSemana: LocalDate,
    val fimSemana: LocalDate,
    val dias: List<DisponibilidadeDiaDTO>
)
