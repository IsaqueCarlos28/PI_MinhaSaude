package com.example.medicoapplication.UI.common.validations.campos

import android.util.Patterns

object EmailValidator {
    fun isValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS
            .matcher(email)
            .matches()
    }
}