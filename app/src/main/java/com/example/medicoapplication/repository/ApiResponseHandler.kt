package com.example.medicoapplication.data.repository

import retrofit2.Response

suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>
): Result<T> = runCatching {

    val response = apiCall()

    if (response.isSuccessful) {
        response.body() ?: error("Resposta vazia do servidor")
    } else {
        error(
            "Erro ${response.code()}: ${response.message()}"
        )
    }
}
