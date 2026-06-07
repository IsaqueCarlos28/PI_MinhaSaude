package com.example.medicoapplication.data.repository

import android.util.Log
import com.example.medicoapplication.data.remote.RetrofitClient
import com.example.medicoapplication.data.remote.DTO.auth.AlterarSenhaRequestDto
import com.example.medicoapplication.data.remote.DTO.auth.EsqueceuSenhaRequestDto
import com.example.medicoapplication.data.remote.DTO.auth.LoginRequestDto
import com.example.medicoapplication.data.remote.DTO.auth.LoginResponseDto
import com.example.medicoapplication.data.remote.DTO.auth.ValidarTokenRequestDto
import com.example.medicoapplication.data.remote.DTO.auth.TokenDeRecuperacaoDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
import com.example.medicoapplication.data.remote.DTO.notification.DesativarFcmTokenRequest
import com.example.medicoapplication.data.remote.DTO.notification.RegistrarFcmTokenRequest
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteResponseDto
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await


class AuthRepository(

): BaseRepository() {

    private val notificationRepository =  NotificationRepository()
    suspend fun login(
        email: String,
        senha: String
    ): Result<LoginResponseDto> =
        safeApiCall {
            api.login(LoginRequestDto(email, senha))
        }.onSuccess { usuario ->

            sessionManager.salvarSessao(
                usuario.id,
                usuario.email,
                usuario.role.toString()
            )

            registrarFcmToken(usuario.id)
        }

    suspend fun cadastrarPaciente(dto: PacienteCreateRequestDto): Result<PacienteResponseDto> =
       safeApiCall { api.createPaciente(dto)}

    suspend fun cadastrarMedico(dto: MedicoCreateRequestDto): Result<MedicoResponseDto> =
        safeApiCall {api.createMedico(dto)}

    suspend fun esqueceuSenha(email: String): Result<Unit> =
        runCatching {
            val response = api.esqueceuSenha(EsqueceuSenhaRequestDto(email))
            if (!response.isSuccessful) error("Erro ao enviar solicitação (${response.code()})")
        }

    suspend fun validarCodigo(email: String?, codigo: String): Result<TokenDeRecuperacaoDto> =
        runCatching {
            val response = api.validarCodigo(ValidarTokenRequestDto(email, codigo))

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }

    suspend fun alterarSenha(token: String?, novaSenha: String): Result<Unit> =
        runCatching {
            val response = api.alterarSenha(AlterarSenhaRequestDto(token, novaSenha))
            if (!response.isSuccessful) error("Erro ao alterar senha (${response.code()})")
        }

    suspend fun logout() {
        desativarFcmToken()
        sessionManager.limparSessao()
    }

    private suspend fun registrarFcmToken(
        usuarioId: Long
    ) {
        try {

            val token =
                FirebaseMessaging
                    .getInstance()
                    .token
                    .await()

            notificationRepository
                .registrarFcmToken(RegistrarFcmTokenRequest(usuarioId,token))


        } catch (e: Exception) {
            Log.e(
                "FCM",
                "Erro ao registrar token",
                e
            )
        }
    }

    suspend fun desativarFcmToken() {

        try {

            val token =
                FirebaseMessaging
                    .getInstance()
                    .token
                    .await()

            notificationRepository
                .desativarFcmToken(DesativarFcmTokenRequest(token))
        } catch (e: Exception) {

            Log.e(
                "FCM",
                "Erro ao remover token",
                e
            )
        }
    }
}
