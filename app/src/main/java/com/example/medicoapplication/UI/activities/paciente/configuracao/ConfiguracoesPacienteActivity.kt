package com.example.medicoapplication.UI.activities.paciente.configuracao

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.activities.auth_e_cadastro.LoginActivity
import com.example.medicoapplication.UI.activities.paciente.perfil.EditarPerfilPacienteActivity
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType

/**
 * Tela de configurações do paciente.
 * Recebe via Intent: ID_PACIENTE (Long)
 */
class ConfiguracoesPacienteActivity : BaseActivity() {

    private var idPaciente: Long = -1L

    override val menuType = BottomMenuType.PACIENTE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        idPaciente = intent.getLongExtra("ID_PACIENTE", -1L)

        configurarItens()
        configurarBotaoSair()
        setupBottomNavigation(R.id.nav_config_medico)
    }

    private fun configurarItens() {
        // Perfil → edição de perfil
        findViewById<LinearLayout>(R.id.itemPerfilUsuario).setOnClickListener {
            startActivity(
                Intent(this, EditarPerfilPacienteActivity::class.java).apply {
                    putExtra("ID_PACIENTE", idPaciente)
                }
            )
        }

        // Segurança → tela de alterar senha
//        findViewById<LinearLayout>(R.id.itemSeguranca).setOnClickListener {
//            startActivity(
//                Intent(this, AlterarSenhaPacienteActivity::class.java).apply {
//                    putExtra("ID_PACIENTE", idPaciente)
//                }
//            )
//        }

        // Notificações
        findViewById<LinearLayout>(R.id.itemNotificacoes).setOnClickListener {
            startActivity(
                Intent(this, ConfigNotificacoesActivity::class.java).apply {
                    putExtra("ID_PACIENTE", idPaciente)
                }
            )
        }

        // Idioma
        findViewById<LinearLayout>(R.id.itemIdioma).setOnClickListener {
            startActivity(
                Intent(this, ConfigIdiomaActivity::class.java).apply {
                    putExtra("ID_PACIENTE", idPaciente)
                }
            )
        }

        // FAQ
        findViewById<LinearLayout>(R.id.itemFaq).setOnClickListener {
            startActivity(
                Intent(this, ConfigFaqActivity::class.java).apply {
                    putExtra("ID_PACIENTE", idPaciente)
                }
            )
        }

        // Termos
        findViewById<LinearLayout>(R.id.itemTermos).setOnClickListener {
            startActivity(
                Intent(this, ConfigTermosActivity::class.java).apply {
                    putExtra("ID_PACIENTE", idPaciente)
                }
            )
        }

        // Sobre
        findViewById<LinearLayout>(R.id.itemSobreApp).setOnClickListener {
            startActivity(
                Intent(this, ConfigSobreActivity::class.java).apply {
                    putExtra("ID_PACIENTE", idPaciente)
                }
            )
        }
    }

    private fun configurarBotaoSair() {
        findViewById<Button>(R.id.btnEncerrarSessao).setOnClickListener {
            startActivity(
                Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        }
    }

}
