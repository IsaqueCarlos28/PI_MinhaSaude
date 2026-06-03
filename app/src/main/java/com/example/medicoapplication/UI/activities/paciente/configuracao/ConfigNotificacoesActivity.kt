package com.example.medicoapplication.UI.activities.paciente.configuracao

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.medicoapplication.R

class ConfigNotificacoesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_notificacoes)
        // Voltar ao toque no item (a tela é simples por enquanto)
        findViewById<LinearLayout>(R.id.itemNotificacoesDetalhe)
            .setOnClickListener { finish() }
    }
}
