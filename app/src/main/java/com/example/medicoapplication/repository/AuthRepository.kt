package com.example.medicoapplication.data.repository

import com.example.medicoapplication.data.remote.RetrofitClient
import com.example.medicoapplication.data.remote.DTO.auth.AlterarSenhaRequestDto
import com.example.medicoapplication.data.remote.DTO.auth.EsqueceuSenhaRequestDto
import com.example.medicoapplication.data.remote.DTO.auth.ValidarTokenRequestDto
import com.example.medicoapplication.data.remote.DTO.login.LoginRequestDto
import com.example.medicoapplication.data.remote.DTO.login.LoginResponseDto
import com.example.medicoapplication.data.remote.DTO.auth.TokenDeRecuperacaoDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto

class AuthRepository {

    private val api = RetrofitClient.api

    suspend fun login(email: String, senha: String): Result<LoginResponseDto> =
        runCatching {
            val response = api.login(LoginRequestDto(email, senha))
            response.body() ?: error("Resposta inesperada do servidor.")
        }

    suspend fun cadastrarPaciente(dto: PacienteCreateRequestDto): Result<Unit> =
        runCatching {
            val response = api.createPaciente(dto)
            if (!response.isSuccessful) error("Erro ${response.code()}: Verifique os dados.")
        }

    suspend fun cadastrarMedico(dto: MedicoCreateRequestDto): Result<Unit> =
        runCatching {
            val response = api.createMedico(dto)
            if (!response.isSuccessful) error("Erro ${response.code()}: Verifique os dados.")
        }

    suspend fun esqueceuSenha(email: String): Result<Unit> =
        runCatching {
            val response = api.esqueceuSenha(EsqueceuSenhaRequestDto(email))
            if (!response.isSuccessful) error("Erro ao enviar solicitação (${response.code()})")
        }

    suspend fun validarCodigo(email: String?, codigo: String): Result<TokenDeRecuperacaoDto> =
        runCatching {
            val response = api.validarCodigo(ValidarTokenRequestDto(email, codigo))
            response.body() ?: error("Código inválido.")
        }

    suspend fun alterarSenha(token: String?, novaSenha: String): Result<Unit> =
        runCatching {
            val response = api.alterarSenha(AlterarSenhaRequestDto(token, novaSenha))
            if (!response.isSuccessful) error("Erro ao alterar senha (${response.code()})")
        }
}
