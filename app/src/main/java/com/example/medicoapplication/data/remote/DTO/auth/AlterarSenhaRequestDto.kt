package com.example.medicoapplication.data.remote.DTO.auth

data class AlterarSenhaRequestDto(
    val tokenRecuperacao: String?,
    val novaSenha: String
)
