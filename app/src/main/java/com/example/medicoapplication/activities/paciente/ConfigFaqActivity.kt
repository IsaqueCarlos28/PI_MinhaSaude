package com.example.medicoapplication.activities.paciente

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.medicoapplication.R

class ConfigFaqActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_faq)
        findViewById<android.widget.LinearLayout>(R.id.itemFaqDetalhe)
            .setOnClickListener { finish() }
    }
}
