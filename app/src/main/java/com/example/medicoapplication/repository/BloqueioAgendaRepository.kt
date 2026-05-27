package com.example.medicoapplication.data.repository
import com.example.medicoapplication.data.remote.DTO.bloqueioagenda.*
import com.example.medicoapplication.data.remote.RetrofitClient
class BloqueioAgendaRepository {
    private val api = RetrofitClient.api
    suspend fun getBloqueios(
        idMedico: Long
    ): Result<List<BloqueioAgendaResponseDto>> =
        runCatching {
            val response = api.getBloqueiosAgenda(idMedico)
            response.body() ?: emptyList()
        }
    suspend fun createBloqueio(
        idMedico: Long,
        dto: BloqueioAgendaCreateRequestDto
    ): Result<BloqueioAgendaResponseDto> =
        runCatching {
            val response = api.createBloqueioAgenda(idMedico, dto)
            response.body() ?: error("Erro ao criar bloqueio.")
        }
    suspend fun updateBloqueio(
        idMedico: Long,
        idBloqueio: Long,
        dto: BloqueioAgendaUpdateRequestDto
    ): Result<BloqueioAgendaResponseDto> =

    runCatching {
        val response = api.updateBloqueioAgenda(idMedico, idBloqueio,
            dto)
        response.body() ?: error("Erro ao atualizar bloqueio.")
    }
    suspend fun deleteBloqueio(
        idMedico: Long,
        idBloqueio: Long
    ): Result<Unit> =
        runCatching {
            val response = api.deleteBloqueioAgenda(idMedico, idBloqueio)
            if (!response.isSuccessful) {
                error("Erro ao deletar bloqueio (${response.code()})")
            }
        }
}