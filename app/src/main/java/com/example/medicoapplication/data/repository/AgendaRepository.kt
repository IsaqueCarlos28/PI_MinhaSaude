package com.example.medicoapplication.data.repository
import com.example.medicoapplication.data.remote.DTO.agenda.*
import com.example.medicoapplication.data.remote.RetrofitClient
class AgendaRepository {
    private val api = RetrofitClient.api
    suspend fun getAgendas(
        idMedico: Long,
        idConsulta: Long
    ): Result<List<AgendaResponseDto>> =
        safeApiCall { api.getAgendas(idMedico,idConsulta) }

    suspend fun getAgendaById(
        idMedico: Long,
        idConsulta: Long,
        idAgenda: Long
    ): Result<AgendaResponseDto> =
        safeApiCall { api.getAgendaById(idMedico, idConsulta, idAgenda)}


    suspend fun getDisponibilidade(
        idMedico: Long,
        idConsulta: Long,
        semanas: Int = 4
    ): Result<List<DisponibilidadeSemanaDTO>> =
        safeApiCall { api.getDisponibilidade(idMedico, idConsulta, semanas) }

    suspend fun createAgenda(
        idMedico: Long,
        idConsulta: Long,
        dto: AgendaRequestDTO
    ): Result<AgendaResponseDto> =
        safeApiCall { api.createAgenda(idMedico, idConsulta, dto) }

    suspend fun updateAgenda(
        idMedico: Long,
        idConsulta: Long,
        idAgenda: Long,
        dto: AgendaRequestDTO
    ): Result<AgendaResponseDto> =
        safeApiCall { api.updateAgenda(idMedico, idConsulta, idAgenda, dto) }

    suspend fun deleteAgenda(
        idMedico: Long,
        idConsulta: Long,
        idAgenda: Long
    ): Result<Unit> =
        safeApiCall { api.deleteAgenda(idMedico, idConsulta, idAgenda) }
}
