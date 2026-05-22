package com.example.medicoapplication.data.remote.DTO.paciente

import com.google.gson.annotations.SerializedName

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
    val genero: String, // Usando String para facilitar o envio do "MASCULINO" / "FEMININO"

    @SerializedName("dataNascimento")
    val dataNascimento: String, // Usando String para enviar o formato "yyyy-MM-dd"

    @SerializedName("senha")
    val senha: String
)