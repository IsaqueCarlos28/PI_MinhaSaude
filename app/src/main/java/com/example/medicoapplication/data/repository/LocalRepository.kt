package com.example.medicoapplication.data.repository
import com.example.medicoapplication.data.remote.DTO.local.*
import com.example.medicoapplication.data.remote.RetrofitClient

class LocalRepository : BaseRepository(){
    suspend fun getLocais(
        page: Int = 0,
        size: Int = 20,
        sort: String = "nome,asc"
    ): Result<LocalPageResponseDto> =
        safeApiCall { api.getLocais(page, size, sort) }


    suspend fun getLocal(
        id: Long
    ): Result<LocalResponseDto> =
        safeApiCall { api.getLocalById(id) }


    suspend fun createLocal(
        dto: LocalCreateRequestDto
    ): Result<LocalResponseDto> =
        safeApiCall { api.createLocal(dto) }


    suspend fun updateLocal(
        id: Long,
        dto: LocalUpdateRequestDto
    ): Result<LocalResponseDto> =
        safeApiCall { api.updateLocal(id, dto) }


    suspend fun deleteLocal(
        id: Long
    ): Result<Unit> =
        safeApiCall { api.deleteLocal(id) }
}
