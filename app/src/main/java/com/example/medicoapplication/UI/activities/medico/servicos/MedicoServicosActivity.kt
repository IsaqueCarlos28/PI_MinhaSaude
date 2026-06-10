package com.example.medicoapplication.UI.activities.medico.servicos

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicoapplication.R
import com.example.medicoapplication.UI.activities.BaseActivity
import com.example.medicoapplication.UI.adapters.ConsultaOfertadaPreviewAdapter
import com.example.medicoapplication.UI.adapters.EspecialidadePreviewAdapter
import com.example.medicoapplication.UI.common.components.bottom_nav.BottomMenuType
import com.example.medicoapplication.viewmodel.medico.servicos.MedicoServicosViewModel
import kotlinx.coroutines.launch

class MedicoServicosActivity : BaseActivity() {

    override val menuType = BottomMenuType.MEDICO

    private val viewModel: MedicoServicosViewModel by viewModels()

    private lateinit var rvEspecialidadesGrid: RecyclerView
    private lateinit var rvConsultasPreview: RecyclerView

    private lateinit var especialidadeAdapter: EspecialidadePreviewAdapter
    private lateinit var consultaAdapter: ConsultaOfertadaPreviewAdapter

    private lateinit var btnVerTodasEspecialidades: TextView
    private lateinit var btnVerTodasConsultas: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_medico_servicos)

        setupViews()
        setupRecyclerViews()
        setupClicks()
        observeViewModel()

        viewModel.carregarDados()

        setupBottomNavigation(R.id.nav_servicos_medico)
    }

    private fun setupViews() {

        rvEspecialidadesGrid =
            findViewById(R.id.rvEspecialidadesGrid)

        rvConsultasPreview =
            findViewById(R.id.rvConsultasPreview)

        btnVerTodasEspecialidades =
            findViewById(R.id.btnVerTodasEspecialidades)

        btnVerTodasConsultas =
            findViewById(R.id.btnVerTodasConsultas)
    }

    private fun setupRecyclerViews() {

        rvEspecialidadesGrid.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        rvConsultasPreview.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )

        especialidadeAdapter =
            EspecialidadePreviewAdapter(
                emptyList()
            ) { }

        consultaAdapter =
            ConsultaOfertadaPreviewAdapter(
                emptyList()
            ) { }

        rvEspecialidadesGrid.adapter =
            especialidadeAdapter

        rvConsultasPreview.adapter =
            consultaAdapter
    }

    private fun setupClicks() {

        btnVerTodasEspecialidades.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    EspecialidadesActivity::class.java
                )
            )
        }

        btnVerTodasConsultas.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ConsultasOfertadasActivity::class.java
                )
            )
        }
    }

    private fun observeViewModel() {

        lifecycleScope.launch {

            launch {
                viewModel.especialidades.collect {

                    especialidadeAdapter.atualizarLista(
                        it
                    )
                }
            }

            launch {
                viewModel.consultas.collect {

                    consultaAdapter.atualizarLista(
                        it
                    )
                }
            }

            launch {
                viewModel.uiState.collect { state ->

                    if (state is MedicoServicosViewModel.UiState.Error) {
                        handleError(state.error)
                    }
                }
            }
        }
    }

}