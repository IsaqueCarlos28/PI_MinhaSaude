package com.example.medicoapplication.UI.common.mappers

import com.example.medicoapplication.UI.common.formatters.CpfFormatter
import com.example.medicoapplication.UI.common.formatters.DateFormatter
import com.example.medicoapplication.UI.common.formatters.NameFormatter
import com.example.medicoapplication.UI.common.formatters.PhoneFormatter
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteEditRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteResponseDto

/**
 * Handles field formatting for profile screens:
 *   - EditarPerfilPacienteActivity  (UI → API + API → pre-fill)
 *   - PerfilPacienteActivity        (API → display)
 */
object PerfilMapper {

    /**
     * Converts raw UI form values to a [PacienteEditRequestDto] ready to send to the API.
     * Strips CPF/phone masks and converts the date to API format.
     */
    fun uiToApi(
        nome: String,
        email: String,
        cpf: String,
        telefone: String,
        dataNascimento: String,
        genero: String
    ): PacienteEditRequestDto {
        return PacienteEditRequestDto(
            nome           = NameFormatter.normalize(nome),
            email          = email.trim(),
            cpf            = CpfFormatter.uiToApiCpf(cpf),
            telefone       = PhoneFormatter.uiToApiPhone(telefone),
            dataNascimento = DateFormatter.uiToApiDate(dataNascimento) ?: dataNascimento,
            genero         = GeneroMapper.fromString(genero)
        )
    }

    /**
     * Formats API response fields for display in PerfilPacienteActivity.
     * Returns a [DisplayPaciente] with all strings ready to set on TextViews.
     */
    fun apiToDisplay(dto: PacienteResponseDto): DisplayPaciente {
        return DisplayPaciente(
            nome           = dto.nome?.let { NameFormatter.normalize(it) } ?: "—",
            email          = dto.email          ?: "—",
            cpf            = dto.cpf?.let  { CpfFormatter.apiToUiCpf(it) }   ?: "—",
            telefone       = dto.telefone?.let { PhoneFormatter.apiToUiPhone(it) } ?: "—",
            dataNascimento = dto.dataNascimento?.let { DateFormatter.apiToUiDate(it) } ?: "—",
            genero         = dto.genero?.let { GeneroMapper.toDisplayString(it) } ?: "—"
        )
    }

    /**
     * Pre-fills the edit form with existing API data (API format → UI format).
     */
    fun apiToFormPrefill(dto: PacienteResponseDto): FormPrefill {
        return FormPrefill(
            nome           = dto.nome           ?: "",
            email          = dto.email          ?: "",
            cpf            = dto.cpf?.let  { CpfFormatter.apiToUiCpf(it) }   ?: "",
            telefone       = dto.telefone?.let { PhoneFormatter.apiToUiPhone(it) } ?: "",
            dataNascimento = dto.dataNascimento?.let { DateFormatter.apiToUiDate(it) } ?: "",
            genero         = dto.genero ?: com.example.medicoapplication.data.remote.DTO.Genero.OUTRO
        )
    }

    data class DisplayPaciente(
        val nome: String,
        val email: String,
        val cpf: String,
        val telefone: String,
        val dataNascimento: String,
        val genero: String
    )

    data class FormPrefill(
        val nome: String,
        val email: String,
        val cpf: String,
        val telefone: String,
        val dataNascimento: String,
        val genero: com.example.medicoapplication.data.remote.DTO.Genero
    )
}
