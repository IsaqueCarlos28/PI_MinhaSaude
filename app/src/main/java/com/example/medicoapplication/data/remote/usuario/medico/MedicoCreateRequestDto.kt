package com.example.medicoapplication.data.remote.usuario.medico

import com.example.medicoapplication.data.remote.DTO.UnidadeFederativa
import com.example.medicoapplication.data.remote.usuario.paciente.PacienteCreateRequestDto
import com.google.gson.annotations.SerializedName

data class MedicoCreateRequestDto(
    @SerializedName("usuarioBase")
    val nome: PacienteCreateRequestDto,

    @SerializedName("crmUf")
    val uf: UnidadeFederativa,

    @SerializedName("crmDigits")
    val digits: String
)
