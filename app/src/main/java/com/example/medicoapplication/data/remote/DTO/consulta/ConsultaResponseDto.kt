package com.example.medicoapplication.data.remote.DTO.consulta

data class ConsultaResponseDto(
    val id: Long,
    val idPaciente: Long,
    val nomePaciente: String?,
    val idConsultaOfertada: Long,
    val idUsuarioMedico: Long,
    val nomeMedico: String?,
    val idConvenio: Long?,
    val nomeConvenio: String?,
    val data: String,
    val horaInicio: String,
    val horaFim: String?,
    val status: StatusConsulta
)
