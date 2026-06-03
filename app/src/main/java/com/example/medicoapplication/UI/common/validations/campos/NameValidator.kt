package com.example.medicoapplication.UI.common.validations.campos

object NameValidator {
    fun isValid(name: String): Boolean {
        return name.isNotBlank()
    }
}