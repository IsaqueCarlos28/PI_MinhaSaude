package com.example.medicoapplication.UI.common.validations


import com.example.medicoapplication.UI.common.formatters.DateFormatter
import com.example.medicoapplication.UI.common.validations.campos.CpfValidator
import com.example.medicoapplication.UI.common.validations.campos.DateValidator
import com.example.medicoapplication.UI.common.validations.campos.EmailValidator
import com.example.medicoapplication.UI.common.validations.campos.NameValidator
import com.example.medicoapplication.UI.common.validations.campos.PasswordValidator
import com.example.medicoapplication.UI.common.validations.campos.PhoneValidator
import com.example.medicoapplication.data.remote.DTO.medico.MedicoCreateRequestDto

object CadastroValidator {

    fun validarMedico(
        dto: MedicoCreateRequestDto
    ): ValidationResult<Unit> {

        val usuario = dto.usuarioBase

        if (!NameValidator.isValid(usuario.nome)) {
            return ValidationResult.Error(
                ValidationField.NOME,
                "Informe um nome válido."
            )
        }

        if (!EmailValidator.isValid(usuario.email)) {
            return ValidationResult.Error(
                ValidationField.EMAIL,
                "E-mail inválido."
            )
        }

        if (!CpfValidator.isValid(usuario.cpf)) {
            return ValidationResult.Error(
                ValidationField.CPF,
                "CPF inválido."
            )
        }

        if (!PhoneValidator.isValid(usuario.telefone)) {
            return ValidationResult.Error(
                ValidationField.TELEFONE,
                "Telefone inválido."
            )
        }

        val data =
            DateFormatter.uiToLocalDate(
                usuario.dataNascimento
            )
                ?: return ValidationResult.Error(
                    ValidationField.DATA_NASCIMENTO,
                    "Data inválida."
                )

        if (!DateValidator.isMaiorDeIdade(data)) {
            return ValidationResult.Error(
                ValidationField.DATA_NASCIMENTO,
                "É necessário ser maior de idade."
            )
        }

        if (!PasswordValidator.isValid(usuario.senha)) {
            return ValidationResult.Error(
                ValidationField.SENHA,
                "A senha deve possuir pelo menos 6 caracteres."
            )
        }

        if (
                dto.crmDigits.length != 6 ||
                !dto.crmDigits.all(Char::isDigit)
            ) {
            return ValidationResult.Error(
                ValidationField.CRM,
                "CRM inválido."
            )
        }

        return ValidationResult.Success(Unit)
    }
}