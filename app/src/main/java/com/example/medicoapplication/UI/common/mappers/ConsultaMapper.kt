package com.example.medicoapplication.UI.common.mappers

import com.example.medicoapplication.UI.common.formatters.DateFormatter
import com.example.medicoapplication.UI.common.formatters.TimeFormatter
import com.example.medicoapplication.data.remote.DTO.StatusConsulta
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto

/**
 * Maps [ConsultaResponseDto] fields to display-ready strings.
 *
 * Used in:
 *   - DetalheConsultaActivity
 *   - MinhasConsultasActivity (item rows)
 *   - HomePacienteActivity (upcoming list)
 */
object ConsultaMapper {

    data class DisplayConsulta(
        val nomeMedico: String,
        val nomePaciente: String,
        val data: String,
        val horaInicio: String,
        val horaFim: String,
        val convenio: String,
        val status: String,
        val statusEnum: StatusConsulta
    )

    fun toDisplay(dto: ConsultaResponseDto): DisplayConsulta {
        return DisplayConsulta(
            nomeMedico  = dto.nomeMedico  ?: "—",
            nomePaciente = dto.nomePaciente ?: "—",
            data        = DateFormatter.apiToUiDate(dto.data),
            horaInicio  = TimeFormatter.apiToUiTime(dto.horaInicio),
            horaFim     = dto.horaFim?.let { TimeFormatter.apiToUiTime(it) } ?: "—",
            convenio    = dto.nomeConvenio ?: "Particular",
            status      = traduzirStatus(dto.status),
            statusEnum  = dto.status
        )
    }

    fun traduzirStatus(status: StatusConsulta): String = when (status) {
        StatusConsulta.AGENDADA  -> "Agendada"
        StatusConsulta.CANCELADA -> "Cancelada"
        StatusConsulta.REALIZADA -> "Realizada"
    }
}