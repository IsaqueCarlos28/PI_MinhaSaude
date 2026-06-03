package com.example.medicoapplication.UI.common.formatters

import java.util.Locale

object NameFormatter {
    private val PREPOSITIONS =
        setOf(
            "da",
            "de",
            "do",
            "das",
            "dos",
            "e"
        )

    fun normalize(name: String): String {

        return name
            .trim()
            .lowercase(Locale("pt", "BR"))
            .split(Regex("\\s+"))
            .joinToString(" ") { word ->

                if (word in PREPOSITIONS) {
                    word
                } else {
                    word.replaceFirstChar {
                        it.uppercase()
                    }
                }
            }
    }
}