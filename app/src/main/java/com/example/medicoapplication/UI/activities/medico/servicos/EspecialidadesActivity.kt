package com.example.medicoapplication.UI.activities.medico.servicos

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.adapters.EspecialidadeAdapter
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.viewmodel.medico.servicos.EspecialidadesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class EspecialidadesActivity : BaseActivity() {

    override val menuType = BottomMenuType.MEDICO

    private val viewModel: EspecialidadesViewModel by viewModels()

    private lateinit var rvEspecialidades: RecyclerView
    private lateinit var fabAdicionarEspecialidade: FloatingActionButton

    private lateinit var adapter: EspecialidadeAdapter

    private val adicionarLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                viewModel.carregarEspecialidades()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_especialidades)

        setupViews()
        setupRecyclerView()
        setupClicks()
        observeViewModel()

        viewModel.carregarEspecialidades()

        setupBottomNavigation(R.id.nav_servicos_medico)
    }

    private fun setupViews() {

        rvEspecialidades =
            findViewById(R.id.rvEspecialidades)

        fabAdicionarEspecialidade =
            findViewById(R.id.fabAdicionarEspecialidade)
    }

    private fun setupRecyclerView() {

        adapter = EspecialidadeAdapter(
            emptyList(),

            onExcluir = { especialidade ->

                viewModel.deletarEspecialidade(
                    especialidade.id
                )
            }
        )

        rvEspecialidades.layoutManager =
            LinearLayoutManager(this)

        rvEspecialidades.adapter = adapter
    }

    private fun setupClicks() {

        fabAdicionarEspecialidade.setOnClickListener {

            adicionarLauncher.launch(
                Intent(
                    this,
                    AdicionarEspecialidadeActivity::class.java
                )
            )
        }
    }

    private fun observeViewModel() {

        lifecycleScope.launch {

            viewModel.uiState.collect { state ->

                when (state) {

                    is EspecialidadesViewModel.UiState.Success -> {
                        adapter.atualizarLista(
                            state.especialidade
                        )
                    }

                    is EspecialidadesViewModel.UiState.Error -> {
                        handleError(state.error)
                    }

                    else -> Unit
                }
            }
        }
    }
}