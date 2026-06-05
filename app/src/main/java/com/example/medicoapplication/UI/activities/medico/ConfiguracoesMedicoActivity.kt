package com.example.medicoapplication.UI.activities.medico

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.activities.auth_e_cadastro.LoginActivity
import com.example.medicoapplication.UI.activities.auth_e_cadastro.ResetPasswordActivity
import com.example.medicoapplication.UI.activities.medico.consulta_ofertada.ConsultasOfertadasActivity
import com.example.medicoapplication.UI.activities.medico.perfil.PerfilMedicoActivity

class ConfiguracoesMedicoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes_medico)

        val nomeMedico = intent.getStringExtra("NOME_MEDICO") ?: "Médico"
        val idMedico = intent.getLongExtra("ID_MEDICO", -1L)

        // Atualizar nome na config
        findViewById<TextView>(R.id.tvNomeMedicoConfig).text = nomeMedico

        // Ir para perfil
        findViewById<LinearLayout>(R.id.itemPerfilMedicoUsuario).setOnClickListener {
            startActivity(Intent(this, PerfilMedicoActivity::class.java).apply {
                putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
            })
        }


        // Consultas Ofertadas
        findViewById<LinearLayout>(R.id.itemConsultasOfertadas).setOnClickListener {
            startActivity(Intent(this, ConsultasOfertadasActivity::class.java).apply {
                putExtra("NOME_MEDICO", nomeMedico); putExtra("ID_MEDICO", idMedico)
            })
        }

        // Segurança → ResetPassword
        findViewById<LinearLayout>(R.id.itemSegurancaMedico).setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        // Notificações (placeholder)
        findViewById<LinearLayout>(R.id.itemNotificacoesMedico).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Encerrar sessão
        findViewById<Button>(R.id.btnEncerrarSessaoMedico).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        setupBottomNavigation(R.id.nav_config_medico)
    }

}