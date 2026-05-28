package com.example.medicoapplication.data.repository
import com.example.medicoapplication.data.remote.DTO.convenio.*
import com.example.medicoapplication.data.remote.RetrofitClient
class ConvenioRepository {
    private val api = RetrofitClient.api
    suspend fun getConvenios(
        page: Int = 0,
        size: Int = 20,
        sort: String = "nome,asc"
    ): Result<ConvenioPageResponseDto> =
        runCatching {
            val response = api.getConvenios(page, size, sort)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }
    suspend fun getConvenio(id: Long): Result<ConvenioResponseDto> =
        runCatching {
            val response = api.getConvenioById(id)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }
    suspend fun createConvenio(
        dto: ConvenioCreateRequestDto
    ): Result<ConvenioResponseDto> =
        runCatching {
            val response = api.createConvenio(dto)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }
    suspend fun updateConvenio(
        id: Long,
        dto: ConvenioUpdateRequestDto
    ): Result<ConvenioResponseDto> =
        runCatching {
            val response = api.updateConvenio(id, dto)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }
    suspend fun deleteConvenio(id: Long): Result<Unit> =
        runCatching {
            val response = api.deleteConvenio(id)
            if (!response.isSuccessful) {
                error("Erro ao deletar convênio (${response.code()})")
            }
        }
}
