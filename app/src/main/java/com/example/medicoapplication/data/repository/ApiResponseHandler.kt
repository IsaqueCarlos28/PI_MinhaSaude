package com.example.medicoapplication.data.repository

import androidx.datastore.core.IOException
import com.example.medicoapplication.data.remote.NetworkError
import retrofit2.Response
import java.net.SocketTimeoutException

suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>
): Result<T> = runCatching {
    return try {
        val response = apiCall()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.success(body)
            } else {
                Result.failure(Exception(NetworkError.ErrroServidor.toString()))
            }
        } else {
            // (1) mapeia código HTTP para NetworkError
            val erro = when (response.code()) {
                401  -> NetworkError.NaoAutorizado
                409  -> NetworkError.Conflito
                422  -> NetworkError.DadosInvalidos
                in 500..599 -> NetworkError.ErrroServidor
                else -> NetworkError.Desconhecido("Erro ${response.code()}")
            }
            Result.failure(NetworkException(erro))
        }

    } catch (e: SocketTimeoutException) {  // (2)
        Result.failure(NetworkException(NetworkError.Timeout))
    } catch (e: IOException) {             // (3)
        Result.failure(NetworkException(NetworkError.SemConexao))
    } catch (e: Exception) {
        Result.failure(NetworkException(NetworkError.Desconhecido(e.message ?: "")))
    }
}

// (4) — wrapper para carregar o NetworkError dentro de uma Exception
class NetworkException(val error: NetworkError) : Exception()