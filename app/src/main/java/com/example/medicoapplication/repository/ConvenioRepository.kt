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
            response.body() ?: error("Erro ao buscar convênios.")
        }
    suspend fun getConvenio(id: Long): Result<ConvenioResponseDto> =
        runCatching {
            val response = api.getConvenioById(id)
            response.body() ?: error("Convênio não encontrado.")
        }
    suspend fun createConvenio(
        dto: ConvenioCreateRequestDto
    ): Result<ConvenioResponseDto> =
        runCatching {
            val response = api.createConvenio(dto)
            response.body() ?: error("Erro ao criar convênio.")
        }
    suspend fun updateConvenio(
        id: Long,
        dto: ConvenioUpdateRequestDto
    ): Result<ConvenioResponseDto> =
        runCatching {
            val response = api.updateConvenio(id, dto)
            response.body() ?: error("Erro ao atualizar convênio.")
        }
    suspend fun deleteConvenio(id: Long): Result<Unit> =
        runCatching {
            val response = api.deleteConvenio(id)
            if (!response.isSuccessful) {
                error("Erro ao deletar convênio (${response.code()})")
            }
        }
}
