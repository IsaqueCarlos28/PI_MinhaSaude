package com.example.medicoapplication.data.remote.DTO.evento_consulta

// =====================================================================
// DTOs para eventos de consulta do paciente.
// Espelham os objetos da API usados em:
//   GET /pacientes/{idPaciente}/consultas
//   POST /pacientes/{idPaciente}/consultas
//   PUT /pacientes/{idPaciente}/consultas/{idEvento}
//   PATCH /pacientes/{idPaciente}/consultas/{idEvento}/status
// =====================================================================

/** Enum de status da consulta (igual ao StatusEventoConsulta.java da API). */
enum class StatusEventoConsulta {
    AGENDADA,
    CANCELADA,
    REALIZADA
}

/** Resposta da API para uma consulta agendada pelo paciente. */
data class EventoConsultaResponseDto(
    val id: Long,
    val idPaciente: Long,
    val nomePaciente: String,
    val idConsultaOfertada: Long,
    val idMedico: Long,
    val nomeMedico: String,
    val idConvenio: Long?,       // null se consulta particular
    val nomeConvenio: String?,   // null se particular
    val data: String,            // formato "yyyy-MM-dd"
    val horaInicio: String,      // formato "HH:mm:ss"
    val horaFim: String,         // formato "HH:mm:ss"
    val status: StatusEventoConsulta
)

/** Corpo da requisição para agendar ou reagendar consulta. */
data class EventoConsultaRequestDto(
    val idConsultaOfertada: Long,
    val idConvenio: Long?,   // null se particular
    val data: String,        // "yyyy-MM-dd"
    val horaInicio: String   // "HH:mm:ss"
)

/** Corpo da requisição para atualizar status da consulta. */
data class EventoConsultaStatusDto(
    val status: StatusEventoConsulta
)
