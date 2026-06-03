package com.example.medicoapplication.UI.common.formatters


import java.time.LocalDate
import java.time.format.DateTimeFormatter



object DateFormatter {
    private val UI_FORMAT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private val API_FORMAT =
        DateTimeFormatter.ISO_LOCAL_DATE

    fun apiToUiDate(uiDate: String): String {
        return try {
            LocalDate.parse(uiDate, API_FORMAT)
                .format(UI_FORMAT)
        } catch (e: Exception) {
            uiDate
        }
    }

    fun uiToApiDate(ApiDate: String): String? {
        return try {
            LocalDate.parse(ApiDate, UI_FORMAT)
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
