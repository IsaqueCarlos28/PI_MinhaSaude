package com.example.medicoapplication.data.repository

import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadePageResponseDto
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeResponseDto
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeUpdateRequestDto
import com.example.medicoapplication.data.remote.RetrofitClient

class EspecialidadeRepository {
    private val api = RetrofitClient.api
    suspend fun getEspecialidades(
        page: Int = 0,
        size: Int = 20,
        sort: String = "nome,asc"
    ): Result<EspecialidadePageResponseDto> =
        runCatching {
            val response = api.getEspecialidades(page, size, sort)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }
    suspend fun getEspecialidade(id: Long): Result<EspecialidadeResponseDto>
            =
        runCatching {
            val response = api.getEspecialidadeById(id)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }
    suspend fun createEspecialidade(
        dto: EspecialidadeCreateRequestDto
    ): Result<EspecialidadeResponseDto> =
        runCatching {
            val response = api.createEspecialidade(dto)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }
    suspend fun updateEspecialidade(
        id: Long,
        dto: EspecialidadeUpdateRequestDto
    ): Result<EspecialidadeResponseDto> =
        runCatching {
            val response = api.updateEspecialidade(id, dto)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }
    suspend fun deleteEspecialidade(id: Long): Result<Unit> =
        runCatching {
            val response = api.deleteEspecialidade(id)
            if (!response.isSuccessful) {
                error("Erro ao deletar especialidade (${response.code()})")
            }
        }
}