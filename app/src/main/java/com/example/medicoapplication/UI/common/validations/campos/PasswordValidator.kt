package com.example.medicoapplication.UI.common.validations.campos

object PasswordValidator {

    private const val MIN_LENGTH = 8

    fun isValid(password: String): Boolean {
        return password.length >= MIN_LENGTH
    }

    /** Checks that two passwords match AND that the new one is valid. */
    fun confirmacaoValida(senha: String, confirmacao: String): Boolean {
        return senha == confirmacao && isValid(senha)
    }
}
