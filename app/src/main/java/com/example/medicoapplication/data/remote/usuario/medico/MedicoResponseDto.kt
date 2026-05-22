package com.example.medicoapplication.data.remote.usuario.medico

import com.example.medicoapplication.data.remote.DTO.UnidadeFederativa
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeResponseDto
import com.example.medicoapplication.data.remote.DTO.medicoespecialidade.MedicoEspecialidadeResponseDto
import com.example.medicoapplication.data.remote.usuario.paciente.PacienteResponseDto
import com.google.gson.annotations.SerializedName

data class MedicoResponseDto(
    val id: Long,

    val usuario: PacienteResponseDto,

    val crmUf: UnidadeFederativa,

    val crmDigits: String,

    val especialidades: List<MedicoEspecialidadeResponseDto>
)
