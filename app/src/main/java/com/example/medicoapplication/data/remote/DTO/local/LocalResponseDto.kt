package com.example.medicoapplication.data.remote.DTO.local

data class LocalResponseDto(
    val id: Long,
    val nome: String?,
    val endereco: EnderecoResponseDto?,
    val tipoLocal: TipoLocal?
)
