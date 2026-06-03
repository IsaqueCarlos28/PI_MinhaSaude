package com.example.medicoapplication.UI.common.formatters

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    private val formatter =
        NumberFormat.getCurrencyInstance(
            Locale("pt", "BR")
        )

    fun format(value: Double): String {
        return formatter.format(value)
    }
}