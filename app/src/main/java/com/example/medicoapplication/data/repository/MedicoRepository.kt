package com.example.medicoapplication.data.repository

import com.example.medicoapplication.data.local.AppDependencies
import com.example.medicoapplication.data.remote.RetrofitClient
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaResponseDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoPageResponseDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
import java.util.Objects

class MedicoRepository : BaseRepository() {

    // Médico autenticado — GET medicos/{id}
    suspend fun getMedico(): Result<MedicoResponseDto> {
        val idMedico = requireUserId()
        return safeApiCall { api.getMedicoById(idMedico) }
    }

    // Médico por ID (visão pública do paciente) — GET medicos/{id}
    suspend fun getMedicoById(idMedico: Long): Result<MedicoResponseDto> {
        return safeApiCall { api.getMedicoById(idMedico) }
    }

    suspend fun getMedicos(page: Int, size: Int): Result<MedicoPageResponseDto> {
        return safeApiCall { api.getMedicos(page, size) }
    }

    // Lista todas as consultas agendadas do médico autenticado
    suspend fun getConsultas(): Result<List<ConsultaResponseDto>> {
        val idMedico = requireUserId()
        return safeApiCall { api.getConsultasByMedico(idMedico) }
    }

    // Detalhe de uma consulta específica do médico — GET medicos/{id}/consultas-agendadas/{idEvento}
    suspend fun getConsultaById(idEvento: Long): Result<ConsultaResponseDto> {
        val idMedico = requireUserId()
        return safeApiCall { api.getConsultaByIdMedico(idMedico, idEvento) }
    }

    suspend fun getConsultasOfertadas(): Result<List<ConsultaOfertadaResponseDto>> {
        val idMedico = requireUserId()
        return safeApiCall { api.getConsultasOfertadas(idMedico) }
    }

    // Consultas ofertadas de qualquer médico pelo ID (visão pública)
    suspend fun getConsultasOfertadasDoMedico(idMedico: Long): Result<List<ConsultaOfertadaResponseDto>> {
        return safeApiCall { api.getConsultasOfertadas(idMedico) }
    }
}