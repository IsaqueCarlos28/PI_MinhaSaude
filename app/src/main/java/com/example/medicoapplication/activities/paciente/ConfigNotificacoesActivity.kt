package com.example.medicoapplication.activities.paciente

import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.medicoapplication.R

class ConfigNotificacoesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_notificacoes)
        // Voltar ao toque no item (a tela é simples por enquanto)
        findViewById<android.widget.LinearLayout>(R.id.itemNotificacoesDetalhe)
            .setOnClickListener { finish() }
    }
}
