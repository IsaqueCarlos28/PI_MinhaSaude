package com.example.medicoapplication.data.repository

import com.example.medicoapplication.data.remote.RetrofitClient
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto

class MedicoRepository {

    private val api = RetrofitClient.api

    suspend fun getMedico(idMedico: Long): Result<MedicoResponseDto> =
        runCatching {
            val response = api.getMedicoById(idMedico)
            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }

    suspend fun getMedicos(page: Int = 0, size: Int = 50): Result<List<MedicoResponseDto>> =
        runCatching {
            val response = api.getMedicos(page, size)
            response.body()?._embedded?.medicos ?: emptyList()
        }

    suspend fun getConsultas(idMedico: Long): Result<List<ConsultaResponseDto>> =
        runCatching {
            val response = api.getConsultasByMedico(idMedico)
            response.body() ?: emptyList()
        }
}