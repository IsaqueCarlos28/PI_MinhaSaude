package com.example.medicoapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.UI.activities.auth_e_cadastro.LoginActivity
import com.example.medicoapplication.UI.activities.medico.HomeMedicoActivity
import com.example.medicoapplication.UI.activities.paciente.HomePacienteActivity
import com.example.medicoapplication.data.local.AppDependencies
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val sessionManager
        get() = AppDependencies.sessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Configuração de bordas (Edge-to-Edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referência do botão "Começar"
        val btnComecar = findViewById<Button>(R.id.btnComecar)

        // Ação de clique para ir para a LoginActivity
        btnComecar.setOnClickListener {
            verificarSessao()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Fecha a tela de entrada para não voltar nela ao clicar em 'back'
        }
    }

    fun verificarSessao() {
        lifecycleScope.launch {
            val sessao = sessionManager.sessaoAtual.firstOrNull()

            if (sessao != null) {
                val destino = when (sessao.role) {
                    "PACIENTE" -> HomePacienteActivity::class.java
                    "MEDICO"   -> HomeMedicoActivity::class.java
                    else       -> return@launch
                }
                startActivity(Intent(this@MainActivity, destino))
                finish()
            }
        }
    }
}
