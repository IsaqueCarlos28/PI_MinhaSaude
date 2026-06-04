package com.example.medicoapplication.UI.common.validations

/**
 * Validates the "criar consulta ofertada" form used in:
 *   - CriarConsultaOfertadaActivity
 */
object ConsultaOfertadaValidator {

    fun validar(
        especialidadeIdx: Int,  // 0 means "Selecione..." placeholder
        valor: Double?,
        duracao: Int?
    ): ValidationResult<Unit> {

        if (especialidadeIdx == 0) {
            return ValidationResult.Error(
                ValidationField.ESPECIALIDADE,
                "Selecione uma especialidade."
            )
        }

        if (valor == null || valor <= 0) {
            return ValidationResult.Error(
                ValidationField.VALOR,
                "Informe um valor válido para a consulta."
            )
        }

        if (duracao == null || duracao <= 0) {
            return ValidationResult.Error(
                ValidationField.DURACAO,
                "Informe a duração em minutos."
            )
        }

        return ValidationResult.Success(Unit)
    }
}
