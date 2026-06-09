package com.example.medicoapplication.UI.activities.medico

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.activities.auth_e_cadastro.LoginActivity
import com.example.medicoapplication.UI.activities.auth_e_cadastro.ResetPasswordActivity
import com.example.medicoapplication.UI.activities.medico.consulta_ofertada.ConsultasOfertadasActivity
import com.example.medicoapplication.UI.activities.medico.perfil.PerfilMedicoActivity
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.viewmodel.auth.LogoutViewModel
import com.example.medicoapplication.viewmodel.medico.perfil.PerfilMedicoViewModel
import kotlinx.coroutines.launch

class ConfiguracoesMedicoActivity : BaseActivity() {

    private val perfilViewModel: PerfilMedicoViewModel by viewModels()
    private val logoutViewModel: LogoutViewModel by viewModels()

    override val menuType = BottomMenuType.MEDICO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes_medico)

        observeViewModels()
        perfilViewModel.carregarPerfil()

        findViewById<LinearLayout>(R.id.itemPerfilMedicoUsuario).setOnClickListener {
            startActivity(Intent(this, PerfilMedicoActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.itemSegurancaMedico).setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.itemNotificacoesMedico).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        val btnSair = findViewById<Button>(R.id.btnEncerrarSessaoMedico)
        btnSair.setOnClickListener { logoutViewModel.logout() }

        setupBottomNavigation(R.id.nav_config_medico)
    }

    private fun observeViewModels() {
        lifecycleScope.launch {
            perfilViewModel.uiState.collect { state ->
                if (state is PerfilMedicoViewModel.UiState.Success) {
                    val nome = state.medico.usuario?.nome ?: return@collect
                    findViewById<TextView>(R.id.tvNomeMedicoConfig).text = nome
                }
            }
        }

        val btnSair = lazy { findViewById<Button>(R.id.btnEncerrarSessaoMedico) }
        lifecycleScope.launch {
            logoutViewModel.uiState.collect { state ->
                when (state) {
                    is LogoutViewModel.UiState.Loading -> btnSair.value.isEnabled = false
                    is LogoutViewModel.UiState.Sucesso -> {
                        startActivity(
                            Intent(this@ConfiguracoesMedicoActivity, LoginActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        )
                    }
                    else -> btnSair.value.isEnabled = true
                }
            }
        }
    }
}
