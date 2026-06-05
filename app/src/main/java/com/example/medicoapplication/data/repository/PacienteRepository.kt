package com.example.medicoapplication.data.repository

import com.example.medicoapplication.data.remote.RetrofitClient
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaStatusRequestDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteEditRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteResponseDto

class PacienteRepository : BaseRepository() {

    suspend fun getPaciente(): Result<PacienteResponseDto> {
        val idPaciente = requireUserId()
        return safeApiCall { api.getPacienteById(idPaciente) }
    }

    suspend fun updatePaciente(
        dto : PacienteEditRequestDto
    ) : Result<PacienteResponseDto> {
        val idPaciente = requireUserId()
        return safeApiCall { api.updatePaciente(idPaciente, dto)}
    }
    suspend fun getConsultas(): Result<List<ConsultaResponseDto>> {
        val idPaciente = requireUserId()
        return safeApiCall { api.getConsultasByPaciente(idPaciente) }
    }

    suspend fun cancelarConsulta(
        idEvento: Long,
        status: ConsultaStatusRequestDto
    ): Result<ConsultaResponseDto> {
        val idPaciente = requireUserId()
        return safeApiCall { api.atualizarStatusPeloPaciente(idPaciente, idEvento, status) }
    }

    suspend fun getMedico(
        idMedico: Long
    ): Result<MedicoResponseDto> =
        safeApiCall { api.getMedicoById(idMedico) }
}
