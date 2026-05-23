package com.example.medicoapplication.data.remote.DTO.local

data class EnderecoResponseDto(
    val cep: String?,
    val logradouro: String?,
    val numero: String?,
    val complemento: String?,
    val bairro: String?,
    val cidade: String?,
    val uf: String?
)
