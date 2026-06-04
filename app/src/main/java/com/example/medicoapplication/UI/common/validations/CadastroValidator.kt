package com.example.medicoapplication.UI.common.validations

import com.example.medicoapplication.UI.common.formatters.DateFormatter
import com.example.medicoapplication.UI.common.validations.campos.CpfValidator
import com.example.medicoapplication.UI.common.validations.campos.CrmValidator
import com.example.medicoapplication.UI.common.validations.campos.DateValidator
import com.example.medicoapplication.UI.common.validations.campos.EmailValidator
import com.example.medicoapplication.UI.common.validations.campos.NameValidator
import com.example.medicoapplication.UI.common.validations.campos.PasswordValidator
import com.example.medicoapplication.UI.common.validations.campos.PhoneValidator
import com.example.medicoapplication.data.remote.DTO.medico.MedicoCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto

object CadastroValidator {

    fun validarMedico(dto: MedicoCreateRequestDto): ValidationResult<Unit> {
        val usuarioValidation = validarUsuario(dto.usuarioBase)
        if (usuarioValidation is ValidationResult.Error) return usuarioValidation

        // BUG FIX: original had `if (CrmValidator.isValid(...))` — the condition was inverted.
        if (!CrmValidator.isValid(dto.crmDigits)) {
            return ValidationResult.Error(
                ValidationField.CRM,
                "CRM inválido. Deve conter exatamente 6 dígitos numéricos."
            )
        }

        return ValidationResult.Success(Unit)
    }

    /** Used by CadastroPacienteActivity — validates base user fields + password confirmation. */
    fun validarPaciente(
        dto: PacienteCreateRequestDto,
        confirmacaoSenha: String
    ): ValidationResult<Unit> {
        val usuarioValidation = validarUsuario(dto)
        if (usuarioValidation is ValidationResult.Error) return usuarioValidation

        if (dto.senha != confirmacaoSenha) {
            return ValidationResult.Error(
                ValidationField.SENHA_CONFIRMACAO,
                "As senhas não coincidem."
            )
        }

        return ValidationResult.Success(Unit)
    }

    fun validarUsuario(dto: PacienteCreateRequestDto): ValidationResult<Unit> {

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

        if (!CpfValidator.isValid(dto.cpf)) {
            return ValidationResult.Error(
                ValidationField.CPF,
                "CPF inválido. Deve conter 11 dígitos numéricos."
            )
        }

        if (!PhoneValidator.isValid(dto.telefone)) {
            return ValidationResult.Error(
                ValidationField.TELEFONE,
                "Telefone inválido."
            )
        }

        val data = DateFormatter.uiToLocalDate(dto.dataNascimento)
            ?: return ValidationResult.Error(
                ValidationField.DATA_NASCIMENTO,
                "Data inválida. Use o formato DD/MM/AAAA."
            )

        if (!DateValidator.isMaiorDeIdade(data)) {
            return ValidationResult.Error(
                ValidationField.DATA_NASCIMENTO,
                "É necessário ser maior de 18 anos."
            )
        }

        if (!PasswordValidator.isValid(dto.senha)) {
            return ValidationResult.Error(
                ValidationField.SENHA,
                "A senha deve ter pelo menos 8 caracteres."
            )
        }

        return ValidationResult.Success(Unit)
    }
}
