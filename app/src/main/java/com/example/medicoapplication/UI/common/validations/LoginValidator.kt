package com.example.medicoapplication.UI.common.validations

import com.example.medicoapplication.UI.common.validations.campos.EmailValidator
import com.example.medicoapplication.UI.common.validations.campos.PasswordValidator

object LoginValidator {

    fun validar(email: String, senha: String): ValidationResult<Unit> {

        if (!EmailValidator.isValid(email)) {
            return ValidationResult.Error(
                ValidationField.EMAIL,
                "Informe um e-mail válido."
            )
        }

        if (senha.isEmpty()) {
            return ValidationResult.Error(
                ValidationField.SENHA,
                "Informe a senha."
            )
        }

        return ValidationResult.Success(Unit)
    }

    /** Used by ForgotPasswordActivity — only email needed. */
    fun validarEmail(email: String): ValidationResult<Unit> {
        if (!EmailValidator.isValid(email)) {
            return ValidationResult.Error(
                ValidationField.EMAIL,
                "Informe um e-mail válido."
            )
        }
        return ValidationResult.Success(Unit)
    }
}
