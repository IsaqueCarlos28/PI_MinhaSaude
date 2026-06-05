package com.example.medicoapplication.data.repository
import com.example.medicoapplication.data.remote.DTO.agenda.*
import com.example.medicoapplication.data.remote.RetrofitClient
class AgendaRepository: BaseRepository(){

    suspend fun getAgendas(
        idConsulta: Long
    ): Result<List<AgendaResponseDto>> {
        val idMedico = requireUserId()
        return safeApiCall { api.getAgendas(idMedico, idConsulta) }
    }

    suspend fun getAgendaById(
        idConsulta: Long,
        idAgenda: Long
    ): Result<AgendaResponseDto> {
        val idMedico = requireUserId()
        return safeApiCall { api.getAgendaById(idMedico, idConsulta, idAgenda) }
    }

    suspend fun getDisponibilidade(
        idConsulta: Long,
        semanas: Int = 4
    ): Result<List<DisponibilidadeSemanaDTO>> {
        val idMedico = requireUserId()
        return safeApiCall { api.getDisponibilidade(idMedico, idConsulta, semanas) }
    }

    suspend fun createAgenda(
        idConsulta: Long,
        dto: AgendaRequestDTO
    ): Result<AgendaResponseDto> {
        val idMedico = requireUserId()
        return safeApiCall { api.createAgenda(idMedico, idConsulta, dto) }
    }

    suspend fun updateAgenda(
        idConsulta: Long,
        idAgenda: Long,
        dto: AgendaRequestDTO
    ): Result<AgendaResponseDto> {
        val idMedico = requireUserId()
        return safeApiCall { api.updateAgenda(idMedico, idConsulta, idAgenda, dto) }
    }

    suspend fun deleteAgenda(
        idMedico: Long,
        idConsulta: Long,
        idAgenda: Long
    ): Result<Unit> {
        val idMedico = requireUserId()
        return safeApiCall { api.deleteAgenda(idMedico, idConsulta, idAgenda) }
    }
}
