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

        medicoId          = intent.getLongExtra("MEDICO_ID", -1L)
        consultaOfertadaId = intent.getLongExtra("CONSULTA_OFERTADA_ID", -1L)

        val btnVoltar           = findViewById<ImageButton>(R.id.btnVoltarAgendar)
        val tvNome              = findViewById<TextView>(R.id.tvNomeMedicoAgendar)
        val tvEspecialidade     = findViewById<TextView>(R.id.tvEspecialidadeAgendar)
        val tvCrm               = findViewById<TextView>(R.id.tvCrmAgendar)
        val tvBio               = findViewById<TextView>(R.id.tvBioMedico)
        val tvData              = findViewById<TextView>(R.id.tvDataSelecionadaAgendar)
        val tvHorario           = findViewById<TextView>(R.id.tvHorarioSelecionado)
        val btnDataAnterior     = findViewById<ImageButton>(R.id.btnDataAnterior)
        val btnProximaData      = findViewById<ImageButton>(R.id.btnProximaData)
        val btnConfirmar        = findViewById<Button>(R.id.btnConfirmarConsulta)
        val tvVerMais           = findViewById<TextView>(R.id.tvVerMaisHorarios)

        btnVoltar.setOnClickListener { finish() }

        // Atualiza data exibida
        tvData.text = formatoData.format(calendar.time)

        btnDataAnterior.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            tvData.text = formatoData.format(calendar.time)
            // TODO: recarregar horários disponíveis via API
        }

        btnProximaData.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            tvData.text = formatoData.format(calendar.time)
            // TODO: recarregar horários disponíveis via API
        }

        // Carregar dados do médico (será via API na semana que vem)
        if (medicoId != -1L) {
            carregarDadosMedico(tvNome, tvEspecialidade, tvCrm, tvBio)
        }

        // Botões de horário
        val botoesHorario = listOf(
            Pair(R.id.btnHorario0800, "08:00"),
            Pair(R.id.btnHorario0830, "08:30"),
            Pair(R.id.btnHorario0900, "09:00"),
            Pair(R.id.btnHorario0930, "09:30"),
            Pair(R.id.btnHorario1000, "10:00"),
            Pair(R.id.btnHorario1030, "10:30"),
            Pair(R.id.btnHorario1100, "11:00"),
            Pair(R.id.btnHorario1130, "11:30")
        )

        botoesHorario.forEach { (idBotao, horario) ->
            findViewById<Button>(idBotao).setOnClickListener { btn ->
                selecionarHorario(horario, botoesHorario.map { it.first }, tvHorario)
            }
        }

        btnConfirmar.setOnClickListener {
            confirmarConsulta(tvData.text.toString())
        }

        tvVerMais.setOnClickListener {
            // TODO: expandir mais horários ou rolar a tela
            Toast.makeText(this, "Mais horários em breve", Toast.LENGTH_SHORT).show()
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
                // TODO: val response = RetrofitClient.api.getMedicoById(medicoId)
                // if (response.isSuccessful) {
                //     val medico = response.body()!!
                //     tvNome.text = medico.usuario?.nome ?: "Médico"
                //     tvEspecialidade.text = medico.especialidades.firstOrNull()?.nomeEspecialidade ?: ""
                //     tvCrm.text = "CRM: ${medico.crmDigitos}/${medico.crmUf}"
                // }
            } catch (e: Exception) {
                // TODO: mostrar erro
            }
        }
    }

    private fun selecionarHorario(
        horario: String,
        idsHorarios: List<Int>,
        tvHorario: TextView
    ) {
        horarioSelecionado = horario
        tvHorario.text = "Horário selecionado: $horario"

        // Resetar todos os botões para cinza e destacar o selecionado
        idsHorarios.forEach { id ->
            val btn = findViewById<Button>(id)
            btn.backgroundTintList =
                android.content.res.ColorStateList.valueOf(Color.parseColor("#E2E8F0"))
            btn.setTextColor(Color.parseColor("#1E293B"))
        }

        // Destacar botão selecionado
        val tagBotao = idsHorarios.indexOfFirst { id ->
            val btn = findViewById<Button>(id)
            btn.text == horario
        }
        if (tagBotao != -1) {
            val btnSelecionado = findViewById<Button>(idsHorarios[tagBotao])
            btnSelecionado.backgroundTintList =
                android.content.res.ColorStateList.valueOf(Color.parseColor("#3B82F6"))
            btnSelecionado.setTextColor(Color.WHITE)
        }
    }

    private fun confirmarConsulta(data: String) {
        if (horarioSelecionado == null) {
            Toast.makeText(this, "Selecione um horário", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // TODO: chamar API na semana que vem
                // val request = ConsultaCreateRequestDto(
                //     idPaciente = pacienteId,
                //     idConsultaOfertada = consultaOfertadaId,
                //     data = data,
                //     horaInicio = horarioSelecionado!!
                // )
                // val response = RetrofitClient.api.createConsulta(request)
                // if (response.isSuccessful) {
                //     Toast.makeText(this@AgendarConsultaActivity, "Consulta agendada!", Toast.LENGTH_SHORT).show()
                //     startActivity(Intent(this@AgendarConsultaActivity, MinhasConsultasActivity::class.java))
                //     finish()
                // }

                // Simulação visual por enquanto:
                Toast.makeText(
                    this@AgendarConsultaActivity,
                    "Consulta agendada para $data às $horarioSelecionado",
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
