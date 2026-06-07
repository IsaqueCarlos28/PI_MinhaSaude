package com.example.medicoapplication.data.repository

import com.example.medicoapplication.data.local.AppDependencies
import com.example.medicoapplication.data.remote.RetrofitClient
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaResponseDto
import com.example.medicoapplication.data.remote.DTO.consulta.ConsultaStatusRequestDto
import com.example.medicoapplication.data.remote.DTO.consultaofertada.ConsultaOfertadaResponseDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoPageResponseDto
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
import java.util.Objects

class MedicoRepository : BaseRepository(){
    //Medico
    suspend fun getMedico(): Result<MedicoResponseDto> {
        val idMedico = requireUserId()
        return safeApiCall { api.getMedicoById(idMedico) }
    }

    //Paciente
    suspend fun getMedicoById(idMedico: Long): Result<MedicoResponseDto> {
        val idMedico = requireUserId()
        return safeApiCall { api.getMedicoById(idMedico) }
    }

    suspend fun getMedicos(page: Int, size: Int): Result<MedicoPageResponseDto> {
        return safeApiCall { api.getMedicos(page, size) }
    }
    suspend fun getConsultas(): Result<List<ConsultaResponseDto>> {
        val idMedico = requireUserId()
        return safeApiCall { api.getConsultasByMedico(idMedico) }
    }

    suspend fun getConsultasOfertadas(): Result<List<ConsultaOfertadaResponseDto>> {
        val idMedico = requireUserId()
        return safeApiCall { api.getConsultasOfertadas(idMedico) }
    }

    suspend fun getConsultaById(idEvento: Long): Result<ConsultaResponseDto> {
        val idMedico = requireUserId()
        return safeApiCall { api.getConsultaByIdMedico(idMedico, idEvento) }
    }

    suspend fun atualizarStatusConsulta(
        idEvento: Long,
        dto: ConsultaStatusRequestDto
    ): Result<ConsultaResponseDto> {
        val idMedico = requireUserId()
        return safeApiCall { api.atualizarStatusPeloMedico(idMedico, idEvento, dto) }
    }
}