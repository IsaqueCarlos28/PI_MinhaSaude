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
    val genero: String,

    @SerializedName("dataNascimento")
    val dataNascimento: String,

    @SerializedName("senha")
    val senha: String
)
