package com.example.medicoapplication.data.remote.DTO.consulta

data class ConsultaCreateRequestDto(
    val pacienteId: Long,
    val consultaOfertadaId: Long,
    val status: StatusConsulta,
    val observacoes: String?
)
