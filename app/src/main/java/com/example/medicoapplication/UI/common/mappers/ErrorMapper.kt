package com.example.medicoapplication.UI.common.mappers

import com.example.medicoapplication.data.remote.NetworkError

object ErrorMapper {

    fun getMessage(error: NetworkError): String {
        return when (error) {

            NetworkError.SemConexao ->
                "Sem conexão com a internet. Verifique sua rede e tente novamente."

            NetworkError.Timeout ->
                "A conexão demorou mais do que o esperado. Tente novamente."

            NetworkError.NaoAutorizado ->
                "Você não tem permissão para realizar esta operação."

            NetworkError.DadosInvalidos ->
                "Os dados informados são inválidos. Verifique as informações e tente novamente."

            NetworkError.Conflito ->
                "Não foi possível concluir a operação devido a um conflito de dados."

            NetworkError.ErrroServidor ->
                "Ocorreu um problema no servidor. Tente novamente mais tarde."

            is NetworkError.Desconhecido ->
                "Ocorreu um erro inesperado."
        }
    }
}