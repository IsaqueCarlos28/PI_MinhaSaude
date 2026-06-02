package com.example.medicoapplication.activities.paciente.configuracao

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.medicoapplication.R

class ConfigTermosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_termos)
        findViewById<LinearLayout>(R.id.itemTermosDetalhe)
            .setOnClickListener { finish() }
    }
}
