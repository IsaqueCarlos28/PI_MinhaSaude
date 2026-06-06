package com.example.medicoapplication.UI.activities.paciente.configuracao

import android.os.Bundle
import android.widget.LinearLayout
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity

class ConfigIdiomaActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_idioma)
        findViewById<LinearLayout>(R.id.itemIdiomaDetalhe)
            .setOnClickListener { finish() }
    }
}
