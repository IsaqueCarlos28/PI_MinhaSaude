package com.example.medicoapplication.UI.common.formatters

object CrmFormatter {
    fun format(
        crmUf: String,
        crmDigits: String
    ): String {
        return "CRM-$crmUf $crmDigits"
    }
}