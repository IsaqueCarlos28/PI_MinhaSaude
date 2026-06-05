package com.example.medicoapplication.data.local

import android.app.Application


class UsuarioApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AppDependencies.init(this)
    }
}