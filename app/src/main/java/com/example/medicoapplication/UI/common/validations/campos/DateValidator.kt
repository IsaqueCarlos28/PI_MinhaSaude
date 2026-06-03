package com.example.medicoapplication.UI.common.validations.campos

import java.time.LocalDate

object DateValidator {

    fun isPasssado(date: LocalDate): Boolean{
        return date.isBefore(LocalDate.now())
    }

    fun isMaiorDeIdade(date: LocalDate): Boolean{
        return date.plusYears(18) <= LocalDate.now()
    }
}