package com.example.medicoapplication.data.remote.DTO.consulta

data class ConsultaUpdateRequestDto(
    val pacienteId: Long,
    val consultaOfertadaId: Long,
    val status: StatusConsulta,
    val observacoes: String?
)
