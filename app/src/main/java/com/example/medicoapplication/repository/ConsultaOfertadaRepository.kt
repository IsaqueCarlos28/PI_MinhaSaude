package com.example.medicoapplication.data.repository
import com.example.medicoapplication.data.remote.DTO.consultaofertada.*
import com.example.medicoapplication.data.remote.RetrofitClient
class ConsultaOfertadaRepository {
    private val api = RetrofitClient.api
    suspend fun getConsultasOfertadas(
        idMedico: Long
    ): Result<List<ConsultaOfertadaResponseDto>> =
        runCatching {
            val response = api.getConsultasOfertadas(idMedico)
            response.body() ?: emptyList()
        }

    suspend fun getConsultaOfertada(
        idMedico: Long,
        id: Long
    ): Result<ConsultaOfertadaResponseDto> =
        runCatching {
            val response = api.getConsultaOfertadaById(idMedico, id)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }

    suspend fun createConsultaOfertada(
        idMedico: Long,
        dto: ConsultaOfertadaCreateRequestDto
    ): Result<ConsultaOfertadaResponseDto> =
        runCatching {
            val response = api.createConsultaOfertada(idMedico, dto)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }

    suspend fun updateConsultaOfertada(
        idMedico: Long,
        id: Long,
        dto: ConsultaOfertadaUpdateRequestDto
    ): Result<ConsultaOfertadaResponseDto> =
        runCatching {
            val response = api.updateConsultaOfertada(idMedico, id, dto)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }

    suspend fun deleteConsultaOfertada(
        idMedico: Long,
        id: Long
    ): Result<Unit> =
        runCatching {
            val response = api.deleteConsultaOfertada(idMedico, id)
            if (!response.isSuccessful) {
                error("Erro ao deletar consulta ofertada ($ {response.code()})")
            }
        }

}

