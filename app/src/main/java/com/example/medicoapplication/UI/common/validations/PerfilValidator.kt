package com.example.medicoapplication.UI.common.validations

import com.example.medicoapplication.UI.common.formatters.DateFormatter
import com.example.medicoapplication.UI.common.validations.campos.CpfValidator
import com.example.medicoapplication.UI.common.validations.campos.EmailValidator
import com.example.medicoapplication.UI.common.validations.campos.NameValidator
import com.example.medicoapplication.UI.common.validations.campos.PhoneValidator
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteEditRequestDto

/**
 * Validates the profile editing form used in:
 *   - EditarPerfilPacienteActivity
 */
object PerfilValidator {

    fun validarEdicaoPaciente(dto: PacienteEditRequestDto): ValidationResult<Unit> {

        if (!NameValidator.isValid(dto.nome)) {
            return ValidationResult.Error(
                ValidationField.NOME,
                "Informe um nome válido."
            )
        }

        if (!EmailValidator.isValid(dto.email)) {
            return ValidationResult.Error(
                ValidationField.EMAIL,
                "E-mail inválido."
            )
        }

        if (dto.cpf.isNotBlank() && !CpfValidator.isValid(dto.cpf)) {
            return ValidationResult.Error(
                ValidationField.CPF,
                "CPF inválido. Deve conter 11 dígitos numéricos."
            )
        }

        if (dto.telefone.isNotBlank() && !PhoneValidator.isValid(dto.telefone)) {
            return ValidationResult.Error(
                ValidationField.TELEFONE,
                "Telefone inválido."
            )
        }

        if (dto.dataNascimento.isNotBlank()) {
            // dto.dataNascimento is already in API/ISO format (yyyy-MM-dd) because
            // PerfilMapper.uiToApi() converts it before validation is called.
            // We accept both ISO (yyyy-MM-dd) and UI (dd/MM/yyyy) formats here.
            val parsedDate = DateFormatter.uiToLocalDate(dto.dataNascimento)
                ?: DateFormatter.apiToLocalDate(dto.dataNascimento)
                ?: return ValidationResult.Error(
                    ValidationField.DATA_NASCIMENTO,
                    "Data inválida. Use o formato DD/MM/AAAA."
                )
        }

        return ValidationResult.Success(Unit)
    }
}