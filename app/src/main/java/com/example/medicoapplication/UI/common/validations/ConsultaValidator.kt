package com.example.medicoapplication.UI.common.validations

import com.example.medicoapplication.UI.common.formatters.DateFormatter
import com.example.medicoapplication.UI.common.validations.campos.DateValidator

/**
 * Validates scheduling / rescheduling forms used in:
 *   - AgendarConsultaActivity
 *   - ReagendarConsultaActivity
 *   - ConfirmacaoConsultaActivity
 */
object ConsultaValidator {

    fun validarAgendamento(
        dataApi: String,        // "yyyy-MM-dd" — already converted by DateFormatter
        horario: String?
    ): ValidationResult<Unit> {

        if (dataApi.isBlank()) {
            return ValidationResult.Error(
                ValidationField.DATA_CONSULTA,
                "Selecione uma data para a consulta."
            )
        }

        val localDate = DateFormatter.uiToLocalDate(
            runCatching {
                java.time.LocalDate.parse(dataApi).toString()
            }.getOrElse { dataApi }
        ) ?: java.time.LocalDate.parse(dataApi)

        if (DateValidator.isPasssado(localDate)) {
            return ValidationResult.Error(
                ValidationField.DATA_CONSULTA,
                "A data da consulta não pode ser no passado."
            )
        }

        if (horario.isNullOrBlank()) {
            return ValidationResult.Error(
                ValidationField.HORA_CONSULTA,
                "Selecione um horário para a consulta."
            )
        }

        return ValidationResult.Success(Unit)
    }

    fun validarConfirmacao(
        idConsultaOfertada: Long,
        dataApi: String,
        horario: String
    ): ValidationResult<Unit> {

        if (idConsultaOfertada == -1L) {
            return ValidationResult.Error(
                ValidationField.MEDICO,
                "Consulta ofertada não encontrada. Volte e tente novamente."
            )
        }

        return validarAgendamento(dataApi, horario)
    }
}
