package com.example.medicoapplication.UI.common.validations


sealed class ValidationResult<out T> {

         data class Success<T>(
            val data: T
        ) : ValidationResult<T>()

        data class Error(
            val field: ValidationField,
            val message: String
        ) : ValidationResult<Nothing>()
    }

