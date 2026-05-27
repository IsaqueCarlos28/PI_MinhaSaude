package com.example.medicoapplication.data.repository

import com.example.medicoapplication.data.remote.RetrofitClient
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaStatusRequestDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteResponseDto

class PacienteRepository {

    private val api = RetrofitClient.api

    suspend fun getPaciente(idPaciente: Long): Result<PacienteResponseDto> =
        runCatching {
            val response = api.getPacienteById(idPaciente)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }

    suspend fun getConsultas(idPaciente: Long): Result<List<ConsultaResponseDto>> =
        runCatching {
            val response = api.getConsultasByPaciente(idPaciente)
            response.body() ?: emptyList()
        }

    suspend fun cancelarConsulta(
        idPaciente: Long,
        idEvento: Long,
        status: ConsultaStatusRequestDto
    ): Result<Unit> =
        runCatching {
            val response = api.atualizarStatusPeloPaciente(idPaciente, idEvento, status)
            if (!response.isSuccessful) error("Não foi possível cancelar (${response.code()}).")
        }

    suspend fun getMedico(idMedico: Long): Result<MedicoResponseDto> =
        runCatching {
            val response = api.getMedicoById(idMedico)

            if (response.isSuccessful) {
                response.body() ?: error("Resposta vazia do servidor")
            } else {
                error("Erro ${response.code()}: ${response.message()}")
            }
        }
}
