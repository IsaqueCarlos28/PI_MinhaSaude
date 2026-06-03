package com.example.medicoapplication.UI.common.validations

abstract class BaseValidator {

    protected fun error(
        field: ValidationField,
        message: String
    ): ValidationResult.Error {
        return ValidationResult.Error(field, message)
    }
}