package com.example.medicoapplication.UI.common.mappers

import com.example.medicoapplication.UI.common.formatters.CpfFormatter
import com.example.medicoapplication.UI.common.formatters.DateFormatter
import com.example.medicoapplication.UI.common.formatters.NameFormatter
import com.example.medicoapplication.UI.common.formatters.PhoneFormatter
import com.example.medicoapplication.data.remote.DTO.medico.MedicoCreateRequestDto

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
}