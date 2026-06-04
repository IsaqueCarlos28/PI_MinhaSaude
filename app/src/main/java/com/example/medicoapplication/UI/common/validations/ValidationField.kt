package com.example.medicoapplication.UI.common.validations

enum class ValidationField {
    // User / cadastro fields
    NOME,
    EMAIL,
    CPF,
    TELEFONE,
    DATA_NASCIMENTO,
    SENHA,
    SENHA_CONFIRMACAO,
    CRM,

    // Consultation scheduling fields
    DATA_CONSULTA,
    HORA_CONSULTA,
    MEDICO,

    // ConsultaOfertada form fields
    ESPECIALIDADE,
    VALOR,
    DURACAO,

    // Generic fallback
    GENERIC
}
