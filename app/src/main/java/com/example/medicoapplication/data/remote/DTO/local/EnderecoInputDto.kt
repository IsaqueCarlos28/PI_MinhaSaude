package com.example.medicoapplication.data.remote.DTO.local

data class EnderecoInputDto(
    val cep: String,
    val numero: String,
    val complemento: String? = null
)
