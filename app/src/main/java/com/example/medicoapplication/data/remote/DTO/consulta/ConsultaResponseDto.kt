package com.example.medicoapplication.data.remote.DTO.consulta

data class ConsultaResponseDto(
    val id: Long,
    val pacienteId: Long,
    val consultaOfertadaId: Long,
    val status: StatusConsulta,
    val observacoes: String?
)
