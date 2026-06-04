package com.example.medicoapplication.UI.common.mappers

import com.example.medicoapplication.UI.common.formatters.CrmFormatter
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto

/**
 * Maps [MedicoResponseDto] to display-ready strings for public/patient-facing screens.
 *
 * Used in:
 *   - PerfilMedicoPublicoActivity
 *   - AgendarConsultaActivity (header summary)
 *   - PerfilMedicoActivity (doctor's own profile)
 */
object MedicoMapper {

    data class DisplayMedico(
        val nome: String,
        val especialidade: String,
        val crm: String,
        val email: String,
        val telefone: String
    )

    /** Summary used in AgendarConsultaActivity header (minimal fields). */
    data class AgendamentoInfo(
        val nome: String,
        val especialidade: String,
        val crm: String
    )

    fun toDisplay(dto: MedicoResponseDto): DisplayMedico {
        val nome = dto.usuario?.nome ?: "—"
        val especialidade = dto.especialidades
            .mapNotNull { it.especialidade?.nome }
            .joinToString(", ")
            .ifBlank { "—" }
        val crm = if (!dto.crmDigitos.isNullOrBlank() && !dto.crmUf.isNullOrBlank())
            CrmFormatter.format(dto.crmUf, dto.crmDigitos)
        else "—"

        return DisplayMedico(
            nome          = nome,
            especialidade = especialidade,
            crm           = crm,
            email         = dto.usuario?.email    ?: "—",
            telefone      = dto.usuario?.telefone ?: "—"
        )
    }

    fun toAgendamentoInfo(dto: MedicoResponseDto): AgendamentoInfo {
        val display = toDisplay(dto)
        return AgendamentoInfo(
            nome          = display.nome,
            especialidade = display.especialidade,
            crm           = display.crm
        )
    }
}
