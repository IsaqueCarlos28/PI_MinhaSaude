package com.example.medicoapplication.data.repository

import com.example.medicoapplication.data.remote.DTO.notification.DesativarFcmTokenRequest
import com.example.medicoapplication.data.remote.DTO.notification.RegistrarFcmTokenRequest

class NotificationRepository : BaseRepository() {

    suspend fun registrarFcmToken(
        dto : RegistrarFcmTokenRequest
    ): Result<Unit> =
        safeApiCall {api.registrarFcmToken(dto)}

    suspend fun desativarFcmToken(
        dto : DesativarFcmTokenRequest
    ): Result<Unit> =
        safeApiCall {api.desativarFcmToken(dto)}



}