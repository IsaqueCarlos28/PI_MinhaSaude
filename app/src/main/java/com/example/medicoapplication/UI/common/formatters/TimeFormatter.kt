package com.example.medicoapplication.UI.common.formatters

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object TimeFormatter {

    private val UI_FORMAT =
        DateTimeFormatter.ofPattern("HH:mm")

    private val API_FORMAT =
        DateTimeFormatter.ofPattern("HH:mm:ss")

    fun apiToUiTime(apiTime: String): String {
        return try {
            LocalTime.parse(apiTime, API_FORMAT)
                .format(UI_FORMAT)
        } catch (e: Exception) {
            apiTime
        }
    }

    fun uiToApiTime(uiTime: String): String? {
        return try {
            LocalTime.parse(uiTime, UI_FORMAT)
                .format(API_FORMAT)
        } catch (e: Exception) {
            null
        }
    }
}