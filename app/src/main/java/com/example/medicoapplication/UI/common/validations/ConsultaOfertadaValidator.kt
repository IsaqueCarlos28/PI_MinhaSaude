package com.example.medicoapplication.UI.common.validations

import com.example.medicoapplication.data.remote.DTO.consultaofertada.TipoConsulta

/**
 * Valida o formulário de criação/edição de consulta ofertada.
 * As regras espelham exatamente as do ConsultaOfertadaService na API.
 */
object ConsultaOfertadaValidator {

    fun validar(
        especialidadeIdx: Int,   // 0 = placeholder "Selecione..."
        tipoConsulta: TipoConsulta,
        localIdx: Int,           // 0 = "Sem local (TELECONSULTA)"
        valor: Double?,
        duracao: Int?,
        aceitaParticular: Boolean,
        conveniosSelecionados: Set<Long>
    ): ValidationResult<Unit> {

        if (especialidadeIdx == 0) {
            return ValidationResult.Error(
                ValidationField.ESPECIALIDADE,
                "Selecione uma especialidade."
            )
        }

        // Regra API: PRESENCIAL exige local
        if (tipoConsulta == TipoConsulta.PRESENCIAL && localIdx == 0) {
            return ValidationResult.Error(
                ValidationField.GENERIC,
                "Consultas presenciais devem possuir local."
            )
        }

        // Regra API: TELECONSULTA não pode ter local
        if (tipoConsulta == TipoConsulta.TELECONSULTA && localIdx != 0) {
            return ValidationResult.Error(
                ValidationField.GENERIC,
                "Teleconsulta não deve possuir local. Remova o local selecionado."
            )
        }

        if (duracao == null || duracao <= 0) {
            return ValidationResult.Error(
                ValidationField.DURACAO,
                "Informe a duração em minutos."
            )
        }

        // Regra API: não aceita particular → valor deve ser zero
        if (!aceitaParticular && valor != null && valor > 0.0) {
            return ValidationResult.Error(
                ValidationField.VALOR,
                "Consultas que não aceitam particular não devem possuir valor."
            )
        }

        // Regra API: aceita particular → valor deve ser > 0
        if (aceitaParticular && (valor == null || valor <= 0.0)) {
            return ValidationResult.Error(
                ValidationField.VALOR,
                "Consultas particulares devem possuir valor maior que zero."
            )
        }

        // Regra API: deve aceitar particular OU ter ao menos um convênio
        if (!aceitaParticular && conveniosSelecionados.isEmpty()) {
            return ValidationResult.Error(
                ValidationField.GENERIC,
                "A consulta deve aceitar particular ou possuir ao menos um convênio."
            )
        }

        return ValidationResult.Success(Unit)
    }
}
