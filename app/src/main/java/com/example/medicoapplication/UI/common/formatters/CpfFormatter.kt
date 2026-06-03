package com.example.medicoapplication.UI.common.formatters

object CpfFormatter {
    private val CPF_PATTERN =
        Regex("(\\d{3})(\\d{3})(\\d{3})(\\d{2})")

    fun apiToUiCpf(cpfApi: String): String {

        val digits =
            cpfApi.filter(Char::isDigit)

        return if (digits.length == 11) {
            digits.replace(
                CPF_PATTERN,
                "$1.$2.$3-$4"
            )
        } else {
            cpfApi
        }
    }

    fun uiToApiCpf(cpfUi: String): String {
        return cpfUi.filter(Char::isDigit)
    }
}