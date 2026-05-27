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
            response.body() ?: error("Erro ao buscar locais.")
        }
    suspend fun getLocal(id: Long): Result<LocalResponseDto> =
        runCatching {
            val response = api.getLocalById(id)
            response.body() ?: error("Local não encontrado.")
        }
    suspend fun createLocal(
        dto: LocalCreateRequestDto
    ): Result<LocalResponseDto> =
        runCatching {
            val response = api.createLocal(dto)
            response.body() ?: error("Erro ao criar local.")
        }
    suspend fun updateLocal(
        id: Long,
        dto: LocalUpdateRequestDto
    ): Result<LocalResponseDto> =
        runCatching {
            val response = api.updateLocal(id, dto)
            response.body() ?: error("Erro ao atualizar local.")
        }
    suspend fun deleteLocal(id: Long): Result<Unit> =
        runCatching {
            val response = api.deleteLocal(id)
            if (!response.isSuccessful) {
                error("Erro ao deletar local (${response.code()})")
            }
        }
}
