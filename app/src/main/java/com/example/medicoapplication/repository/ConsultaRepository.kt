package com.example.medicoapplication.data.repository
import com.example.medicoapplication.data.remote.DTO.consulta.*
import com.example.medicoapplication.data.remote.RetrofitClient
class ConsultaRepository {
    private val api = RetrofitClient.api
    suspend fun agendarConsulta(
        idPaciente: Long,
        dto: ConsultaCreateRequestDto
    ): Result<ConsultaResponseDto> =
        runCatching {
            val response = api.agendarConsulta(idPaciente, dto)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }
    suspend fun reagendarConsulta(
        idPaciente: Long,
        idEvento: Long,
        dto: ConsultaUpdateRequestDto
    ): Result<ConsultaResponseDto> =
        runCatching {
            val response = api.reagendarConsulta(idPaciente, idEvento, dto)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }
    suspend fun atualizarStatusPaciente(
        idPaciente: Long,
        idEvento: Long,
        dto: ConsultaStatusRequestDto
    ): Result<ConsultaResponseDto> =
        runCatching {
            val response = api.atualizarStatusPeloPaciente(idPaciente,
                idEvento, dto)
            response.body() ?: error("Erro ao atualizar status.")
        }
    suspend fun atualizarStatusMedico(
        idMedico: Long,
        idEvento: Long,
        dto: ConsultaStatusRequestDto
    ): Result<ConsultaResponseDto> =
        runCatching {
            val response = api.atualizarStatusPeloMedico(idMedico, idEvento,
                dto)
            response.body() ?: error("Erro ao atualizar status.")
        }
}
