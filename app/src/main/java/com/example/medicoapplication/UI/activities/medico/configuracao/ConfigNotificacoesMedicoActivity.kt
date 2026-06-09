package com.example.medicoapplication.UI.activities.medico.configuracao

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import com.example.medicoapplication.UI.activities.BaseActivity

class ConfigNotificacoesMedicoActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        montarTela("notificacoes_medico")
    }

    private fun montarTela(prefName: String) {
        val prefs = getSharedPreferences(prefName, MODE_PRIVATE)
        val scroll = ScrollView(this)
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 48, 32, 32)
        }
        scroll.addView(container)

        container.addView(TextView(this).apply {
            text = "Notificações do Médico"
            textSize = 22f
            setPadding(0, 0, 0, 10)
        })
        container.addView(TextView(this).apply {
            text = "Escolha quais avisos deseja receber sobre agenda e atendimentos. As preferências ficam salvas neste aparelho."
            textSize = 14f
            setPadding(0, 0, 0, 20)
        })

        fun addSwitch(key: String, label: String, checkedDefault: Boolean = true) {
            val sw = Switch(this).apply {
                text = label
                textSize = 15f
                isChecked = prefs.getBoolean(key, checkedDefault)
                setPadding(0, 12, 0, 12)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setOnCheckedChangeListener { _, checked ->
                    prefs.edit().putBoolean(key, checked).apply()
                }
            }
            container.addView(sw)
        }

        addSwitch("nova_consulta", "Nova consulta agendada")
        addSwitch("consulta_cancelada", "Consulta cancelada ou reagendada")
        addSwitch("proximo_atendimento", "Lembrete de próximo atendimento")
        addSwitch("agenda", "Alterações em horários ofertados", false)

        container.addView(Button(this).apply {
            text = "Voltar"
            setOnClickListener { finish() }
        })
        setContentView(scroll)
    }
}
