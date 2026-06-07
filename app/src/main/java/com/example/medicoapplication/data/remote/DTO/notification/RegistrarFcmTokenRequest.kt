package com.example.medicoapplication.data.remote.DTO.notification

data class RegistrarFcmTokenRequest(
    val usuarioId: Long,
    val token: String
)