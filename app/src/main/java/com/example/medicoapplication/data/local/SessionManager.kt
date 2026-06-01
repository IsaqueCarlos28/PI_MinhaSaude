package com.example.medicoapplication.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionManager(private val context: Context) {


    // (1) define as chaves — como nomes de colunas numa tabela
    private object Keys {
        val USER_ID    = longPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_ROLE  = stringPreferencesKey("user_role")
    }

    // (2) cria o DataStore — um arquivo por app, criado uma vez
    private val Context.dataStore by preferencesDataStore(name = "session")

    // (3) salva a sessão após login bem-sucedido
    suspend fun salvarSessao(id: Long, email: String, role: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_ID]    = id
            prefs[Keys.USER_EMAIL] = email
            prefs[Keys.USER_ROLE]  = role
        }
    }

    // (4) lê a sessão — retorna um Flow que emite sempre que os dados mudam
    val sessaoAtual: Flow<SessaoUsuario?> = context.dataStore.data
        .map { prefs ->
            val id = prefs[Keys.USER_ID] ?: return@map null  // (5)
            SessaoUsuario(
                id    = id,
                email = prefs[Keys.USER_EMAIL] ?: "",
                role  = prefs[Keys.USER_ROLE]  ?: ""
            )
        }

    // (6) limpa ao fazer logout
    suspend fun limparSessao() {
        context.dataStore.edit { it.clear() }
    }
}

    data class SessaoUsuario(val id: Long, val email: String, val role: String)
