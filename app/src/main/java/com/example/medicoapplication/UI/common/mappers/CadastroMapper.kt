package com.example.medicoapplication.UI.common.mappers

import com.example.medicoapplication.UI.common.formatters.CpfFormatter
import com.example.medicoapplication.UI.common.formatters.DateFormatter
import com.example.medicoapplication.UI.common.formatters.NameFormatter
import com.example.medicoapplication.UI.common.formatters.PhoneFormatter
import com.example.medicoapplication.data.remote.DTO.medico.MedicoCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteCreateRequestDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteResponseDto

object CadastroMapper {

    fun cadastroMedicoToApi(
        dto: MedicoCreateRequestDto
    ): MedicoCreateRequestDto {

        val usuario = dto.usuarioBase

        return dto.copy(
            usuarioBase = usuario.copy(
                nome = NameFormatter.normalize(usuario.nome),

                cpf = CpfFormatter.uiToApiCpf(
                    usuario.cpf
                ),

                telefone = PhoneFormatter.uiToApiPhone(
                    usuario.telefone
                ),

                dataNascimento =
                    DateFormatter.uiToApiDate(
                        usuario.dataNascimento
                    ) ?: usuario.dataNascimento
            )
        )
    }

    fun cadastroPacienteToApi(
        dto: PacienteCreateRequestDto
    ): PacienteCreateRequestDto {


        return dto.copy(
                nome = NameFormatter.normalize(dto.nome),
                cpf = CpfFormatter.uiToApiCpf(
                    dto.cpf
                ),
                telefone = PhoneFormatter.uiToApiPhone(
                    dto.telefone
                ),
                dataNascimento =
                    DateFormatter.uiToApiDate(
                        dto.dataNascimento
                    ) ?: dto.dataNascimento
        )
    }


}