package com.example.medicoapplication.UI.activities.configuracao

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity

abstract class BaseConfigTextoActivity : BaseActivity() {

    protected abstract val titulo: String
    protected abstract val subtitulo: String
    protected abstract val conteudo: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_texto)

        findViewById<TextView>(R.id.tvTituloConfigTexto).text = titulo
        findViewById<TextView>(R.id.tvSubtituloConfigTexto).text = subtitulo
        findViewById<TextView>(R.id.tvConteudoConfigTexto).text = conteudo.trimIndent()

        findViewById<ImageButton>(R.id.btnVoltarConfigTexto).setOnClickListener {
            finish()
        }
    }
}
