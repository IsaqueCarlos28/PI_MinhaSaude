package com.example.medicoapplication.UI.common.validations.campos

object CpfValidator {

    fun isValid(cpf: String): Boolean {

        val digits = cpf.filter(Char::isDigit)

        return digits.length == 11
    }
}