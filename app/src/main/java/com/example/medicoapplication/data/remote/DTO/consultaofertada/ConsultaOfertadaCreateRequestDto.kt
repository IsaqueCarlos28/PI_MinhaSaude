package com.example.medicoapplication.data.remote.DTO.consultaofertada

data class ConsultaOfertadaCreateRequestDto(
    val idEspecialidade: Long,
    val idLocal: Long?,
    val tipoConsulta: TipoConsulta,
    val valorConsulta: Double,
    val aceitaParticular: Boolean,
    val duracaoMinutos: Int,
    val conveniosAceitosIds: Set<Long>
)
