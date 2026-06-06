package com.example.medicoapplication.UI.activities.paciente.configuracao

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.activities.auth_e_cadastro.LoginActivity
import com.example.medicoapplication.UI.activities.paciente.perfil.EditarPerfilPacienteActivity
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.viewmodel.auth.LogoutViewModel
import kotlinx.coroutines.launch

class ConfiguracoesPacienteActivity : BaseActivity() {

    private val logoutViewModel: LogoutViewModel by viewModels()

    override val menuType = BottomMenuType.PACIENTE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        configurarItens()
        configurarBotaoSair()
        setupBottomNavigation(R.id.nav_perfil_paciente)
    }

    private fun configurarItens() {
        findViewById<LinearLayout>(R.id.itemPerfilUsuario).setOnClickListener {
            startActivity(Intent(this, EditarPerfilPacienteActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.itemNotificacoes).setOnClickListener {
            startActivity(Intent(this, ConfigNotificacoesActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.itemIdioma).setOnClickListener {
            startActivity(Intent(this, ConfigIdiomaActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.itemFaq).setOnClickListener {
            startActivity(Intent(this, ConfigFaqActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.itemTermos).setOnClickListener {
            startActivity(Intent(this, ConfigTermosActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.itemSobreApp).setOnClickListener {
            startActivity(Intent(this, ConfigSobreActivity::class.java))
        }
    }

    private fun configurarBotaoSair() {
        val btnSair = findViewById<Button>(R.id.btnEncerrarSessao)

        lifecycleScope.launch {
            logoutViewModel.uiState.collect { state ->
                when (state) {
                    is LogoutViewModel.UiState.Loading -> btnSair.isEnabled = false
                    is LogoutViewModel.UiState.Sucesso -> {
                        startActivity(
                            Intent(this@ConfiguracoesPacienteActivity, LoginActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        )
                    }
                    else -> btnSair.isEnabled = true
                }
            }
        }

        btnSair.setOnClickListener { logoutViewModel.logout() }
    }
}
