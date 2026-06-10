package com.example.medicoapplication.data.repository

import com.example.medicoapplication.data.remote.DTO.medicoespecialidade.MedicoEspecialidadeCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.medicoespecialidade.MedicoEspecialidadeResponseDto

class MedicoEspecialidadeRepository : BaseRepository() {

    suspend fun getMedicoEspecialidades(
        medicoId: Long
    ): Result<List<MedicoEspecialidadeResponseDto>> =
        safeApiCall {
            api.getMedicoEspecialidades(medicoId)
        }

    suspend fun getMinhasEspecialidades(
    ): Result<List<MedicoEspecialidadeResponseDto>>{
        val idMedico = requireUserId()
        return safeApiCall {
            api.getMedicoEspecialidades(idMedico)
        }
    }

    suspend fun addMedicoEspecialidade(
        dto: MedicoEspecialidadeCreateRequestDto
    ): Result<MedicoEspecialidadeResponseDto>{
        val idMedico = requireUserId()
        return safeApiCall {
            api.addMedicoEspecialidade(
                idMedico,
                dto
            )
        }
    }


    suspend fun deleteMedicoEspecialidade(
        id: Long
    ): Result<Unit>{
        val idMedico = requireUserId()
        return safeApiCall {
            api.deleteMedicoEspecialidade(
                idMedico,
                id
            )
        }

    }

}