package com.example.medicoapplication.data.remote.DTO.local

data class LocalUpdateRequestDto(
    val nome: String,
    val endereco: EnderecoInputDto,
    val tipoLocal: TipoLocal
)
