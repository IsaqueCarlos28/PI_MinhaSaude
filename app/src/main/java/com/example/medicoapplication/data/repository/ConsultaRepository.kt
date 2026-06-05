package com.example.medicoapplication.data.repository
import com.example.medicoapplication.data.remote.DTO.consulta.*
import com.example.medicoapplication.data.remote.RetrofitClient
class ConsultaRepository : BaseRepository(){
    suspend fun agendarConsulta(
        dto: ConsultaCreateRequestDto
    ): Result<ConsultaResponseDto> {
        val idPaciente = requireUserId()
        return safeApiCall { api.agendarConsulta(idPaciente, dto) }
    }

    suspend fun getConsultaByIdPaciente(
        idEvento : Long
    ) : Result<ConsultaResponseDto> {
        val idPaciente = requireUserId()
        return safeApiCall { api.getConsultaByIdPaciente(idPaciente, idEvento) }
    }

    suspend fun reagendarConsulta(
        idEvento: Long,
        dto: ConsultaUpdateRequestDto
    ): Result<ConsultaResponseDto> {
        val idPaciente = requireUserId()
        return safeApiCall { api.reagendarConsulta(idPaciente, idEvento, dto) }
    }

    suspend fun atualizarStatusPaciente(
        idEvento: Long,
        dto: ConsultaStatusRequestDto
    ): Result<ConsultaResponseDto> {
        val idPaciente = requireUserId()
        return safeApiCall { api.atualizarStatusPeloPaciente(idPaciente, idEvento, dto) }
    }

    suspend fun atualizarStatusMedico(
        idEvento: Long,
        dto: ConsultaStatusRequestDto
    ): Result<ConsultaResponseDto> {
        val idMedico = requireUserId()
        return safeApiCall { api.atualizarStatusPeloMedico(idMedico, idEvento, dto) }
    }
}
