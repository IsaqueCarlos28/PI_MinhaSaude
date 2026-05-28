package com.example.medicoapplication.data.repository
import com.example.medicoapplication.data.remote.DTO.local.*
import com.example.medicoapplication.data.remote.RetrofitClient

class LocalRepository {
    private val api = RetrofitClient.api
    suspend fun getLocais(
        page: Int = 0,
        size: Int = 20,
        sort: String = "nome,asc"
    ): Result<LocalPageResponseDto> =
        runCatching {
            val response = api.getLocais(page, size, sort)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }
    suspend fun getLocal(id: Long): Result<LocalResponseDto> =
        runCatching {
            val response = api.getLocalById(id)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }
    suspend fun createLocal(
        dto: LocalCreateRequestDto
    ): Result<LocalResponseDto> =
        runCatching {
            val response = api.createLocal(dto)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }
    suspend fun updateLocal(
        id: Long,
        dto: LocalUpdateRequestDto
    ): Result<LocalResponseDto> =
        runCatching {
            val response = api.updateLocal(id, dto)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }
    suspend fun deleteLocal(id: Long): Result<Unit> =
        runCatching {
            val response = api.deleteLocal(id)
            if (!response.isSuccessful) {
                error("Erro ao deletar local (${response.code()})")
            }
        }
}
