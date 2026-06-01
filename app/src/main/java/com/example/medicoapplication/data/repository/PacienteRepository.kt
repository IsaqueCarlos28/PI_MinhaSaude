package com.example.medicoapplication.data.repository

import com.example.medicoapplication.data.remote.RetrofitClient
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaStatusRequestDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteEditRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteResponseDto

class PacienteRepository {

    private val api = RetrofitClient.api

    suspend fun getPaciente(
        idPaciente: Long
    ): Result<PacienteResponseDto> =
        safeApiCall { api.getPacienteById(idPaciente) }

    suspend fun updatePaciente(
        idPaciente: Long,
        dto : PacienteEditRequestDto
    ) : Result<PacienteResponseDto> =
    safeApiCall { api.updatePaciente(idPaciente, dto)}
    suspend fun getConsultas(
        idPaciente: Long
    ): Result<List<ConsultaResponseDto>> =
        safeApiCall { api.getConsultasByPaciente(idPaciente) }

    suspend fun cancelarConsulta(
        idPaciente: Long,
        idEvento: Long,
        status: ConsultaStatusRequestDto
    ): Result<ConsultaResponseDto> =
        safeApiCall { api.atualizarStatusPeloPaciente(idPaciente, idEvento, status) }

    suspend fun getMedico(
        idMedico: Long
    ): Result<MedicoResponseDto> =
        safeApiCall { api.getMedicoById(idMedico) }
}
