package com.example.medicoapplication.UI.common.mappers

import com.example.medicoapplication.UI.common.formatters.CpfFormatter
import com.example.medicoapplication.UI.common.formatters.DateFormatter
import com.example.medicoapplication.UI.common.formatters.NameFormatter
import com.example.medicoapplication.UI.common.formatters.PhoneFormatter
import com.example.medicoapplication.data.remote.DTO.Genero
import com.example.medicoapplication.data.remote.DTO.medico.MedicoResponseDto
import com.example.medicoapplication.data.remote.DTO.paciente.PacienteResponseDto


object UiMapper {

    fun medicoApiToUi(
        medico: MedicoResponseDto
    ): MedicoResponseDto {
        return medico.copy(
            usuario= usuarioApiToUi(medico.usuario!!)
        )
    }

    fun usuarioApiToUi(
        usuario: PacienteResponseDto
    ): PacienteResponseDto {

        return usuario.copy(
            nome = NameFormatter.normalize(
                usuario.nome!!
            ),

            cpf = CpfFormatter.apiToUiCpf(
                usuario.cpf!!
            ),

            telefone = PhoneFormatter.apiToUiPhone(
                usuario.telefone!!
            ),

            dataNascimento =
                DateFormatter.apiToUiDate(
                    usuario.dataNascimento!!
                ),

        )
    }
}
