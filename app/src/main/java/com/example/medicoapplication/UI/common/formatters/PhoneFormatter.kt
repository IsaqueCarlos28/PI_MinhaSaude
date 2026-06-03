package com.example.medicoapplication.UI.common.formatters

object PhoneFormatter {
    private val PHONE_PATTERN =
        Regex("(\\d{2})(\\d{5})(\\d{4})")

    fun apiToUiPhone(phoneApi: String): String {

        val digits =
            phoneApi.filter(Char::isDigit)

        return if (digits.length == 11) {
            digits.replace(
                PHONE_PATTERN,
                "($1) $2-$3"
            )
        } else {
            phoneApi
        }
    }

    fun uiToApiPhone(phoneUi: String): String {
        return phoneUi.filter(Char::isDigit)
    }
}