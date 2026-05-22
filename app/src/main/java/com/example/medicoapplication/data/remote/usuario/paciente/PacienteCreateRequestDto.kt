package com.example.medicoapplication.data.remote.usuario.paciente

import com.example.medicoapplication.data.remote.DTO.Genero
import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class PacienteCreateRequestDto(
    @SerializedName("nome")
    val nome: String,

    @SerializedName("cpf")
    val cpf: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("telefone")
    val telefone: String,

    @SerializedName("genero")
    val genero: Genero,

    @SerializedName("dataNascimento")
    val dataNascimento: LocalDate,

    @SerializedName("senha")
    val senha: String
)