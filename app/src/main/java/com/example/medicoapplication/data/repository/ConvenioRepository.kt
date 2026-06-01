package com.example.medicoapplication.data.repository
import com.example.medicoapplication.data.remote.DTO.convenio.*
import com.example.medicoapplication.data.remote.RetrofitClient
class ConvenioRepository {
    private val api = RetrofitClient.api
    suspend fun getConvenios(
        page: Int = 0,
        size: Int = 20,
        sort: String = "nome,asc"
    ): Result<ConvenioPageResponseDto> =
        safeApiCall { api.getConvenios(page, size, sort) }


    suspend fun getConvenio(id: Long): Result<ConvenioResponseDto> =
        safeApiCall { api.getConvenioById(id) }


    suspend fun createConvenio(
        dto: ConvenioCreateRequestDto
    ): Result<ConvenioResponseDto> =
        safeApiCall { api.createConvenio(dto) }


    suspend fun updateConvenio(
        id: Long,
        dto: ConvenioUpdateRequestDto
    ): Result<ConvenioResponseDto> =
        safeApiCall {  api.updateConvenio(id, dto) }


    suspend fun deleteConvenio(id: Long): Result<Unit> =
        safeApiCall { api.deleteConvenio(id) }
}
