package com.example.medicoapplication.data.repository

import com.example.medicoapplication.data.remote.RetrofitClient
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaResponseDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoPageResponseDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto

class MedicoRepository {

    private val api = RetrofitClient.api

    suspend fun getMedico(idMedico: Long): Result<MedicoResponseDto> =
        safeApiCall { api.getMedicoById(idMedico) }

    suspend fun getMedicos(page: Int, size: Int): Result<MedicoPageResponseDto> =
        safeApiCall { api.getMedicos(page, size) }

    suspend fun getConsultas(idMedico: Long): Result<List<ConsultaResponseDto>> =
        safeApiCall { api.getConsultasByMedico(idMedico) }

    suspend fun getConsultasOfertadas(idMedico: Long): Result<List<ConsultaOfertadaResponseDto>> =
        safeApiCall { api.getConsultasOfertadas(idMedico) }
}