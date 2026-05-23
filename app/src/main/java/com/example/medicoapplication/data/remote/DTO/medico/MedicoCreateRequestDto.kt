package com.example.medicoapplication.data.remote.DTO.medico

import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto

data class MedicoCreateRequestDto(
<<<<<<< HEAD
    val nome: String,
    val crm: String,
    val email: String,
    val telefone: String,
    val senha: String,
    val especialidade: String,
    val uf: String   // ex: "SP"
=======
    val usuarioBase: PacienteCreateRequestDto,
    val crmUf: String,      // JSON key is "crmUf" — UF enum string e.g. "SP"
    val crmDigits: String   // exactly 6 digits
>>>>>>> 2ace8766d38c78a681c89f28b8086b9ad78212c2
)
