package com.example.medicoapplication.data.repository
import com.example.medicoapplication.data.remote.DTO.bloqueioagenda.*
import com.example.medicoapplication.data.remote.RetrofitClient
class BloqueioAgendaRepository : BaseRepository(){

    suspend fun getBloqueios(
    ): Result<List<BloqueioAgendaResponseDto>> {
        val idMedico = requireUserId()
        return safeApiCall { api.getBloqueiosAgenda(idMedico) }
    }

    suspend fun createBloqueio(
        dto: BloqueioAgendaCreateRequestDto
    ): Result<BloqueioAgendaResponseDto> {
        val idMedico = requireUserId()
        return safeApiCall { api.createBloqueioAgenda(idMedico, dto) }
    }

    suspend fun updateBloqueio(
        idMedico: Long,
        idBloqueio: Long,
        dto: BloqueioAgendaUpdateRequestDto
    ): Result<BloqueioAgendaResponseDto> =
    safeApiCall { api.updateBloqueioAgenda(idMedico, idBloqueio, dto) }

    suspend fun deleteBloqueio(
        idMedico: Long,
        idBloqueio: Long
    ): Result<Unit> =
        safeApiCall { api.deleteBloqueioAgenda(idMedico, idBloqueio) }
}