package com.example.medicoapplication.UI.common.validations.campos

object PasswordValidator {

    fun isValid(password: String): Boolean {
        return password.length >= 8
    }
}