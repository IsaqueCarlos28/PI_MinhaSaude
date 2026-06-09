package com.example.medicoapplication.data.repository
import com.example.medicoapplication.data.remote.DTO.agenda.DisponibilidadeSemanaDTO
import com.example.medicoapplication.data.remote.DTO.consultaofertada.*
import com.example.medicoapplication.data.remote.RetrofitClient

class ConsultaOfertadaRepository : BaseRepository() {

    //PACIENTE
    suspend fun getConsultasOfertadas(
        idMedico: Long
    ): Result<List<ConsultaOfertadaResponseDto>> =
        safeApiCall { api.getConsultasOfertadas(idMedico) }

    suspend fun getConsultaOfertada(
        idMedico: Long,
        id: Long
    ): Result<ConsultaOfertadaResponseDto> =
        safeApiCall { api.getConsultaOfertadaById(idMedico, id) }

    //MEDICO
    suspend fun getMinhaConsultasOfertadas(
    ): Result<List<ConsultaOfertadaResponseDto>> {
        val idMedico = requireUserId()
        return safeApiCall { api.getConsultasOfertadas(idMedico) }
    }

    suspend fun getMinhasConsultaOfertada(
        id: Long
    ): Result<ConsultaOfertadaResponseDto> {
        val idMedico = requireUserId()
        return safeApiCall { api.getConsultaOfertadaById(idMedico, id) }
    }

    suspend fun createConsultaOfertada(
        dto: ConsultaOfertadaCreateRequestDto
    ): Result<ConsultaOfertadaResponseDto> {
        val idMedico = requireUserId()
        return safeApiCall { api.createConsultaOfertada(idMedico, dto) }
    }

    suspend fun updateConsultaOfertada(
        id: Long,
        dto: ConsultaOfertadaUpdateRequestDto
    ): Result<ConsultaOfertadaResponseDto>{
        val idMedico = requireUserId()
        return safeApiCall { api.updateConsultaOfertada(idMedico, id, dto) }
    }

    suspend fun deleteConsultaOfertada(
        id: Long
    ): Result<Unit> {
        val idMedico = requireUserId()
        return safeApiCall { api.deleteConsultaOfertada(idMedico, id) }
    }

    suspend fun getDisponibilidadePublica(
        idMedico: Long,
        idConsulta: Long,
        semanas: Int = 4
    ): Result<List<DisponibilidadeSemanaDTO>> =
        safeApiCall { api.getDisponibilidade(idMedico, idConsulta, semanas) }

}

