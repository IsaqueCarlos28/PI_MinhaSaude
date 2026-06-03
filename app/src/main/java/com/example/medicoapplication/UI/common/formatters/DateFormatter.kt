package com.example.medicoapplication.UI.common.formatters


import java.time.LocalDate
import java.time.format.DateTimeFormatter



object DateFormatter {
    private val UI_FORMAT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private val API_FORMAT =
        DateTimeFormatter.ISO_LOCAL_DATE

    fun apiToUiDate(apiDate: String): String {
        return try {
            LocalDate.parse(apiDate, API_FORMAT)
                .format(UI_FORMAT)
        } catch (e: Exception) {
            apiDate
        }
    }

    fun uiToApiDate(uiDate: String): String? {
        return try {
            LocalDate.parse(uiDate, UI_FORMAT)
                .format(DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            null
        }
    }

    fun uiToLocalDate(
        uiDate: String
    ): LocalDate? {

        return try {
            LocalDate.parse(uiDate, UI_FORMAT)
        } catch (e: Exception) {
            null
        }
    }
}
