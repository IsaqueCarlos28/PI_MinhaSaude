package com.example.medicoapplication.UI.activities.paciente.medicos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.activities.paciente.medicos.marcar_consulta.AgendarConsultaActivity
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.data.repository.MedicoRepository
import kotlinx.coroutines.launch

class PerfilMedicoPublicoActivity : BaseActivity() {

    private var medicoId: Long = -1L
    private var nomeMedico: String = "Médico"
    private var especialidadeMedico: String = "Clínico Geral"  // ✅ guarda especialidade

    private val repository = MedicoRepository()

    override val menuType = BottomMenuType.PACIENTE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_medico_publico)

        medicoId   = intent.getLongExtra("MEDICO_ID", -1L)
        nomeMedico = intent.getStringExtra("NOME_MEDICO") ?: "Médico"

        findViewById<ImageButton>(R.id.btnVoltarPerfilPublico).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.tvNomeMedicoPublico).text = nomeMedico

        if (medicoId != -1L) carregarMedico()

        // ✅ Botão Agendar — completo e dentro do onCreate
        // Passar para o repositorio
        findViewById<Button>(R.id.btnAgendarComEsteMedico).setOnClickListener {
            lifecycleScope.launch {
                val consultasResult = repository.getConsultasOfertadas()
                consultasResult.onSuccess { lista ->
                    // ✅ bloqueia se médico não tem consultas ofertadas
                    if (lista.isEmpty()) {
                        showToast( "Este médico não possui consultas disponíveis no momento.",)
                        return@onSuccess
                    }
                    val idConsultaOfertada = lista.first().id
                    val intent = Intent(
                        this@PerfilMedicoPublicoActivity,
                        AgendarConsultaActivity::class.java
                    ).apply {
                        putExtra("ID_MEDICO",            medicoId)
                        putExtra("NOME_MEDICO",          nomeMedico)
                        putExtra("ESPECIALIDADE",        especialidadeMedico)  // ✅ passa especialidade
                        putExtra("ID_CONSULTA_OFERTADA", idConsultaOfertada)
                    }
                    startActivity(intent)
                }.onFailure {
                    showToast("Erro ao buscar consultas do médico")
                }
            }
        }

        setupBottomNavigation(R.id.nav_medicos_paciente)
    }

    //Passar para viewModel
    private fun carregarMedico() {
        lifecycleScope.launch {
            repository.getMedico()
                .onSuccess { medico ->
                    val nome = medico.usuario?.nome ?: nomeMedico
                    nomeMedico = nome

                    findViewById<TextView>(R.id.tvNomeMedicoPublico).text = nome

                    val esp = medico.especialidades
                        .mapNotNull { it.especialidade?.nome }
                        .joinToString(", ")
                        .ifBlank { "Clínico Geral" }

                    especialidadeMedico = esp  // ✅ salva para usar no Intent

                    findViewById<TextView>(R.id.tvEspecialidadePublico).text = esp
                    findViewById<TextView>(R.id.tvEspecialidadesPublico).text = esp

                    val crm = buildString {
                        if (!medico.crmDigitos.isNullOrBlank()) append(medico.crmDigitos)
                        if (!medico.crmUf.isNullOrBlank()) append("/${medico.crmUf}")
                        if (isEmpty()) append("—")
                    }
                    findViewById<TextView>(R.id.tvCrmPublico).text = "CRM: $crm"

                    findViewById<TextView>(R.id.tvEmailPublico).text =
                        medico.usuario?.email ?: "—"

                    findViewById<TextView>(R.id.tvTelefonePublico).text =
                        medico.usuario?.telefone ?: "—"
                }
                .onFailure {
                    showToast("Erro ao carregar dados do médico")
                }
        }
    }


}