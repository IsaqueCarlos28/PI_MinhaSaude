package com.example.medicoapplication.UI.activities.paciente.configuracao

import android.os.Bundle
import android.widget.LinearLayout
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity

class ConfigSobreActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_sobre)
        findViewById<LinearLayout>(R.id.itemSobreDetalhe)
            .setOnClickListener { finish() }
    }
}
