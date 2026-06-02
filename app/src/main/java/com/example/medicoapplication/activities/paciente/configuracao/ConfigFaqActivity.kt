package com.example.medicoapplication.activities.paciente.configuracao

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.medicoapplication.R

class ConfigFaqActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_faq)
        findViewById<LinearLayout>(R.id.itemFaqDetalhe)
            .setOnClickListener { finish() }
    }
}
