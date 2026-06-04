package com.example.medicoapplication.UI.common.mappers

import com.example.medicoapplication.data.remote.DTO.Genero

/**
 * Centralises Genero parsing so Activities don't repeat the same when/lowercase logic.
 *
 * Used in:
 *   - CadastroPacienteActivity (free-text EditText input)
 *   - EditarPerfilPacienteActivity (Spinner selection)
 *   - CadastroMedicoActivity (Spinner selection)
 */
object GeneroMapper {

    /**
     * Converts a free-text or spinner string to [Genero].
     * Accepts Portuguese labels ("Masculino", "masculino", "M", etc.) and enum names.
     */
    fun fromString(value: String): Genero {
        return when (value.trim().lowercase()) {
            "masculino", "m" -> Genero.MASCULINO
            "feminino", "f"  -> Genero.FEMININO
            else -> runCatching { Genero.valueOf(value.trim().uppercase()) }
                .getOrDefault(Genero.OUTRO)
        }
    }

    /** Returns the Portuguese display label for a [Genero] value. */
    fun toDisplayString(genero: Genero): String = when (genero) {
        Genero.MASCULINO -> "Masculino"
        Genero.FEMININO  -> "Feminino"
        Genero.OUTRO     -> "Outro"
    }

    /** List of display labels in the same order as [Genero.entries], for Spinner adapters. */
    val spinnerOptions: List<String> = Genero.entries.map { toDisplayString(it) }
}
