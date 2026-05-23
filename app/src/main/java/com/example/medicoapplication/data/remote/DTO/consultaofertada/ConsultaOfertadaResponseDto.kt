package com.example.medicoapplication.data.remote.DTO.consultaofertada

import com.example.medicoapplication.data.remote.DTO.convenio.ConvenioResponseDto
import com.example.medicoapplication.data.remote.DTO.especialidades.EspecialidadeResponseDto
import com.example.medicoapplication.data.remote.DTO.local.LocalResponseDto

data class ConsultaOfertadaResponseDto(
    val id: Long,
    val idMedico: Long,
    val especialidade: EspecialidadeResponseDto?,
    val tipoConsulta: TipoConsulta,
    val local: LocalResponseDto?,
    val valorConsulta: String,   // API serializes BigDecimal as String
    val aceitaParticular: Boolean,
    val duracao: String,         // API serializes Duration as String e.g. "PT30M"
    val conveniosAceitos: List<ConvenioResponseDto>
)
