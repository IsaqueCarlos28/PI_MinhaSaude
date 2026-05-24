package com.example.medicoapplication.activities.paciente

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.data.remote.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AgendarConsultaActivity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()
    private val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    private var horarioSelecionado: String? = null
    private var medicoId: Long = -1L
    private var consultaOfertadaId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendar_consulta)

        medicoId           = intent.getLongExtra("MEDICO_ID", -1L)
        consultaOfertadaId = intent.getLongExtra("CONSULTA_OFERTADA_ID", -1L)

        val tvNome          = findViewById<TextView>(R.id.tvNomeMedicoAgendar)
        val tvEspecialidade = findViewById<TextView>(R.id.tvEspecialidadeAgendar)
        val tvCrm           = findViewById<TextView>(R.id.tvCrmAgendar)
        val tvBio           = findViewById<TextView>(R.id.tvBioMedico)
        val tvVerMais       = findViewById<TextView>(R.id.tvVerMaisHorarios)
        val btnConfirmar    = findViewById<Button>(R.id.btnConfirmarConsulta)

        // CORRIGIDO: IDs reais do XML (nao existe btnVoltarAgendar, tvDataSelecionadaAgendar
        // nem tvHorarioSelecionado no activity_agendar_consulta.xml — o layout usa navegacao
        // pelos botoes btnDataAnterior e btnProximaData para navegar entre datas)
        val btnDataAnterior = findViewById<ImageButton>(R.id.btnDataAnterior)
        val btnProximaData  = findViewById<ImageButton>(R.id.btnProximaData)

        btnDataAnterior.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            // TODO: recarregar horarios via API com a nova data
        }

        btnProximaData.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            // TODO: recarregar horarios via API com a nova data
        }

        if (medicoId != -1L) {
            carregarDadosMedico(tvNome, tvEspecialidade, tvCrm, tvBio)
        }

        // CORRIGIDO: IDs reais dos botoes de horario no XML
        // (nao existe btnHorario0800 nem btnHorario0830 — os IDs reais sao os abaixo)
        val botoesHorario = listOf(
            Pair(R.id.btnHorario0900, "09:05"),
            Pair(R.id.btnHorario0930, "09:30"),
            Pair(R.id.btnHorario1000, "11:00"),
            Pair(R.id.btnHorario1030, "11:00"),
            Pair(R.id.btnHorario1100, "11:30"),
            Pair(R.id.btnHorario1130, "09:30"),
            Pair(R.id.btnHorario1200, "12:00"),
            Pair(R.id.btnHorario1230, "12:30")
        )

        botoesHorario.forEach { (idBotao, horario) ->
            findViewById<Button>(idBotao).setOnClickListener {
                selecionarHorario(horario, botoesHorario.map { it.first })
            }
        }

        btnConfirmar.setOnClickListener {
            confirmarConsulta()
        }

        tvVerMais.setOnClickListener {
            Toast.makeText(this, "Mais horarios em breve", Toast.LENGTH_SHORT).show()
        }

        configurarBottomNav(R.id.nav_consultas)
    }

    private fun carregarDadosMedico(
        tvNome: TextView,
        tvEspecialidade: TextView,
        tvCrm: TextView,
        tvBio: TextView
    ) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getMedicoById(medicoId)
                if (response.isSuccessful) {
                    val medico = response.body()
                    medico?.let {
                        tvNome.text = it.usuario?.nome ?: "Medico"
                        tvEspecialidade.text = it.especialidades.firstOrNull()?.especialidade?.nome ?: ""
                        tvCrm.text = "CRM: ${it.crmDigitos ?: ""}/${it.crmUf ?: ""}"
                        tvBio.text = ""
                    }
                }
            } catch (e: Exception) {
                // silencioso — dados do medico ja estao visualmente no card
            }
        }
    }

    private fun selecionarHorario(horario: String, idsHorarios: List<Int>) {
        horarioSelecionado = horario

        idsHorarios.forEach { id ->
            val btn = findViewById<Button>(id)
            btn.backgroundTintList =
                android.content.res.ColorStateList.valueOf(Color.parseColor("#E2E8F0"))
            btn.setTextColor(Color.parseColor("#1E293B"))
        }

        val indexSelecionado = idsHorarios.indexOfFirst { id ->
            findViewById<Button>(id).text == horario
        }
        if (indexSelecionado != -1) {
            val btnSelecionado = findViewById<Button>(idsHorarios[indexSelecionado])
            btnSelecionado.backgroundTintList =
                android.content.res.ColorStateList.valueOf(Color.parseColor("#3B82F6"))
            btnSelecionado.setTextColor(Color.WHITE)
        }
    }

    private fun confirmarConsulta() {
        if (horarioSelecionado == null) {
            Toast.makeText(this, "Selecione um horario", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // TODO: chamar API quando o fluxo de agendar estiver completo
                // val pacienteId = intent.getLongExtra("ID_PACIENTE", -1L)
                // val dataFormatada = converterDataParaApi(formatoData.format(calendar.time))
                // val request = ConsultaCreateRequestDto(
                //     idConsultaOfertada = consultaOfertadaId,
                //     idAgenda = ...,   // obter do slot selecionado via getDisponibilidade()
                // )
                // val response = RetrofitClient.api.agendarConsulta(pacienteId, request)
                // if (response.isSuccessful) { ... }

                Toast.makeText(
                    this@AgendarConsultaActivity,
                    "Consulta agendada para ${formatoData.format(calendar.time)} as $horarioSelecionado",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(this@AgendarConsultaActivity, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavAgendar)
        bottomNav.selectedItemId = itemSelecionado

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomePacienteActivity::class.java))
                    false
                }
                R.id.nav_consultas -> {
                    startActivity(Intent(this, MinhasConsultasActivity::class.java))
                    false
                }
                R.id.nav_medicos -> {
                    startActivity(Intent(this, BuscaMedicosActivity::class.java))
                    false
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, PerfilPacienteActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }
}
