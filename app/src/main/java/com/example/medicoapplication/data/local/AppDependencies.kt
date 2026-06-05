package com.example.medicoapplication.data.local

import android.content.Context

object AppDependencies {

    lateinit var sessionManager: SessionManager

    fun init(context: Context) {
        sessionManager = SessionManager(context)
    }
}