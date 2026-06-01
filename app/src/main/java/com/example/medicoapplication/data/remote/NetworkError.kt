package com.example.medicoapplication.data.remote

sealed class NetworkError {
    object SemConexao      : NetworkError()  // IOException — sem internet
    object Timeout         : NetworkError()  // SocketTimeoutException
    object NaoAutorizado   : NetworkError()  // 401
    object DadosInvalidos  : NetworkError()  // 422
    object Conflito        : NetworkError()  // 409
    object ErrroServidor   : NetworkError()  // 5xx
    data class Desconhecido(val mensagem: String) : NetworkError()
}