package com.example.medicoapplication.data.remote.DTO.agenda

data class DisponibilidadeSemanaDTO(
    val inicioSemana: String,
    val fimSemana: String,
    val dias: List<DisponibilidadeDiaDTO>
)
