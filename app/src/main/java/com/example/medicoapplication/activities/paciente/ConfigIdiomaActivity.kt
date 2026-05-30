package com.example.medicoapplication.activities.paciente

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.medicoapplication.R

class ConfigIdiomaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_idioma)
        findViewById<android.widget.LinearLayout>(R.id.itemIdiomaDetalhe)
            .setOnClickListener { finish() }
    }
}
