package com.example.medicoapplication.UI.common.validations.campos

object CrmValidator {
    fun isValid(crm: String): Boolean {
        return crm.length == 6 &&
                crm.all(Char::isDigit)
    }
}