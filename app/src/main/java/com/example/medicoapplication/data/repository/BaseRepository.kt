package com.example.medicoapplication.data.repository

import com.example.medicoapplication.data.local.AppDependencies
import com.example.medicoapplication.data.remote.RetrofitClient

abstract class BaseRepository {

    protected val sessionManager =
        AppDependencies.sessionManager

    protected val api = RetrofitClient.api

    protected suspend fun requireUserId(): Long {
        return sessionManager.getUserId()
            ?: throw IllegalStateException("Usuário não logado")
    }
}