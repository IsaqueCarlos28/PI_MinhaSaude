package com.example.medicoapplication.data.repository
import com.example.medicoapplication.data.remote.DTO.consultaofertada.*
import com.example.medicoapplication.data.remote.RetrofitClient

class ConsultaOfertadaRepository {
    private val api = RetrofitClient.api
    suspend fun getConsultasOfertadas(
        idMedico: Long
    ): Result<List<ConsultaOfertadaResponseDto>> =
        safeApiCall { api.getConsultasOfertadas(idMedico) }

    suspend fun getConsultaOfertada(
        idMedico: Long,
        id: Long
    ): Result<ConsultaOfertadaResponseDto> =
        safeApiCall { api.getConsultaOfertadaById(idMedico, id) }

    suspend fun createConsultaOfertada(
        idMedico: Long,
        dto: ConsultaOfertadaCreateRequestDto
    ): Result<ConsultaOfertadaResponseDto> =
        safeApiCall { api.createConsultaOfertada(idMedico, dto) }

    suspend fun updateConsultaOfertada(
        idMedico: Long,
        id: Long,
        dto: ConsultaOfertadaUpdateRequestDto
    ): Result<ConsultaOfertadaResponseDto> =
        safeApiCall { api.updateConsultaOfertada(idMedico, id, dto) }

    suspend fun deleteConsultaOfertada(
        idMedico: Long,
        id: Long
    ): Result<Unit> =
        safeApiCall { api.deleteConsultaOfertada(idMedico, id) }

}

