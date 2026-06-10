package com.example.medicoapplication.UI.activities.medico.configuracao

import android.os.Bundle
import android.widget.ImageButton
import android.widget.RadioGroup
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity

class ConfigIdiomaMedicoActivity : BaseActivity() {

    private val prefs by lazy { getSharedPreferences("config_idioma_medico", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_idioma)

        findViewById<ImageButton>(R.id.btnVoltarIdioma).setOnClickListener { finish() }

        val radioGroup = findViewById<RadioGroup>(R.id.rgIdiomas)
        val idiomaSalvo = prefs.getString("idioma", "pt") ?: "pt"
        radioGroup.check(
            when (idiomaSalvo) {
                "en" -> R.id.rbIngles
                "es" -> R.id.rbEspanhol
                else -> R.id.rbPortugues
            }
        )

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val idioma = when (checkedId) {
                R.id.rbIngles -> "en"
                R.id.rbEspanhol -> "es"
                else -> "pt"
            }
            prefs.edit().putString("idioma", idioma).apply()
        }
    }
}
