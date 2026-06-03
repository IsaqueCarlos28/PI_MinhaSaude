package com.example.medicoapplication.UI.activities.paciente

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.viewmodel.paciente.consulta.DetalheConsultaViewModel
import com.example.medicoapplication.data.remote.DTO.StatusConsulta
import com.example.medicoapplication.data.remote.NetworkError
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

/**
 * Exibe os detalhes completos de uma consulta do paciente.
 * Recebe via Intent:
 *   - ID_PACIENTE (Long)
 *   - ID_EVENTO   (Long)
 */
class DetalheConsultaActivity : BaseActivity() {

    private val viewModel: DetalheConsultaViewModel by viewModels()

    private var idPaciente: Long = -1L
    private var idEvento:   Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhe_consulta)

        idPaciente = intent.getLongExtra("ID_PACIENTE", -1L)
        idEvento   = intent.getLongExtra("ID_EVENTO",   -1L)

        findViewById<ImageButton>(R.id.btnVoltarDetalhe).setOnClickListener { finish() }

        observeViewModel()

        if (idPaciente != -1L && idEvento != -1L) {
            viewModel.carregarConsulta(idPaciente, idEvento)
        } else {
            Toast.makeText(this, "Consulta não encontrada.", Toast.LENGTH_SHORT).show()
            finish()
        }

        configurarBottomNav(R.id.nav_consultas)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is DetalheConsultaViewModel.UiState.Idle    -> Unit
                    is DetalheConsultaViewModel.UiState.Loading -> Unit
                    is DetalheConsultaViewModel.UiState.Error   -> {
                        val mensagem = when (state.error) {
                            is NetworkError.NaoAutorizado ->
                                "Email ou senha incorretos. Verifique seus dados."
                            is NetworkError.SemConexao ->
                                "Sem conexão com a internet. Verifique sua rede."
                            is NetworkError.Timeout ->
                                "O servidor demorou para responder. Tente novamente."
                            is NetworkError.ErrroServidor ->
                                "Problema no servidor. Tente mais tarde."
                            is NetworkError.Desconhecido ->
                                "Erro inesperado: ${state.error.mensagem}"
                            else ->
                                "Algo deu errado. Tente novamente."
                        }
                        Toast.makeText(this@DetalheConsultaActivity, mensagem, Toast.LENGTH_SHORT).show()}
                    is DetalheConsultaViewModel.UiState.Success -> {
                        val c = state.consulta
                        findViewById<TextView>(R.id.tvDetalheMedico).text   = c.nomeMedico ?: "—"
                        findViewById<TextView>(R.id.tvDetalheData).text     = formatarData(c.data)
                        findViewById<TextView>(R.id.tvDetalheHora).text     = c.horaInicio.take(5)
                        findViewById<TextView>(R.id.tvDetalheConvenio).text = c.nomeConvenio ?: "Particular"
                        findViewById<TextView>(R.id.tvDetalheStatus).text   = traduzirStatus(c.status)

                        val podeAcionar = c.status == StatusConsulta.AGENDADA
                        findViewById<Button>(R.id.btnReagendarDetalhe).visibility =
                            if (podeAcionar) View.VISIBLE else View.GONE
                        findViewById<Button>(R.id.btnCancelarDetalhe).visibility =
                            if (podeAcionar) View.VISIBLE else View.GONE

                        findViewById<Button>(R.id.btnReagendarDetalhe).setOnClickListener {
                            startActivity(
                                Intent(this@DetalheConsultaActivity, ReagendarConsultaActivity::class.java).apply {
                                    putExtra("ID_PACIENTE", idPaciente)
                                    putExtra("ID_EVENTO",   idEvento)
                                    putExtra("ID_MEDICO",   c.idMedico)
                                    putExtra("NOME_MEDICO", c.nomeMedico ?: "")
                                }
                            )
                        }

                        findViewById<Button>(R.id.btnCancelarDetalhe).setOnClickListener {
                            viewModel.cancelarConsulta(idPaciente, idEvento) {
                                Toast.makeText(this@DetalheConsultaActivity, "Consulta cancelada.", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun formatarData(data: String): String {
        return try {
            val p = data.split("-"); "${p[2]}/${p[1]}/${p[0]}"
        } catch (e: Exception) { data }
    }

    private fun traduzirStatus(status: StatusConsulta): String = when (status) {
        StatusConsulta.AGENDADA  -> "Agendada"
        StatusConsulta.CANCELADA -> "Cancelada"
        StatusConsulta.REALIZADA -> "Realizada"
    }

    private fun configurarBottomNav(itemSelecionado: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavDetalhe)
        bottomNav.selectedItemId = itemSelecionado
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home      -> { startActivity(Intent(this, HomePacienteActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                R.id.nav_consultas -> { startActivity(Intent(this, MinhasConsultasActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                R.id.nav_medicos   -> { startActivity(Intent(this, BuscaMedicosActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                R.id.nav_perfil    -> { startActivity(Intent(this, PerfilPacienteActivity::class.java).apply { putExtra("ID_PACIENTE", idPaciente) }); false }
                else -> false
            }
        }
    }
}
