package com.example.medicoapplication.UI.activities.medico.configuracao

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import com.example.medicoapplication.UI.activities.BaseActivity

class ConfigIdiomaMedicoActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        montarTela("idioma_medico")
    }

    private fun montarTela(prefName: String) {
        val prefs = getSharedPreferences(prefName, MODE_PRIVATE)
        val idiomaAtual = prefs.getString("idioma", "pt") ?: "pt"
        val scroll = ScrollView(this)
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 48, 32, 32)
        }
        scroll.addView(container)
        container.addView(TextView(this).apply { text = "Idioma do Médico"; textSize = 22f })
        container.addView(TextView(this).apply {
            text = "Selecione o idioma preferido para sua área médica. A preferência fica salva localmente; para tradução completa, o projeto ainda precisa de strings.xml por idioma."
            textSize = 14f
            setPadding(0, 10, 0, 20)
        })
        val grupo = RadioGroup(this).apply { orientation = RadioGroup.VERTICAL }
        val opcoes = listOf("pt" to "Português", "en" to "Inglês", "es" to "Espanhol")
        opcoes.forEachIndexed { index, (valor, texto) ->
            grupo.addView(RadioButton(this).apply {
                id = 2000 + index
                text = texto
                tag = valor
                isChecked = valor == idiomaAtual
            })
        }
        grupo.setOnCheckedChangeListener { group, checkedId ->
            val rb = group.findViewById<RadioButton>(checkedId)
            prefs.edit().putString("idioma", rb.tag.toString()).apply()
            showToast("Idioma salvo: ${rb.text}")
        }
        container.addView(grupo)
        container.addView(Button(this).apply { text = "Voltar"; setOnClickListener { finish() } })
        setContentView(scroll)
    }
}
