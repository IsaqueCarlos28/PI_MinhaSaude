package com.example.medicoapplication.data.repository

import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadePageResponseDto
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeResponseDto
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeUpdateRequestDto
import com.example.medicoapplication.data.remote.RetrofitClient

class EspecialidadeRepository {
    private val api = RetrofitClient.api
    suspend fun getEspecialidades(
        page: Int = 0,
        size: Int = 20,
        sort: String = "nome,asc"
    ): Result<EspecialidadePageResponseDto> =
        safeApiCall { api.getEspecialidades(page, size, sort) }

    suspend fun getEspecialidade(
        id: Long
    ): Result<EspecialidadeResponseDto> =
        safeApiCall { api.getEspecialidadeById(id) }

    suspend fun createEspecialidade(
        dto: EspecialidadeCreateRequestDto
    ): Result<EspecialidadeResponseDto> =
        safeApiCall { api.createEspecialidade(dto) }

    suspend fun updateEspecialidade(
        id: Long,
        dto: EspecialidadeUpdateRequestDto
    ): Result<EspecialidadeResponseDto> =
        safeApiCall { api.updateEspecialidade(id, dto) }

    suspend fun deleteEspecialidade(
        id: Long
    ): Result<Unit> =
        safeApiCall { api.deleteEspecialidade(id) }
}