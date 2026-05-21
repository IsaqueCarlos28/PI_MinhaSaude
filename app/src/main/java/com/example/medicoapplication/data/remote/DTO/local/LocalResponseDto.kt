package com.example.medicoapplication.data.remote.DTO.local

data class LocalResponseDto(
    val id: Long,
    val nome: String,
    val endereco: String,
    val cidade: String,
    val estado: String,
    val cep: String
)
