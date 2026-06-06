package com.example.medicoapplication.UI.activities.paciente.configuracao

import android.os.Bundle
import android.widget.LinearLayout
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity

class ConfigNotificacoesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_notificacoes)
        findViewById<LinearLayout>(R.id.itemNotificacoesDetalhe)
            .setOnClickListener { finish() }
    }
}
