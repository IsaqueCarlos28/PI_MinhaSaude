package com.example.medicoapplication.data.repository
import com.example.medicoapplication.data.remote.DTO.consulta.*
import com.example.medicoapplication.data.remote.RetrofitClient
class ConsultaRepository {
    private val api = RetrofitClient.api
    suspend fun agendarConsulta(
        idPaciente: Long,
        dto: ConsultaCreateRequestDto
    ): Result<ConsultaResponseDto> =
        safeApiCall { api.agendarConsulta(idPaciente, dto) }

    suspend fun getConsultaByIdPaciente(
        idPaciente: Long,
        idEvento : Long
    ) : Result<ConsultaResponseDto> =
        safeApiCall { api.getConsultaByIdPaciente(idPaciente, idEvento) }

    suspend fun reagendarConsulta(
        idPaciente: Long,
        idEvento: Long,
        dto: ConsultaUpdateRequestDto
    ): Result<ConsultaResponseDto> =
        safeApiCall { api.reagendarConsulta(idPaciente, idEvento, dto) }

    suspend fun atualizarStatusPaciente(
        idPaciente: Long,
        idEvento: Long,
        dto: ConsultaStatusRequestDto
    ): Result<ConsultaResponseDto> =
        safeApiCall { api.atualizarStatusPeloPaciente(idPaciente, idEvento, dto) }

    suspend fun atualizarStatusMedico(
        idMedico: Long,
        idEvento: Long,
        dto: ConsultaStatusRequestDto
    ): Result<ConsultaResponseDto> =
        safeApiCall {  api.atualizarStatusPeloMedico(idMedico, idEvento, dto) }
}
