package com.example.medicoapplication.UI.common.validations

import com.example.medicoapplication.UI.common.validations.campos.PasswordValidator

/**
 * Validates the "reset / change password" form used in:
 *   - ResetPasswordActivity
 *   - AlterarSenhaPacienteActivity (dialog)
 */
object ResetPasswordValidator {

    fun validar(novaSenha: String, confirmacao: String): ValidationResult<Unit> {

        if (!PasswordValidator.isValid(novaSenha)) {
            return ValidationResult.Error(
                ValidationField.SENHA,
                "A senha deve ter pelo menos 8 caracteres."
            )
        }

        if (novaSenha != confirmacao) {
            return ValidationResult.Error(
                ValidationField.SENHA_CONFIRMACAO,
                "As senhas não coincidem."
            )
        }

        return ValidationResult.Success(Unit)
    }
}
