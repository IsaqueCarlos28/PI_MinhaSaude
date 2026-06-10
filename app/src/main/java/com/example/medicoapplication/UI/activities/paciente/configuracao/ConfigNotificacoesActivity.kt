package com.example.medicoapplication.UI.activities.paciente.configuracao

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Switch
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity

class ConfigNotificacoesActivity : BaseActivity() {

    private val prefs by lazy { getSharedPreferences("config_notificacoes_paciente", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_notificacoes)

        findViewById<ImageButton>(R.id.btnVoltarNotificacoes).setOnClickListener { finish() }

        configurarSwitch(R.id.swNotificacoesGerais, "gerais", true)
        configurarSwitch(R.id.swLembretesConsulta, "lembretes_consulta", true)
        configurarSwitch(R.id.swAlteracoesAgenda, "alteracoes_agenda", true)
    }

    private fun configurarSwitch(id: Int, chave: String, valorPadrao: Boolean) {
        val switch = findViewById<Switch>(id)
        switch.isChecked = prefs.getBoolean(chave, valorPadrao)
        switch.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean(chave, checked).apply()
        }
    }
}
