package com.example.medicoapplication.UI.activities.medico.servicos

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.viewmodel.medico.servicos.AdicionarEspecialidadeViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AdicionarEspecialidadeActivity : BaseActivity() {

    private val viewModel: AdicionarEspecialidadeViewModel by viewModels()

    private lateinit var actEspecialidade: AutoCompleteTextView
    private lateinit var etRqe: TextInputEditText
    private lateinit var btnSalvar: MaterialButton

    private var especialidadeSelecionadaId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_adicionar_especialidade)

        initViews()
        configurarObservers()
        configurarListeners()

        viewModel.carregarEspecialidades()
    }

    private fun initViews() {

        actEspecialidade =
            findViewById(R.id.actEspecialidade)

        etRqe =
            findViewById(R.id.etRqe)

        btnSalvar =
            findViewById(R.id.btnSalvar)
    }

    private fun configurarListeners() {

        actEspecialidade.setOnItemClickListener { _, _, position, _ ->

            especialidadeSelecionadaId =
                viewModel.especialidades.value[position].id
        }

        btnSalvar.setOnClickListener {

            val especialidadeId =
                especialidadeSelecionadaId

            if (especialidadeId == null) {

                showToast("Selecione uma especialidade")

                return@setOnClickListener
            }

            val rqe =
                etRqe.text?.toString()?.trim() ?: ""

            if (rqe.isBlank()) {

                etRqe.error = "Informe o RQE"

                return@setOnClickListener
            }

            viewModel.salvar(
                especialidadeId,
                rqe
            )
        }
    }

    private fun configurarObservers() {

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {

                    viewModel.especialidades.collect { especialidades ->

                        val adapter =
                            ArrayAdapter(
                                this@AdicionarEspecialidadeActivity,
                                android.R.layout.simple_dropdown_item_1line,
                                especialidades.map { it.nome }
                            )

                        actEspecialidade.setAdapter(adapter)
                    }
                }

                launch {

                    viewModel.uiState.collect { state ->

                        when (state) {

                            is AdicionarEspecialidadeViewModel.UiState.Idle -> {}

                            is AdicionarEspecialidadeViewModel.UiState.Loading -> {
                                btnSalvar.isEnabled = false
                            }

                            is AdicionarEspecialidadeViewModel.UiState.Success -> {

                                showToast("Especialidade adicionada com sucesso")

                                setResult(RESULT_OK)

                                finish()
                            }

                            is AdicionarEspecialidadeViewModel.UiState.Error -> {

                                btnSalvar.isEnabled = true

                                handleError(state.error)
                            }
                        }
                    }
                }
            }
        }
    }
}