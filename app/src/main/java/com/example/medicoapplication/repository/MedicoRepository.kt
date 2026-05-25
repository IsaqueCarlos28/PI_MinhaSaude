package com.example.medicoapplication.data.repository

import com.example.medicoapplication.data.remote.RetrofitClient
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto

class MedicoRepository {

    private val api = RetrofitClient.api

    suspend fun getMedico(idMedico: Long): Result<MedicoResponseDto> =
        runCatching {
            val response = api.getMedicoById(idMedico)
            response.body() ?: error("Médico não encontrado.")
        }

    suspend fun getConsultas(idMedico: Long): Result<List<ConsultaResponseDto>> =
        runCatching {
            val response = api.getConsultasByMedico(idMedico)
            response.body() ?: emptyList()
        }
}
