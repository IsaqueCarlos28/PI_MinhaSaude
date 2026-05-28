package com.example.medicoapplication.data.repository
import com.example.medicoapplication.data.remote.DTO.agenda.*
import com.example.medicoapplication.data.remote.RetrofitClient
class AgendaRepository {
    private val api = RetrofitClient.api
    suspend fun getAgendas(
        idMedico: Long,
        idConsulta: Long
    ): Result<List<AgendaResponseDto>> =
        runCatching {
            val response = api.getAgendas(idMedico, idConsulta)
            response.body() ?: emptyList()
        }
    suspend fun getAgenda(
        idMedico: Long,
        idConsulta: Long,
        idAgenda: Long
    ): Result<AgendaResponseDto> =
        runCatching {
            val response = api.getAgendaById(idMedico, idConsulta, idAgenda)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }
    suspend fun getDisponibilidade(
        idMedico: Long,
        idConsulta: Long,
        semanas: Int = 4
    ): Result<List<DisponibilidadeSemanaDTO>> =
        runCatching {
            val response = api.getDisponibilidade(idMedico, idConsulta,
                semanas)
            response.body() ?: emptyList()
        }
    suspend fun createAgenda(
        idMedico: Long,
        idConsulta: Long,
        dto: AgendaRequestDTO
    ): Result<AgendaResponseDto> =
        runCatching {
            val response = api.createAgenda(idMedico, idConsulta, dto)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }
    suspend fun updateAgenda(
        idMedico: Long,
        idConsulta: Long,
        idAgenda: Long,
        dto: AgendaRequestDTO
    ): Result<AgendaResponseDto> =
        runCatching {
            val response = api.updateAgenda(idMedico, idConsulta, idAgenda,
                dto)
            response.body() ?: error("Erro ao atualizar agenda.")
        }
    suspend fun deleteAgenda(
        idMedico: Long,
        idConsulta: Long,
        idAgenda: Long
    ): Result<Unit> =
        runCatching {
            val response = api.deleteAgenda(idMedico, idConsulta, idAgenda)
            if (!response.isSuccessful) {
                error("Erro ao deletar agenda (${response.code()})")
            }
        }
}
