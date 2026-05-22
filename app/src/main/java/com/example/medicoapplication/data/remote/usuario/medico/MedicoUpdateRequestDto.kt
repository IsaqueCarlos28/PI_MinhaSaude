package com.example.medicoapplication.data.remote.usuario.medico

import com.example.medicoapplication.data.remote.DTO.UnidadeFederativa
import com.example.medicoapplication.data.remote.usuario.paciente.PacienteUpdateRequestDto
import com.google.gson.annotations.SerializedName

data class MedicoUpdateRequestDto(
    @SerializedName("usuarioBase")
    val nome: PacienteUpdateRequestDto,

    @SerializedName("crmUf")
    val uf: UnidadeFederativa,

    @SerializedName("crmDigits")
    val digits: String
)
