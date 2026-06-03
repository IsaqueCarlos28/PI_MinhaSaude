package com.example.medicoapplication.UI.common.validations.campos

object PhoneValidator {

    fun isValid(phone: String): Boolean {

        val digits = phone.filter(Char::isDigit)

        return digits.length in 10..11
    }
}