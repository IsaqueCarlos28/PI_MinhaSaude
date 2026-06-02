package com.example.medicoapplication.activities.paciente.configuracao

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.medicoapplication.R

class ConfigSobreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_sobre)
        findViewById<LinearLayout>(R.id.itemSobreDetalhe)
            .setOnClickListener { finish() }
    }
}
