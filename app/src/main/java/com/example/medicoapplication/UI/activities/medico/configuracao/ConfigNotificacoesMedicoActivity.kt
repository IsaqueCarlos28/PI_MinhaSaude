package com.example.medicoapplication.UI.activities.medico.configuracao

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity

class ConfigNotificacoesMedicoActivity : BaseActivity() {

    private val prefs by lazy { getSharedPreferences("config_notificacoes_medico", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_notificacoes)

        findViewById<ImageButton>(R.id.btnVoltarNotificacoes).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tvDescricaoNotificacoes).text =
            "Escolha quais avisos deseja receber sobre agenda e atendimentos."
        findViewById<Switch>(R.id.swLembretesConsulta).text = "Lembretes de próximos atendimentos"
        findViewById<Switch>(R.id.swAlteracoesAgenda).text = "Novas consultas, reagendamentos e cancelamentos"

        configurarSwitch(R.id.swNotificacoesGerais, "gerais", true)
        configurarSwitch(R.id.swLembretesConsulta, "lembretes_atendimento", true)
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
